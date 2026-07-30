/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.mallowigi.icons.patchers

import com.thoughtworks.xstream.annotations.XStreamAlias

/** Glyph icons patcher. */
@XStreamAlias("glyphPatcher")
class GlyphIconsPatcher : ExternalIconsPatcher() {

  /**
   * Rewrite the path to its glyph variant, then apply any PSI user override on top of it.
   *
   * The override is matched against the glyph path (e.g. `/glyphs/nodes/class.svg`), so this reproduces the previous
   * post-path-patcher behavior without relying on the internal `IconLoader.installPostPathPatcher` API.
   */
  override fun patchPath(path: String, classLoader: ClassLoader?): String? {
    val glyphPath = super.patchPath(path, classLoader) ?: return null
    return PsiIconOverrides.findOverride(glyphPath) ?: glyphPath
  }
}
