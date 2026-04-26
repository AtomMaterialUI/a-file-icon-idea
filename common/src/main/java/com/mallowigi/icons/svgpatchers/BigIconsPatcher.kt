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

import com.intellij.openapi.application.ApplicationManager
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

  /** Gets the current config. */
  private val config: AtomSettingsConfig?
    get() = ApplicationManager.getApplication().getServiceIfCreated(AtomSettingsConfig::class.java)

  /** Computes hash digest of custom size and line height settings. */
  override fun digest(): LongArray {
    val entries = mutableListOf<Long>()
    // Always include line height related settings in the digest
    entries += hasCustomLineHeight.toString().toHash()
    entries += customLineHeight.toString().toHash()
    entries += hasCustomSize.toString().toHash()
    entries += customIconSize.toString().toHash()

    return entries.toLongArray()
  }

  override fun patch(attributes: MutableMap<String, String>): Unit = patchSizes(attributes)

  override fun priority(): Int = 97

  override fun refresh(): Unit = refreshSizes()

  /**
   * Modifies the size attributes of an SVG element based on specific conditions. If the width attribute is equal to "16" or "16px", it
   * updates both the width and height attributes to use either the custom icon size from the current configuration or a default size.
   *
   * @param attributes a mutable map representing the SVG attributes, where the size-related attributes like "width" and "height" may be
   *    modified.
   */
  private fun patchSizes(attributes: MutableMap<String, String>) {
    val hasWidth = attributes[SvgPatcher.WIDTH]
    val ignore = attributes[SvgPatcher.IGNORE]

    if (hasWidth == null || ignore != null) return

    if (hasWidth == "16" || hasWidth == "16px") {
      val currentConfig = config ?: return
      val customFontSize = currentConfig.customIconSize.toString()
      val hasCustomSize = currentConfig.hasCustomIconSize
      val size = if (hasCustomSize) customFontSize else DEFAULT_ICON_SIZE

      attributes[SvgPatcher.WIDTH] = size.toString() + SvgPatcher.PX
      attributes[SvgPatcher.HEIGHT] = size.toString() + SvgPatcher.PX
    }
  }

  private fun refreshSizes() {
    val currentConfig = config ?: return
    hasCustomSize = currentConfig.hasCustomIconSize
    hasCustomLineHeight = currentConfig.hasCustomLineHeight
    customIconSize = currentConfig.customIconSize
    customLineHeight = currentConfig.customLineHeight

    updateRowHeight()
  }

  /**
   * Updates the row height configuration dynamically based on custom settings or material design defaults.
   *
   * The method calculates and applies a new row height depending on several conditions:
   * 1. If custom size settings are enabled, the extra height is computed by combining the default row height, custom icon size, and a
   *    predefined minimum line height offset.
   * 2. If custom line height settings are enabled, it takes precedence over the extra height calculation.
   * 3. If a material design row height is defined in the UIManager, it is preferred over all other values.
   *
   * The computed or default row height is then updated in the `UIManager` under the `ROW_HEIGHT` key.
   */
  private fun updateRowHeight() {
    val extraHeight = if (hasCustomSize) defaultRowHeight + customIconSize - MIN_LINE_HEIGHT else null
    val customRowHeight = if (hasCustomLineHeight) customLineHeight else extraHeight
    val materialHeight = UIManager.getInt(MATERIAL_ROW_HEIGHT)

    when {
      materialHeight != 0     -> UIManager.put(ROW_HEIGHT, materialHeight)
      customRowHeight != null -> UIManager.put(ROW_HEIGHT, customRowHeight)
      else                    -> UIManager.put(ROW_HEIGHT, null)
    }
  }

  companion object {
    private const val MIN_LINE_HEIGHT = 16
    private const val DEFAULT_ICON_SIZE = 16
    private const val ROW_HEIGHT = "Tree.rowHeight"
    private const val MATERIAL_ROW_HEIGHT = "Tree.materialRowHeight"
  }

}
