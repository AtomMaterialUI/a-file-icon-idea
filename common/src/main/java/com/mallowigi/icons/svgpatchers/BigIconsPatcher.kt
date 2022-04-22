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

import com.intellij.util.io.DigestUtil
import com.mallowigi.config.AtomFileIconsConfig
import org.w3c.dom.Element
import java.nio.charset.StandardCharsets

/**
 * Big icons patcher
 *
 * @constructor Create empty Big icons patcher
 */
class BigIconsPatcher : SvgPatcher {
  private var hasBigIcons = false

  override fun refresh(): Unit = refreshBigIcons()

  override fun patch(svg: Element, path: String?): Unit = patchSizes(svg)

  private fun refreshBigIcons() {
    hasBigIcons = AtomFileIconsConfig.instance.hasBigIcons
  }

  override fun priority(): Int = 97

  override fun digest(): ByteArray? {
    val hasher = DigestUtil.sha512()
    hasher.update(hasBigIcons.toString().toByteArray(StandardCharsets.UTF_8))
    return hasher.digest()
  }

  private fun patchSizes(svg: Element) {
    val isBig = svg.getAttribute(SvgPatcher.BIG)
    val customFontSize = AtomFileIconsConfig.instance.customIconSize.toString()
    val size = if (hasBigIcons) customFontSize else REGULAR

    if (isBig == SvgPatcher.TRUE) {
      svg.setAttribute(SvgPatcher.WIDTH, size)
      svg.setAttribute(SvgPatcher.HEIGHT, size)
    }
  }

  companion object {
    private const val REGULAR = "16"
  }
}
