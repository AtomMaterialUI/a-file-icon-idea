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
package com.mallowigi.icons.svgpatchers

import com.intellij.ui.ColorUtil
import com.intellij.util.io.DigestUtil
import com.mallowigi.config.AtomFileIconsConfig.Companion.instance
import org.w3c.dom.Element
import java.nio.charset.StandardCharsets
import javax.swing.plaf.ColorUIResource

/**
 * Tint color patcher
 *
 * @constructor Create empty Tint color patcher
 */
class AccentColorPatcher : SvgPatcher {

  private var accentColor: ColorUIResource = getAccentColor()

  override fun digest(): ByteArray? {
    val hasher = DigestUtil.sha512()
    // order is significant
    hasher.update(ColorUtil.toHex(accentColor).toByteArray(StandardCharsets.UTF_8))
    return hasher.digest()
  }

  override fun patch(svg: Element, path: String?): Unit = patchTints(svg)

  override fun priority(): Int = 99

  override fun refresh(): Unit = refreshAccentColor()

  private fun getAccentColor(): ColorUIResource = ColorUIResource(ColorUtil.fromHex(instance.getCurrentAccentColor()))

  private fun patchTints(svg: Element) {
    val tint = svg.getAttribute(SvgPatcher.TINT) ?: return
    val newAccentColor = ColorUtil.toHex(accentColor)

    // if tint = "true" or tint = "fill", change the fill color. If tint = "stroke", change the stroke color
    if (tint == SvgPatcher.TRUE || tint == SvgPatcher.FILL) {
      svg.setAttribute(SvgPatcher.FILL, "#$newAccentColor")
    } else if (SvgPatcher.STROKE == tint) {
      svg.setAttribute(SvgPatcher.STROKE, "#$newAccentColor")
    }
  }

  private fun refreshAccentColor() {
    accentColor = getAccentColor()
  }

}
