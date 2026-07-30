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

import { Octokit } from '@octokit/rest';

import { UPSTREAM_OWNER, UPSTREAM_REPO } from './config.mjs';

function createClient() {
  const token = process.env.GITHUB_TOKEN || process.env.GH_TOKEN;
  return new Octokit({
    auth: token,
    userAgent: 'a-file-icon-idea-upstream-sync',
  });
}

// Page through the issues endpoint (which also returns PRs) via octokit's
// built-in pagination. Returns open items only unless `includeClosed` is set.
export async function fetchUpstreamItems(includeClosed) {
  const octokit = createClient();

  return octokit.paginate(octokit.rest.issues.listForRepo, {
    owner: UPSTREAM_OWNER,
    repo: UPSTREAM_REPO,
    state: includeClosed ? 'all' : 'open',
    per_page: 100,
  });
}

export function isIconRelevant(item) {
  const labels = (item.labels || []).map((label) => (typeof label === 'string' ? label : label.name || '').toLowerCase());
  const title = (item.title || '').toLowerCase();

  if (labels.includes('icon request') || labels.includes('icons')) return true;
  return title.includes('[icon request]') || title.includes('icon request');
}
