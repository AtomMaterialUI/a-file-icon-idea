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

/** Tint color patcher. */
class AccentColorPatcher : SvgPatcher {

  /** Cached accent color. */
  private var accentColor: ColorUIResource? = null

  private val config: AtomSettingsConfig?
    get() = ApplicationManager.getApplication().getServiceIfCreated(AtomSettingsConfig::class.java)

  override fun digest(): LongArray = longArrayOf(
    (accentColor ?: getAccentColor()).toHex().toHash()
  )

  override fun patch(attributes: MutableMap<String, String>): Unit = patchTints(attributes)

  override fun priority(): Int = 99

  override fun refresh(): Unit = refreshAccentColor()

  private fun getAccentColor(): ColorUIResource {
    val hex = config?.getCurrentAccentColor() ?: DEFAULT_ACCENT_COLOR
    return ColorUIResource(ColorUtil.fromHex(hex))
  }

  /**
   * Updates the fill or stroke attributes of a given SVG element based on the specified tint property.
   *
   * @param attributes a mutable map representing the attributes of an SVG element. This should include keys for SVG-related properties,
   *    such as `fill`, `stroke`, or a custom `data-tint`. If `data-tint` is present and its value is `"true"`, `"fill"`, or `"stroke"`, the
   *    corresponding color attribute (fill or stroke) will be updated to match the current accent color.
   */
  private fun patchTints(attributes: MutableMap<String, String>) {
    val tint = attributes[SvgPatcher.TINT] ?: return
    val color = accentColor ?: getAccentColor()
    val newAccentColor = ColorUtil.toHex(color)

    // if tint = "true" or tint = "fill", change the fill color. If tint = "stroke", change the stroke color
    if (tint == SvgPatcher.TRUE || tint == SvgPatcher.FILL) {
      attributes[SvgPatcher.FILL] = "#$newAccentColor"
    } else if (SvgPatcher.STROKE == tint) {
      attributes[SvgPatcher.STROKE] = "#$newAccentColor"
    }
  }

  private fun refreshAccentColor() {
    accentColor = getAccentColor()
  }

  companion object {
    private const val DEFAULT_ACCENT_COLOR: String = "009688"
  }

}