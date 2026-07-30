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

// Orchestrates a single run: parse args, build the association index, fetch
// upstream items, reconcile state and emit the report.

import { writeFileSync } from 'node:fs';
import { resolve } from 'node:path';

import { ASSOCIATION_FILES } from './config.mjs';
import { parseArgs } from './args.mjs';
import { buildAssociationIndex } from './associations.mjs';
import { fetchUpstreamItems } from './github.mjs';
import { loadState, reconcile, saveState } from './state.mjs';
import { buildReport } from './report.mjs';

export async function run(argv) {
  const args = parseArgs(argv);

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
    const reportPath = resolve(args.report);
    writeFileSync(reportPath, report, 'utf8');
    console.warn(`report written to ${reportPath}`);
  }

  if (args.writeState) {
    saveState(state);
    console.warn('state written to state.json');
  }
}
