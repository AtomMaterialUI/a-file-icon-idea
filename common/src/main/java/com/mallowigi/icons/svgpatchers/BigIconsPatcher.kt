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
 *
 */
package com.mallowigi.icons.svgpatchers

import com.mallowigi.config.AtomSettingsConfig
import com.mallowigi.utils.toHash
import javax.swing.UIManager

/** Big icons patcher. */
class BigIconsPatcher : SvgPatcher {

  private var customIconSize = DEFAULT_ICON_SIZE
  private var customLineHeight = DEFAULT_ICON_SIZE
  private var defaultRowHeight = UIManager.getInt(ROW_HEIGHT)
  private var hasCustomLineHeight = false
  private var hasCustomSize = false

  override fun digest(): LongArray = longArrayOf(
    hasCustomSize.toString().toHash(),
    hasCustomLineHeight.toString().toHash(),
    customIconSize.toString().toHash(),
    customLineHeight.toString().toHash(),
  )

  override fun patch(attributes: MutableMap<String, String>): Unit = patchSizes(attributes)

  override fun priority(): Int = 97

  override fun refresh(): Unit = refreshSizes()

  private fun patchSizes(attributes: MutableMap<String, String>) {
    val hasWidth = attributes[SvgPatcher.WIDTH]
    val customFontSize = AtomSettingsConfig.instance.customIconSize.toString()
    val hasCustomSize = AtomSettingsConfig.instance.hasCustomIconSize
    val size = if (hasCustomSize) customFontSize else DEFAULT_ICON_SIZE

    if (hasWidth == "16" || hasWidth == "16px") {
      attributes[SvgPatcher.WIDTH] = size.toString() + SvgPatcher.PX
      attributes[SvgPatcher.HEIGHT] = size.toString() + SvgPatcher.PX
    }
  }

  private fun refreshSizes() {
    hasCustomSize = AtomSettingsConfig.instance.hasCustomIconSize
    hasCustomLineHeight = AtomSettingsConfig.instance.hasCustomLineHeight
    customIconSize = AtomSettingsConfig.instance.customIconSize
    customLineHeight = AtomSettingsConfig.instance.customLineHeight

    updateRowHeight()
  }

  private fun updateRowHeight() {
    val extraHeight = if (hasCustomSize) defaultRowHeight + customIconSize - MIN_LINE_HEIGHT else null
    val customRowHeight = if (hasCustomLineHeight) customLineHeight else extraHeight
    val materialHeight = UIManager.getInt(MATERIAL_ROW_HEIGHT)

    if (materialHeight != 0) {
      UIManager.put(ROW_HEIGHT, materialHeight)
    } else if (customRowHeight != null) {
      UIManager.put(ROW_HEIGHT, customRowHeight)
    } else {
      UIManager.put(ROW_HEIGHT, null)
    }
  }

  companion object {
    private const val MIN_LINE_HEIGHT = 16
    private const val DEFAULT_LINE_HEIGHT = 20
    private const val DEFAULT_ICON_SIZE = 16
    private const val ROW_HEIGHT = "Tree.rowHeight"
    private const val MATERIAL_ROW_HEIGHT = "Tree.materialRowHeight"
  }

}
