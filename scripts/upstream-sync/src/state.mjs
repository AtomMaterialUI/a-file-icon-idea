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

// Persistent tracking state. Records are keyed by `issue-<n>` / `pr-<n>` and
// carry a human-owned `status` (pending/ported/ignored) that the reconciler
// never downgrades.

import { readFileSync, writeFileSync, existsSync } from 'node:fs';

import { STATE_PATH } from './config.mjs';
import { classify } from './classify.mjs';
import { isIconRelevant } from './github.mjs';

export function loadState() {
  if (!existsSync(STATE_PATH)) return { version: 1, updatedAt: null, items: {} };
  return JSON.parse(readFileSync(STATE_PATH, 'utf8'));
}

export function saveState(state) {
  writeFileSync(STATE_PATH, `${JSON.stringify(state, null, 2)}\n`, 'utf8');
}

export function keyFor(item) {
  const type = item.pull_request ? 'pr' : 'issue';
  return `${type}-${item.number}`;
}

// Merge freshly fetched upstream items into the state, updating advisory fields
// while preserving human-set status. Returns the set of keys seen this run.
export function reconcile(state, upstreamItems, index) {
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
