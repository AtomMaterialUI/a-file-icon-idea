/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.icons.svgpatchers

import org.w3c.dom.Element

/** Interface for all svg patchers. */
@Suppress("KDocMissingDocumentation")
interface SvgPatcher {
  /** Refresh. */
  fun refresh()

  /**
   * Patch colors
   *
   * @param svg
   * @param path
   */
  fun patch(svg: Element, path: String?)

  /**
   * Priority in the list of patchers
   *
   * @return
   */
  fun priority(): Int = 1

  /** Get digest of patcher properties. */
  fun digest(): ByteArray?

  companion object {
    const val ICONCOLOR: String = "iconColor"
    const val FOLDERCOLOR: String = "folderColor"
    const val FOLDERICONCOLOR: String = "folderIconColor"
    const val STROKE: String = "stroke"
    const val FILL: String = "fill"
    const val WIDTH: String = "width"
    const val HEIGHT: String = "height"
    const val TRUE: String = "true"
    const val BIG: String = "big"
    const val TINT: String = "tint"
    const val THEMED: String = "themed"
  }
}
