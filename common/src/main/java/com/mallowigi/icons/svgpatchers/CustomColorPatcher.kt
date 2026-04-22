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
import com.mallowigi.config.select.AtomSelectConfig
import com.mallowigi.utils.toHash

/** Custom Color Patcher. */
class CustomColorPatcher : SvgPatcher {

  /** Gets the current config. */
  private val config: AtomSelectConfig?
    get() = ApplicationManager.getApplication().getServiceIfCreated(AtomSelectConfig::class.java)

  /** Computes config-based hash digest of file and folder color associations. */
  override fun digest(): LongArray {
    val hasher = mutableListOf<Long>()
    val currentConfig = config ?: return longArrayOf()
    val fileAssociations = currentConfig.selectedFileAssociations.ownValues()
    val folderAssociations = currentConfig.selectedFolderAssociations.ownValues()

    fileAssociations.forEach {
      hasher.add((it.iconColor ?: "").toHash())
    }

    folderAssociations.forEach {
      hasher.add((it.folderColor ?: "").toHash())
      hasher.add((it.folderIconColor ?: "").toHash())
    }

    return hasher.toLongArray()
  }

  override fun patch(attributes: MutableMap<String, String>) {
    patchIconColor(attributes)
    patchFolderColor(attributes)
    patchFolderIconColor(attributes)
  }

  override fun priority(): Int = 101

  override fun refresh() {
    // do nothing
  }

  /** Patches folder SVG fill and stroke with associated color. */
  private fun patchFolderColor(attributes: MutableMap<String, String>) {
    val attr = attributes[SvgPatcher.FOLDERCOLOR] ?: return
    val matchingAssociation = config?.findFolderAssociationByName(attr) ?: return
    val folderColor = matchingAssociation.folderColor

    attributes[SvgPatcher.FILL] = "#$folderColor"
    if (attributes[SvgPatcher.STROKE] != null && attributes[SvgPatcher.STROKE] != "") {
      attributes[SvgPatcher.STROKE] = "#$folderColor"
    }
  }

  /** Patches folder icon fill and stroke with associated color. */
  private fun patchFolderIconColor(attributes: MutableMap<String, String>) {
    val attr = attributes[SvgPatcher.FOLDERICONCOLOR] ?: return
    val matchingAssociation = config?.findFolderAssociationByName(attr) ?: return
    val folderIconColor = matchingAssociation.folderIconColor

    attributes[SvgPatcher.FILL] = "#$folderIconColor"
    if (attributes[SvgPatcher.STROKE] != null && attributes[SvgPatcher.STROKE] != "") {
      attributes[SvgPatcher.STROKE] = "#$folderIconColor"
    }
  }

  /** Applies matching file association icon color to SVG fill and stroke. */
  private fun patchIconColor(attributes: MutableMap<String, String>) {
    val attr = attributes[SvgPatcher.ICONCOLOR] ?: return
    val matchingAssociation = config?.findFileAssociationByName(attr) ?: return
    val iconColor = matchingAssociation.iconColor

    attributes[SvgPatcher.FILL] = "#$iconColor"
    if (attributes[SvgPatcher.STROKE] != null && attributes[SvgPatcher.STROKE] != "") {
      attributes[SvgPatcher.STROKE] = "#$iconColor"
    }
  }

}