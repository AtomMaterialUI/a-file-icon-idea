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

// Builds a normalized token index from the association XML files. Uses
// fast-xml-parser to read every element's attributes rather than scraping raw
// text, so the index survives attribute-order and formatting changes.

import { readFileSync, existsSync } from 'node:fs';
import { basename } from 'node:path';
import { XMLParser } from 'fast-xml-parser';

import { normalize } from './classify.mjs';

const ATTRIBUTE_PREFIX = '@_';
const RELEVANT_ATTRS = new Set(['name', 'fileNames', 'folderNames', 'icon']);

const parser = new XMLParser({
  ignoreAttributes: false,
  attributeNamePrefix: ATTRIBUTE_PREFIX,
  allowBooleanAttributes: true,
});

// Depth-first walk yielding [attrName, value] for every relevant attribute
// found anywhere in the parsed tree.
function* iterateAttributes(node) {
  if (Array.isArray(node)) {
    for (const child of node) yield* iterateAttributes(child);
    return;
  }

  if (node === null || typeof node !== 'object') return;

  for (const [key, value] of Object.entries(node)) {
    if (key.startsWith(ATTRIBUTE_PREFIX)) {
      const attr = key.slice(ATTRIBUTE_PREFIX.length);
      if (RELEVANT_ATTRS.has(attr) && typeof value === 'string') yield [attr, value];
    } else {
      yield* iterateAttributes(value);
    }
  }
}

export function buildAssociationIndex(paths) {
  const index = new Set();

  const addValue = (value) => {
    const normalized = normalize(value);
    if (normalized.length >= 2) index.add(normalized);
  };

  for (const path of paths) {
    if (!existsSync(path)) continue;

    const tree = parser.parse(readFileSync(path, 'utf8'));

    for (const [attr, rawValue] of iterateAttributes(tree)) {
      if (attr === 'icon') {
        addValue(basename(rawValue).replace(/\.svg$/i, ''));
      } else if (attr === 'fileNames' || attr === 'folderNames') {
        for (const part of rawValue.split(',')) {
          const cleaned = part.trim().replace(/^[._]+/, '');
          if (cleaned) addValue(cleaned);
        }
      } else {
        addValue(rawValue);
      }
    }
  }

  return index;
}
