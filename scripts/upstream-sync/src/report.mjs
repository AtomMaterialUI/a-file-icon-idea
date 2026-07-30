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

// Renders the pending, still-seen tracking records into a Markdown report
// grouped by classification.

import { UPSTREAM_URL } from './config.mjs';

const CLASSIFICATION_ORDER = { missing: 0, review: 1, present: 2 };

function pendingRecords(state, seen) {
  return Object.values(state.items)
    .filter((record) => record.status === 'pending' && seen.has(`${record.type}-${record.number}`))
    .sort((a, b) =>
      (CLASSIFICATION_ORDER[a.classification] - CLASSIFICATION_ORDER[b.classification]) || (b.number - a.number));
}

function section(lines, heading, records) {
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
}

export function buildReport(state, seen) {
  const pending = pendingRecords(state, seen);
  const missing = pending.filter((record) => record.classification === 'missing');
  const review = pending.filter((record) => record.classification === 'review');
  const present = pending.filter((record) => record.classification === 'present');

  const lines = [];
  lines.push('# Upstream icon sync report');
  lines.push('');
  lines.push(`Generated: ${state.updatedAt}`);
  lines.push('');
  lines.push(`Upstream: ${UPSTREAM_URL}`);
  lines.push('');
  lines.push(`- **${missing.length}** likely missing`);
  lines.push(`- **${review.length}** need review`);
  lines.push(`- **${present.length}** likely already present`);
  lines.push('');

  section(lines, 'Likely missing — not found in association XMLs', missing);
  section(lines, 'Needs review — ambiguous match', review);
  section(lines, 'Likely already present', present);

  lines.push('---');
  lines.push('');
  lines.push('Mark an item as handled by setting its `status` to `ported` or `ignored` in `state.json`.');
  lines.push('');

  return lines.join('\n');
}
