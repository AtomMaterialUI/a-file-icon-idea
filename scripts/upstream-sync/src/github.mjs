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

// Thin wrapper over the GitHub REST API (via @octokit/rest) that fetches
// upstream issues and PRs, plus the relevance filter used to keep only
// icon-related items.
//
// We page the `issues.listForRepo` endpoint by explicit page number rather than
// relying on `Link`-header pagination: that endpoint is eventually consistent
// and was observed dropping the `next` link (or returning a short page) early,
// which silently truncated the result set. The Search API is not a viable
// alternative here because the Actions `GITHUB_TOKEN` returns restricted
// cross-repo search results. `open_issues_count` from the repo endpoint gives a
// token-agnostic expected size we can validate against.
//
// A shortfall is reported as a warning rather than a hard failure: the report is
// rendered from the persisted state (not from a single run's fetch), so a
// partial run only delays discovery of new items instead of dropping tracked
// ones.

import { Octokit } from '@octokit/rest';

import { UPSTREAM_OWNER, UPSTREAM_REPO } from './config.mjs';

const PAGE_SIZE = 100;
// Hard ceiling on pages walked, well above the current open-item count, so a
// misbehaving endpoint can never spin us into an unbounded loop.
const MAX_PAGES = 30;
const FETCH_ATTEMPTS = 3;
// Concurrent opens/closes mean the live count and the paged count rarely match
// exactly; only a larger gap indicates real truncation.
const SHORTFALL_TOLERANCE = 5;

function createClient() {
  const token = process.env.GITHUB_TOKEN || process.env.GH_TOKEN;
  return new Octokit({
    auth: token,
    userAgent: 'a-file-icon-idea-upstream-sync',
  });
}

// The repo's own open issue/PR total (includes PRs). Token-agnostic and works
// cross-repo on public repos, unlike Search. Returns null on the closed scan,
// where no single authoritative count is available.
async function expectedOpenCount(octokit, includeClosed) {
  if (includeClosed) return null;

  const { data } = await octokit.rest.repos.get({ owner: UPSTREAM_OWNER, repo: UPSTREAM_REPO });
  return data.open_issues_count;
}

// Walk pages by explicit number, stopping only on an empty page. Short but
// non-empty pages are tolerated (the endpoint returns them mid-list) so we do
// not stop early the way Link-following pagination did.
async function pageThrough(octokit, includeClosed) {
  const items = new Map();

  for (let page = 1; page <= MAX_PAGES; page++) {
    const { data } = await octokit.rest.issues.listForRepo({
      owner: UPSTREAM_OWNER,
      repo: UPSTREAM_REPO,
      state: includeClosed ? 'all' : 'open',
      per_page: PAGE_SIZE,
      page,
    });

    if (data.length === 0) break;

    for (const item of data) items.set(item.number, item);
  }

  return [...items.values()];
}

// Fetch icon-related upstream issues/PRs, retrying while the paged count falls
// short of the repo's reported open count. Never throws on an incomplete fetch:
// it warns and returns whatever was gathered, leaving the persisted state (and
// thus the report) intact.
export async function fetchUpstreamItems(includeClosed) {
  const octokit = createClient();
  const expected = await expectedOpenCount(octokit, includeClosed);

  let best = [];
  for (let attempt = 1; attempt <= FETCH_ATTEMPTS; attempt++) {
    const items = await pageThrough(octokit, includeClosed);
    if (items.length > best.length) best = items;

    if (expected === null || best.length >= expected - SHORTFALL_TOLERANCE) return best;

    console.warn(`fetch attempt ${attempt}/${FETCH_ATTEMPTS} collected ${items.length} of ~${expected} open items; retrying.`);
  }

  if (expected !== null && best.length < expected - SHORTFALL_TOLERANCE) {
    console.warn(`warning: upstream fetch incomplete (${best.length} of ~${expected} open items). Report is rendered from persisted state, so tracked items are preserved; new items may be picked up on a later run.`);
  }

  return best;
}

export function isIconRelevant(item) {
  const labels = (item.labels || []).map((label) => (typeof label === 'string' ? label : label.name || '').toLowerCase());
  const title = (item.title || '').toLowerCase();

  if (labels.includes('icon request') || labels.includes('icons')) return true;
  return title.includes('[icon request]') || title.includes('icon request');
}
