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

// Static configuration: repository coordinates, filesystem paths and the
// tokenizer stop-word list. Kept dependency-free so every other module can
// import it without side effects.

import { fileURLToPath } from 'node:url';
import { dirname, join, resolve } from 'node:path';

const __dirname = dirname(fileURLToPath(import.meta.url));

// scripts/upstream-sync
export const PACKAGE_ROOT = resolve(__dirname, '..');
// repository root (../../.. from src/)
export const REPO_ROOT = resolve(__dirname, '..', '..', '..');
export const STATE_PATH = join(PACKAGE_ROOT, 'state.json');

export const UPSTREAM_OWNER = 'material-extensions';
export const UPSTREAM_REPO = 'vscode-material-icon-theme';
export const UPSTREAM_URL = `https://github.com/${UPSTREAM_OWNER}/${UPSTREAM_REPO}`;

export const ASSOCIATION_FILES = [
  join(REPO_ROOT, 'iconGenerator', 'icon_associations.xml'),
  join(REPO_ROOT, 'iconGenerator', 'folder_associations.xml'),
];

// Words that carry no matching signal when tokenizing an item title. These are
// conventional-commit verbs and generic icon-theme vocabulary that would
// otherwise cause spurious matches against the large association index.
export const STOP_WORDS = new Set([
  'icon', 'icons', 'request', 'the', 'and', 'for', 'add', 'added', 'adds', 'new',
  'folder', 'folders', 'file', 'files', 'support', 'please', 'theme', 'material',
  'directory', 'directories', 'feat', 'fix', 'feature', 'mapping', 'alt', 'correct',
  'language', 'config', 'native', 'update', 'improve', 'with', 'from',
  'json', 'yaml', 'yml', 'toml', 'xml', 'test', 'spec', 'dark', 'light',
  'default', 'example',
]);
