/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Elior "Mallowigi" Boukhobza
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
 */
package com.mallowigi.icons.svgpatchers

import com.intellij.openapi.application.ApplicationManager
import com.intellij.ui.ColorUtil
import com.mallowigi.config.AtomSettingsConfig
import com.mallowigi.utils.toHash
import com.mallowigi.utils.toHex
import javax.swing.plaf.ColorUIResource

/** Color Patcher for themed color. */
class ThemeColorPatcher : SvgPatcher {

  private var themedColor: ColorUIResource? = null

  /** Gets the current config. */
  private val config: AtomSettingsConfig?
    get() = ApplicationManager.getApplication().getServiceIfCreated(AtomSettingsConfig::class.java)

  override fun digest(): LongArray = longArrayOf(
    (themedColor ?: getThemedColor()).toHex().toHash()
  )

  override fun patch(attributes: MutableMap<String, String>): Unit = patchTints(attributes)

  override fun priority(): Int = 98

  override fun refresh(): Unit = refreshThemeColor()

  private fun getThemedColor(): ColorUIResource {
    val hex = config?.getCurrentThemedColor() ?: DEFAULT_THEME_COLOR
    return ColorUIResource(ColorUtil.fromHex(hex))
  }

  /**
   * Patches the attributes of an SVG element to apply a themed color tint.
   *
   * Modifies the `fill` or `stroke` attributes of the SVG based on the value of the `data-themed` key in the given attributes map. If the
   * `data-themed` value is "true" or "fill", the `fill` attribute is updated to the themed color. If the value is "stroke", the `stroke`
   * attribute is updated instead.
   *
   * @param attributes a mutable map of SVG attributes where the patching will be applied. Expected to contain the key `data-themed` with
   *    values "true", "fill", or "stroke" to determine the attribute to be patched.
   */
  private fun patchTints(attributes: MutableMap<String, String>) {
    val themed = attributes[SvgPatcher.THEMED] ?: return
    val color = themedColor ?: getThemedColor()
    val newThemedColor = ColorUtil.toHex(color)

    // if data-themed="true" or themed="fill", change the fill color, or change the stroke color if "stroke"
    when (themed) {
      SvgPatcher.TRUE, SvgPatcher.FILL -> attributes[SvgPatcher.FILL] = "#$newThemedColor"
      SvgPatcher.STROKE                -> attributes[SvgPatcher.STROKE] = "#$newThemedColor"
    }
  }

  private fun refreshThemeColor() {
    themedColor = getThemedColor()
  }

  companion object {
    private const val DEFAULT_THEME_COLOR: String = "b0bec5"
  }
}