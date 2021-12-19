/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Elior "Mallowigi" Boukhobza
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

import com.intellij.ui.ColorUtil
import com.mallowigi.config.AtomFileIconsConfig.Companion.instance
import org.w3c.dom.Element
import javax.swing.plaf.ColorUIResource

/**
 * Color Patcher for themed color
 *
 */
class ThemeColorPatcher : SvgPatcher {
  private var themedColor: ColorUIResource? = getThemedColor()

  override fun refresh(): Unit = refreshThemeColor()

  override fun patch(svg: Element, path: String?): Unit = patchTints(svg)

  private fun refreshThemeColor() {
    themedColor = getThemedColor()
  }

  override fun priority(): Int = 98

  private fun getThemedColor(): ColorUIResource = ColorUIResource(ColorUtil.fromHex(instance.getCurrentThemedColor()))

  private fun patchTints(svg: Element) {
    val themed = svg.getAttribute(SvgPatcher.THEMED) ?: return
    val newThemedColor = ColorUtil.toHex(themedColor!!)

    // if themed="true" or themed="fill", change the fill color, or change the stroke color if "stroke"
    if (themed == SvgPatcher.TRUE || themed == SvgPatcher.FILL) {
      svg.setAttribute(SvgPatcher.FILL, "#$newThemedColor")
    } else if (themed == SvgPatcher.STROKE) {
      svg.setAttribute(SvgPatcher.STROKE, "#$newThemedColor")
    }
  }
}
