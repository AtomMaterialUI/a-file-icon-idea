/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2025 Elior "Mallowigi" Boukhobza
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

// Upstream icon sync tracker.
//
// Fetches icon-related issues and PRs from the upstream
// material-extensions/vscode-material-icon-theme repository and reports which of
// them do not appear to be ported into this repo's association XML files yet.
//
// State is persisted in state.json so repeat runs only surface new / unresolved
// items. Matching is heuristic and advisory; a human confirms and flips an
// item's `status` to `ported`/`ignored` to suppress it in future reports.

import { readFileSync, writeFileSync, existsSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { dirname, join, resolve, basename } from 'node:path';

const __dirname = dirname(fileURLToPath(import.meta.url));
const REPO_ROOT = resolve(__dirname, '..', '..');
const STATE_PATH = join(__dirname, 'state.json');

const UPSTREAM_OWNER = 'material-extensions';
const UPSTREAM_REPO = 'vscode-material-icon-theme';
const API_BASE = `https://api.github.com/repos/${UPSTREAM_OWNER}/${UPSTREAM_REPO}`;

const ASSOCIATION_FILES = [
  join(REPO_ROOT, 'iconGenerator', 'icon_associations.xml'),
  join(REPO_ROOT, 'iconGenerator', 'folder_associations.xml'),
];

// Words that carry no matching signal when tokenizing an item title. These are
// conventional-commit verbs and generic icon-theme vocabulary that would
// otherwise cause spurious matches against the large association index.
const STOP_WORDS = new Set([
  'icon', 'icons', 'request', 'the', 'and', 'for', 'add', 'added', 'adds', 'new',
  'folder', 'folders', 'file', 'files', 'support', 'please', 'theme', 'material',
  'directory', 'directories', 'feat', 'fix', 'feature', 'mapping', 'alt', 'correct',
  'language', 'config', 'native', 'update', 'improve', 'with', 'from',
  'json', 'yaml', 'yml', 'toml', 'xml', 'test', 'spec', 'dark', 'light',
  'default', 'example',
]);

function parseArgs(argv) {
  const args = { writeState: false, report: null, includeClosed: false, limit: 0 };

  // Environment fallbacks let the GitHub workflow drive the script via `env:`
  // instead of shell-built argument lists.
  if (process.env.INCLUDE_CLOSED === 'true') args.includeClosed = true;
  if (process.env.REPORT_PATH) args.report = process.env.REPORT_PATH;

  for (let i = 0; i < argv.length; i++) {
    const arg = argv[i];
    if (arg === '--write-state') args.writeState = true;
    else if (arg === '--include-closed') args.includeClosed = true;
    else if (arg === '--report') args.report = argv[++i];
    else if (arg === '--limit') args.limit = Number(argv[++i]) || 0;
    else if (arg === '--help' || arg === '-h') args.help = true;
  }
  return args;
}

function normalize(value) {
  return value.toLowerCase().replace(/[^a-z0-9]+/g, '');
}

// Extract matchable tokens from XML attribute values into a normalized set.
function buildAssociationIndex(paths) {
  const index = new Set();

  const addValue = (value) => {
    const normalized = normalize(value);
    if (normalized.length >= 2) index.add(normalized);
  };

  const attrRegex = /(name|fileNames|folderNames|icon)="([^"]*)"/g;

  for (const path of paths) {
    if (!existsSync(path)) continue;
    const xml = readFileSync(path, 'utf8');

    let match;
    while ((match = attrRegex.exec(xml)) !== null) {
      const [, attr, rawValue] = match;
      if (attr === 'icon') {
        addValue(basename(rawValue).replace(/\.svg$/i, ''));
      } else if (attr === 'fileNames' || attr === 'folderNames') {
        for (const part of rawValue.split(',')) {
          const cleaned = part.trim().replace(/^[._]+/, '');
          if (cleaned) addValue(cleaned);
        }
      } else {
        addValue(rawValue);
      }
    }
  }

  return index;
}

// Strip an item title down to its meaningful "name" words, discarding the
// bracketed issue prefix (`[Icon Request]:`), conventional-commit prefixes
// (`feat:`, `feat(icon):`, `fix:`) and generic theme vocabulary.
function titleName(title) {
  return title
    .replace(/^\s*\[[^\]]*\]:?\s*/i, '')
    .replace(/^\s*\w+(\([^)]*\))?:\s*/i, '')
    .replace(/[`<>]/g, ' ')
    .replace(/\(.*?\)/g, ' ')
    .trim();
}

// Derive candidate name tokens from an upstream item title only. The body is
// intentionally ignored: it introduces far too much noise against the large
// association index and produces false "present" matches.
function candidateTokens(title) {
  const name = titleName(title);

  const tokens = new Set();
  tokens.add(normalize(name));

  for (const word of name.split(/[^a-zA-Z0-9]+/)) {
    const normalized = normalize(word);
    if (normalized.length >= 3 && !STOP_WORDS.has(word.toLowerCase())) {
      tokens.add(normalized);
    }
  }

  tokens.delete('');
  return [...tokens];
}

function classify(title, index) {
  const fullName = normalize(titleName(title));
  const tokens = candidateTokens(title);

  // Strongest signal: the whole name matches a known association name/file.
  if (fullName.length >= 2 && index.has(fullName)) return 'present';

  const meaningful = tokens.filter((token) => token !== fullName && token.length >= 4);
  const matched = meaningful.filter((token) => index.has(token));

  if (matched.length === 0) {
    // Nothing meaningful matched. A lone short (3-char) token is too weak to
    // trust either way, so flag it for review instead of declaring it missing.
    const shortOnly = tokens.some((token) => token.length === 3 && index.has(token));
    return shortOnly ? 'review' : 'missing';
  }

  return 'present';
}

async function githubFetch(url) {
  const headers = { Accept: 'application/vnd.github+json', 'User-Agent': 'a-file-icon-idea-upstream-sync' };
  const token = process.env.GITHUB_TOKEN || process.env.GH_TOKEN;
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(url, { headers });
  if (!response.ok) {
    const text = await response.text();
    throw new Error(`GitHub API ${response.status} for ${url}: ${text.slice(0, 200)}`);
  }
  return response.json();
}

// Page through the issues endpoint (which also returns PRs) and split them.
async function fetchUpstreamItems(includeClosed) {
  const state = includeClosed ? 'all' : 'open';
  const items = [];

  for (let page = 1; page <= 20; page++) {
    const url = `${API_BASE}/issues?state=${state}&per_page=100&page=${page}`;
    const batch = await githubFetch(url);
    if (!Array.isArray(batch) || batch.length === 0) break;
    items.push(...batch);
    if (batch.length < 100) break;
  }

  return items;
}

function isIconRelevant(item) {
  const labels = (item.labels || []).map((label) => (typeof label === 'string' ? label : label.name).toLowerCase());
  const title = (item.title || '').toLowerCase();

  if (labels.includes('icon request') || labels.includes('icons')) return true;
  return title.includes('[icon request]') || title.includes('icon request');
}

function loadState() {
  if (!existsSync(STATE_PATH)) return { version: 1, updatedAt: null, items: {} };
  return JSON.parse(readFileSync(STATE_PATH, 'utf8'));
}

function keyFor(item) {
  const type = item.pull_request ? 'pr' : 'issue';
  return `${type}-${item.number}`;
}

function reconcile(state, upstreamItems, index) {
  const now = new Date().toISOString();
  const seen = new Set();

  for (const item of upstreamItems) {
    if (!isIconRelevant(item)) continue;

    const key = keyFor(item);
    seen.add(key);

    const classification = classify(item.title || '', index);
    const existing = state.items[key];

    if (existing) {
      existing.title = item.title;
      existing.url = item.html_url;
      existing.classification = classification;
      existing.lastSeen = now;
      if (item.state === 'closed' && existing.status === 'pending') {
        existing.upstreamClosed = true;
      }
    } else {
      state.items[key] = {
        type: item.pull_request ? 'pr' : 'issue',
        number: item.number,
        title: item.title,
        url: item.html_url,
        status: 'pending',
        classification,
        firstSeen: now,
        lastSeen: now,
      };
    }
  }

  state.updatedAt = now;
  return { seen };
}

function buildReport(state, seen) {
  const pending = Object.values(state.items)
    .filter((record) => record.status === 'pending' && seen.has(`${record.type}-${record.number}`))
    .sort((a, b) => {
      const order = { missing: 0, review: 1, present: 2 };
      return (order[a.classification] - order[b.classification]) || (b.number - a.number);
    });

  const missing = pending.filter((record) => record.classification === 'missing');
  const review = pending.filter((record) => record.classification === 'review');
  const present = pending.filter((record) => record.classification === 'present');

  const lines = [];
  lines.push('# Upstream icon sync report');
  lines.push('');
  lines.push(`Generated: ${state.updatedAt}`);
  lines.push('');
  lines.push(`Upstream: https://github.com/${UPSTREAM_OWNER}/${UPSTREAM_REPO}`);
  lines.push('');
  lines.push(`- **${missing.length}** likely missing`);
  lines.push(`- **${review.length}** need review`);
  lines.push(`- **${present.length}** likely already present`);
  lines.push('');

  const section = (heading, records) => {
    lines.push(`## ${heading} (${records.length})`);
    lines.push('');
    if (records.length === 0) {
      lines.push('_None._');
      lines.push('');
      return;
    }
    lines.push('| Type | # | Title | Link |');
    lines.push('| --- | --- | --- | --- |');
    for (const record of records) {
      const safeTitle = record.title.replace(/\|/g, '\\|');
      lines.push(`| ${record.type} | ${record.number} | ${safeTitle} | [open](${record.url}) |`);
    }
    lines.push('');
  };

  section('Likely missing — not found in association XMLs', missing);
  section('Needs review — ambiguous match', review);
  section('Likely already present', present);

  lines.push('---');
  lines.push('');
  lines.push('Mark an item as handled by setting its `status` to `ported` or `ignored` in `state.json`.');
  lines.push('');

  return lines.join('\n');
}

async function main() {
  const args = parseArgs(process.argv.slice(2));
  if (args.help) {
    console.log('Usage: node check-upstream.mjs [--write-state] [--report <path>] [--include-closed] [--limit <n>]');
    return;
  }

  if (!process.env.GITHUB_TOKEN && !process.env.GH_TOKEN) {
    console.warn('warning: no GITHUB_TOKEN/GH_TOKEN set; using unauthenticated requests (low rate limit).');
  }

  const index = buildAssociationIndex(ASSOCIATION_FILES);
  console.warn(`indexed ${index.size} association tokens from ${ASSOCIATION_FILES.length} files`);

  let upstreamItems = await fetchUpstreamItems(args.includeClosed);
  if (args.limit > 0) upstreamItems = upstreamItems.slice(0, args.limit);
  console.warn(`fetched ${upstreamItems.length} upstream issues/PRs`);

  const state = loadState();
  const { seen } = reconcile(state, upstreamItems, index);

  const report = buildReport(state, seen);
  console.log(report);

  if (args.report) {
    writeFileSync(resolve(args.report), report, 'utf8');
    console.warn(`report written to ${args.report}`);
  }

  if (args.writeState) {
    writeFileSync(STATE_PATH, `${JSON.stringify(state, null, 2)}\n`, 'utf8');
    console.warn(`state written to ${STATE_PATH}`);
  }
}

main().catch((error) => {
  console.error(error.message || error);
  process.exit(1);
});
