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
// The issues list endpoint (`issues.listForRepo`) is eventually consistent and
// was observed returning silently truncated pages in CI, which let already
// tracked items vanish from the report. We instead drive the Search API, whose
// `total_count` gives an authoritative expected size we can assert against, so a
// truncated fetch fails loudly rather than corrupting the tracking state.

import { Octokit } from '@octokit/rest';

import { UPSTREAM_OWNER, UPSTREAM_REPO } from './config.mjs';

const SEARCH_PAGE_SIZE = 100;
// Search only exposes the first 1000 results; treat anything beyond that as
// unfetchable rather than pretending the page count is authoritative.
const SEARCH_RESULT_CAP = 1000;
const FETCH_ATTEMPTS = 3;

const REPO_QUALIFIER = `repo:${UPSTREAM_OWNER}/${UPSTREAM_REPO}`;

// The two independent signals that mark an upstream item as an icon request.
// Kept as separate queries (rather than a fragile boolean OR) so each one has
// its own authoritative `total_count` to validate against.
const RELEVANCE_QUERIES = [
  'label:"icon request",icons',
  '"icon request" in:title',
];

function createClient() {
  const token = process.env.GITHUB_TOKEN || process.env.GH_TOKEN;
  return new Octokit({
    auth: token,
    userAgent: 'a-file-icon-idea-upstream-sync',
  });
}

function keyOf(item) {
  return `${item.pull_request ? 'pr' : 'issue'}-${item.number}`;
}

// Page through a single search query, asserting the collected item count
// reconciles with the API-reported `total_count`. Throws on any shortfall so a
// truncated response can never be mistaken for an authoritative empty result.
async function searchAll(octokit, query) {
  const items = [];
  let expected = null;
  let incomplete = false;

  for (let page = 1; page <= Math.ceil(SEARCH_RESULT_CAP / SEARCH_PAGE_SIZE); page++) {
    const { data } = await octokit.rest.search.issuesAndPullRequests({
      q: query,
      per_page: SEARCH_PAGE_SIZE,
      page,
      advanced_search: 'true',
    });

    if (page === 1) {
      expected = data.total_count;
      incomplete = Boolean(data.incomplete_results);
    }

    items.push(...data.items);

    if (data.items.length < SEARCH_PAGE_SIZE) break;
    if (items.length >= SEARCH_RESULT_CAP) break;
  }

  const reachable = Math.min(expected, SEARCH_RESULT_CAP);

  if (incomplete) {
    throw new Error(`search returned incomplete_results for query "${query}" (expected ${expected})`);
  }

  if (items.length !== reachable) {
    throw new Error(`search truncated for query "${query}": collected ${items.length} of ${reachable} expected`);
  }

  if (expected > SEARCH_RESULT_CAP) {
    console.warn(`warning: query "${query}" has ${expected} results, above the ${SEARCH_RESULT_CAP} search cap; only the first ${SEARCH_RESULT_CAP} were fetched.`);
  }

  return items;
}

async function fetchOnce(octokit, includeClosed) {
  const stateQualifier = includeClosed ? '' : 'is:open ';
  const merged = new Map();

  for (const relevance of RELEVANCE_QUERIES) {
    const query = `${REPO_QUALIFIER} ${stateQualifier}${relevance}`.trim();
    for (const item of await searchAll(octokit, query)) {
      merged.set(keyOf(item), item);
    }
  }

  return [...merged.values()];
}

// Fetch icon-related upstream issues/PRs via the Search API. Retries the whole
// fetch on truncation/transient inconsistency and surfaces a hard failure if it
// never reconciles, so partial data is never written to state.
export async function fetchUpstreamItems(includeClosed) {
  const octokit = createClient();

  let lastError;
  for (let attempt = 1; attempt <= FETCH_ATTEMPTS; attempt++) {
    try {
      return await fetchOnce(octokit, includeClosed);
    } catch (error) {
      lastError = error;
      console.warn(`fetch attempt ${attempt}/${FETCH_ATTEMPTS} failed: ${error.message}`);
    }
  }

  throw new Error(`unable to fetch a complete upstream item set after ${FETCH_ATTEMPTS} attempts: ${lastError?.message}`);
}

export function isIconRelevant(item) {
  const labels = (item.labels || []).map((label) => (typeof label === 'string' ? label : label.name || '').toLowerCase());
  const title = (item.title || '').toLowerCase();

  if (labels.includes('icon request') || labels.includes('icons')) return true;
  return title.includes('[icon request]') || title.includes('icon request');
}
