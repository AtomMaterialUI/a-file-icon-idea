/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Chris Magnussen and Elior Boukhobza
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
 *
 *
 */

import {Logger} from './services/logger';
import {ExamplesArgsParser} from './argsParsers/examplesArgsParser';
import {ExampleGenerator} from './exampleGenerator';
import {findDirectorySync, findFileSync} from './utils';
import {WikiArgsParser} from './argsParsers/wikiArgsParser';
import {GitClient} from './services/gitClient';
import {WikiGenerator} from './wikiGenerator';

/**
 * Run the cli in the folder containing the associations
 */
export function examples() {
  const logger = new Logger();
  // Parse arguments
  const pargs = new ExamplesArgsParser(logger).parse();

  // Find the icon association files root folder
  const rootDir = findDirectorySync('.');

  // Regexp to find the associations.json
  const baseRegex = 'docs(?:(?:\\/|\\\\)[a-zA-Z0-9\\s_@\-^!#$%&+={}\\[\\]]+)*(?:\\/|\\\\)';
  // Find associations in src
  const filesPath = findFileSync(new RegExp(`${baseRegex}icon_associations\\.json`), rootDir)[0];
  const foldersPath = findFileSync(new RegExp(`${baseRegex}folder_associations\\.json`), rootDir)[0];

  try {
    // Try to parse the json files
    const files = require(filesPath).associations.associations.regex;
    const folders = require(foldersPath).associations.associations.regex;

    // Generate the files
    new ExampleGenerator(pargs, files, folders, logger).generate();
    process.exit(0);
  } finally {
    process.exit(1);
  }
}

export async function wiki() {
  const logger = new Logger();
  // Parse arguments
  const pargs = new WikiArgsParser(logger).parse();
  const gitClient = new GitClient(pargs, logger);

  // Find the icon association files root folder
  const rootDir = findDirectorySync('.');

  // Regexp to find the associations.json
  const baseRegex = 'docs(?:(?:\\/|\\\\)[a-zA-Z0-9\\s_@\-^!#$%&+={}\\[\\]]+)*(?:\\/|\\\\)';
  // Find associations in src
  const filesPath = findFileSync(new RegExp(`${baseRegex}icon_associations\\.json`), rootDir)[0];
  const foldersPath = findFileSync(new RegExp(`${baseRegex}folder_associations\\.json`), rootDir)[0];

  // Clone or open repo
  await Promise.all([gitClient.getCodeRepository(), gitClient.getWikiRepository()]);

  try {
    // Try to parse the json files
    const files = require(filesPath).associations.associations.regex;
    const folders = require(foldersPath).associations.associations.regex;

    // Generate the files
    await new WikiGenerator(pargs, files, folders, logger, gitClient).generate();
    process.exit(0);
  } finally {
    process.exit(1);
  }
}