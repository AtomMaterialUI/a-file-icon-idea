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

// Heuristic matching: turn an upstream item title into name tokens and decide
// whether it already appears in the association index. Advisory only.

import { STOP_WORDS } from './config.mjs';

export function normalize(value) {
  return value.toLowerCase().replace(/[^a-z0-9]+/g, '');
}

// Strip an item title down to its meaningful "name" words, discarding the
// bracketed issue prefix (`[Icon Request]:`), conventional-commit prefixes
// (`feat:`, `feat(icon):`, `fix:`) and generic theme vocabulary.
export function titleName(title) {
  return title
    .replace(/^\s*\[[^\]]*\]:?\s*/i, '')
    .replace(/^\s*\w+(\([^)]*\))?:\s*/i, '')
    .replace(/[`<>]/g, ' ')
    .replace(/\(.*?\)/g, ' ')
    .trim();
}

// Derive candidate name tokens from an upstream item title only. The body is
// intentionally ignored: it introduces far too much noise against the large
// association index and produces false "present" matches.
export function candidateTokens(title) {
  const name = titleName(title);

  const tokens = new Set();
  tokens.add(normalize(name));

  for (const word of name.split(/[^a-zA-Z0-9]+/)) {
    const normalized = normalize(word);
    if (normalized.length >= 3 && !STOP_WORDS.has(word.toLowerCase())) {
      tokens.add(normalized);
    }
  }

  tokens.delete('');
  return [...tokens];
}

export function classify(title, index) {
  const fullName = normalize(titleName(title));
  const tokens = candidateTokens(title);

  // Strongest signal: the whole name matches a known association name/file.
  if (fullName.length >= 2 && index.has(fullName)) return 'present';

  const meaningful = tokens.filter((token) => token !== fullName && token.length >= 4);
  const matched = meaningful.filter((token) => index.has(token));

  if (matched.length === 0) {
    // Nothing meaningful matched. A lone short (3-char) token is too weak to
    // trust either way, so flag it for review instead of declaring it missing.
    const shortOnly = tokens.some((token) => token.length === 3 && index.has(token));
    return shortOnly ? 'review' : 'missing';
  }

  return 'present';
}
