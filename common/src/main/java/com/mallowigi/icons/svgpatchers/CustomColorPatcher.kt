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

import com.intellij.util.io.DigestUtil
import com.mallowigi.config.select.AtomSelectConfig
import org.w3c.dom.Element
import java.nio.charset.StandardCharsets

/** Custom Color Patcher. */
class CustomColorPatcher : SvgPatcher {

  override fun refresh() {
    // do nothing
  }

  override fun patch(svg: Element, path: String?) {
    patchIconColor(svg)
    patchFolderColor(svg)
    patchFolderIconColor(svg)
  }

  override fun priority(): Int = 101

  override fun digest(): ByteArray? {
    val hasher = DigestUtil.sha512()
    val fileAssociations = AtomSelectConfig.instance.selectedFileAssociations.ownValues()
    val folderAssociations = AtomSelectConfig.instance.selectedFolderAssociations.ownValues()

    fileAssociations.forEach {
      hasher.update(it.iconColor?.toByteArray(StandardCharsets.UTF_8))
    }

    folderAssociations.forEach {
      hasher.update(it.folderColor?.toByteArray(StandardCharsets.UTF_8))
      hasher.update(it.folderIconColor?.toByteArray(StandardCharsets.UTF_8))
    }

    return hasher.digest()
  }

  private fun patchIconColor(svg: Element) {
    val attr = svg.getAttribute(SvgPatcher.ICONCOLOR) ?: return
    val matchingAssociation = AtomSelectConfig.instance.findFileAssociationByName(attr) ?: return
    val iconColor = matchingAssociation.iconColor

    svg.setAttribute(SvgPatcher.FILL, "#$iconColor")
    if (svg.getAttribute(SvgPatcher.STROKE) != "") {
      svg.setAttribute(SvgPatcher.STROKE, "#$iconColor")
    }
  }

  private fun patchFolderColor(svg: Element) {
    val attr = svg.getAttribute(SvgPatcher.FOLDERCOLOR) ?: return
    val matchingAssociation = AtomSelectConfig.instance.findFolderAssociationByName(attr) ?: return
    val folderColor = matchingAssociation.folderColor

    svg.setAttribute(SvgPatcher.FILL, "#$folderColor")
    if (svg.getAttribute(SvgPatcher.STROKE) != "") {
      svg.setAttribute(SvgPatcher.STROKE, "#$folderColor")
    }
  }

  private fun patchFolderIconColor(svg: Element) {
    val attr = svg.getAttribute(SvgPatcher.FOLDERICONCOLOR) ?: return
    val matchingAssociation = AtomSelectConfig.instance.findFolderAssociationByName(attr) ?: return
    val folderIconColor = matchingAssociation.folderIconColor

    svg.setAttribute(SvgPatcher.FILL, "#$folderIconColor")
    if (svg.getAttribute(SvgPatcher.STROKE) != "") {
      svg.setAttribute(SvgPatcher.STROKE, "#$folderIconColor")
    }
  }
}
