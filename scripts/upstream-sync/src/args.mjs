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

// Command-line parsing built on commander. Environment variables provide
// defaults so the GitHub workflow can drive the script via `env:` instead of a
// shell-built argument list.

import { Command } from 'commander';

export function parseArgs(argv) {
  const program = new Command();

  program
    .name('check-upstream')
    .description('Report upstream icon requests not yet ported into the association XMLs.')
    .option('--write-state', 'persist new / updated records back to state.json', false)
    .option('--report <path>', 'write the Markdown report to a file (also printed to stdout)', process.env.REPORT_PATH)
    .option('--include-closed', 'also scan closed/merged upstream items', process.env.INCLUDE_CLOSED === 'true')
    .option('--limit <n>', 'only process the first n fetched items', (value) => Number(value) || 0, 0)
    .allowExcessArguments(false);

  program.parse(argv, { from: 'user' });
  const options = program.opts();

  return {
    writeState: Boolean(options.writeState),
    report: options.report ?? null,
    includeClosed: Boolean(options.includeClosed),
    limit: options.limit ?? 0,
  };
}
