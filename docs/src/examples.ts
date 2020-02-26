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

import {Logger} from './logger';
import {YargsParser} from './yargsParser';
import path from 'path';
import * as fs from 'fs';
import {ExampleGenerator} from './exampleGenerator';

function findDirectorySync(dirName: string): string {
  let dir = path.resolve(); // will return the current path
  const root = path.parse(dir).root; // returns {root: '/', dir: the module, base: the current dir... }

  while (true) {
    let lookUpDir: string;
    try {
      fs.accessSync(path.resolve(dir, dirName)); // will resolve the passed path
      lookUpDir = dirName;
    } catch (err) {
      lookUpDir = undefined;
    }

    if (lookUpDir) {
      return path.join(dir, lookUpDir); // will build the path
    } else if (dir === root) {
      return null;
    }
    dir = path.dirname(dir);
  }
}

function findFileSync(filePath: string | RegExp, rootPath?: string, results?: string[]): string[] {
  if (!!!rootPath) {
    rootPath = path.resolve();
  }
  if (!!!results) {
    results = [];
  }
  const files = fs.readdirSync(rootPath);

  for (const file of files) {
    const filename = path.join(rootPath, file);
    const stat = fs.lstatSync(filename);
    // If file already exists and is a directory, recurse
    if (stat.isDirectory()) {
      findFileSync(filePath, filename, results);
    }

    // if filePath is a regex, check match with regex
    if (filePath instanceof RegExp) {
      if (filePath.test(filename)) {
        results.push(filename);
      }
      continue;
    }
    // otherwise match with contains
    if (filename.indexOf(filePath) > -1) {
      results.push(filename);
    }
    return results;
  }
}

/**
 * Run the cli in the folder containing the associations
 */
export function main() {
  const logger = new Logger();
  const pargs = new YargsParser(logger).parse();

  const rootDir = findDirectorySync('../test');

  // Read file and icon assocs
  const baseRegex = 'src(?:(?:\\/|\\\\)[a-zA-Z0-9\\s_@\-^!#$%&+={}\\[\\]]+)*(?:\\/|\\\\)';
  // Find associations in src
  const filesPath = findFileSync(new RegExp(`${baseRegex}icon_associations\\.json`), rootDir)[0];
  const foldersPath = findFileSync(new RegExp(`${baseRegex}folder_associations\\.json`), rootDir)[0];

  try {
    const files = JSON.parse(require(filesPath)).associations.regex;
    const folders = JSON.parse(require(foldersPath)).associations.regex;

    new ExampleGenerator(pargs, files, folders, logger).generate();
    process.exit(0);
  } finally {
    process.exit(1);
  }
}
