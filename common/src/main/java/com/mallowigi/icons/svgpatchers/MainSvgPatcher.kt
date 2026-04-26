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

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.ui.svg.SvgAttributePatcher
import com.intellij.util.SVGLoader
import com.intellij.util.SVGLoader.SvgElementColorPatcherProvider
import java.util.*

/** Main svg patcher: run all registered svg patchers. */
@Suppress("UnstableApiUsage")
@Service(Service.Level.APP)
class MainSvgPatcher : SvgElementColorPatcherProvider {

  private val patchers: SortedSet<SvgPatcher> = sortedSetOf(
    compareByDescending { it.priority() },
    ThemeColorPatcher(),
    AccentColorPatcher(),
    BigIconsPatcher(),
    CustomColorPatcher(),
  )

  /** Add patcher to the OtherPatcher. */
  fun addPatcher(otherPatcher: SvgElementColorPatcherProvider) {
    patchers.add(OtherSvgPatcher(otherPatcher))
  }

  /** Call refresh colors on all patchers. */
  fun applySvgPatchers() {
    SVGLoader.colorPatcherProvider = this
    patchers.forEach { it.refresh() }
    // SwingUtilities.invokeLater { ActionToolbarImpl.updateAllToolbarsImmediately() }
  }

  /** Create patcher for path. */
  override fun attributeForPath(path: String): SvgAttributePatcher = object : SvgAttributePatcher {
    override fun patchColors(attributes: MutableMap<String, String>) {
      val useFiltered = ignoredIcons.any { path.contains(it, ignoreCase = true) }
      val effectivePatchers: Iterable<SvgPatcher> = when {
        useFiltered -> patchers.filterNot { it is BigIconsPatcher }
        else        -> patchers
      }
      effectivePatchers.forEach { it.patch(attributes) }
    }
  }

  /** Aggregates digests from all patchers into single array. */
  override fun digest(): LongArray {
    val longArrays = mutableListOf<LongArray>()
    patchers.forEach { longArrays.add(it.digest()) }
    return longArrays.flatMap { it.asIterable() }.toLongArray()
  }

  companion object {
    val instance: MainSvgPatcher
      get() = service()

    private val ignoredIcons = setOf(
      "checkBox.svg",
      "checkBoxDisabled.svg",
      "checkBoxFocused.svg",
      "checkBoxIndeterminateSelectedFocused.svg",
      "checkBoxIndeterminateSelectedDisabled.svg",
      "checkBoxIndeterminateSelected.svg",
      "checkBoxSelected.svg",
      "checkBoxSelectedDisabled.svg",
      "checkBoxSelectedFocused.svg",
      "radio.svg",
      "radioDisabled.svg",
      "radioFocused.svg",
      "radioSelected.svg",
      "radioSelectedDisabled.svg",
      "radioSelectedFocused.svg",
      "windowsMenu@20x20.svg"
    )
  }

}
