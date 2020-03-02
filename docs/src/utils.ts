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

import path from 'path';
import fs from 'fs';

/**
 * Find a directory from the current directory
 * @param dirName
 */
export function findDirectorySync(dirName: string): string {
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

/**
 * Find a file from the current directory
 * @param filePath
 * @param rootPath
 * @param results
 */
export function findFileSync(filePath: string | RegExp, rootPath?: string, results?: string[]): string[] {
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
  }
  return results;
}

/**
 * Delete a directory
 * @param dirName
 */
export function deleteDirectoryRecursively(dirName: string) {
  if (fs.existsSync(dirName)) {
    fs.readdirSync(dirName)
        .forEach(file => {
          const curPath = `${dirName}/${file}`;
          if (fs.lstatSync(curPath).isDirectory()) {
            deleteDirectoryRecursively(curPath);
          } else {
            fs.unlinkSync(curPath);
          }
        });
    fs.rmdirSync(dirName);
  }
}

/**
 * Join paths using the posix characters
 * @param paths
 */
export function pathUnixJoin(...paths: string[]) {
  return path.posix.join(...paths);
}


/**
 * Combine two arrays
 * @param array1
 * @param array2
 * @param separator
 */
export function combine(array1: any[], array2: any[], separator = '.'): any[] {
  return array1.reduce((previous: string[], current: string) =>
      previous.concat(array2.map(value => [current, value].join(separator))), []);
}

export const ROOT = 'a-file-icon-idea';