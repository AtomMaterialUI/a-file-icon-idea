/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2024 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.icons.svgpatchers

/** Interface for all svg patchers. */
@Suppress("KDocMissingDocumentation")
interface SvgPatcher {

  /** Get digest of patcher properties. */
  fun digest(): LongArray

  /**
   * Patch colors
   *
   * @param attributes
   * @param path
   */
  fun patch(attributes: MutableMap<String, String>)

  /**
   * Priority in the list of patchers
   *
   * @return
   */
  fun priority(): Int = 1

  /** Refresh. */
  fun refresh()

  companion object {
    const val ICONCOLOR: String = "data-iconColor"
    const val FOLDERCOLOR: String = "data-folderColor"
    const val FOLDERICONCOLOR: String = "data-folderIconColor"
    const val STROKE: String = "stroke"
    const val FILL: String = "fill"
    const val WIDTH: String = "width"
    const val HEIGHT: String = "height"
    const val TRUE: String = "true"
    const val BIG: String = "data-big"
    const val TINT: String = "data-tint"
    const val THEMED: String = "data-themed"
    const val PX: String = "px"
  }

}
