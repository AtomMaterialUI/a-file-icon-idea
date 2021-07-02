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

package com.mallowigi.tree

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.packageDependencies.ui.PackageDependenciesNode
import com.intellij.ui.ColoredTreeCellRenderer
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.config.associations.AtomAssocConfig
import com.mallowigi.icons.providers.DefaultFileIconProvider
import com.mallowigi.icons.special.CustomFileIcon
import com.mallowigi.models.VirtualFileInfo
import icons.MTIcons

class PythonDecorator : ProjectViewNodeDecorator {
  override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
    val virtualFile = node.virtualFile ?: return
    val extensions = listOf("py", "pt", "pyx", "webpy", "pyc", "mako")

    if (!AtomFileIconsConfig.instance.isEnabledIcons) return
    if (virtualFile.extension !in extensions) return

    matchCustomAssociation(virtualFile, data)
    matchDefaultAssociation(virtualFile, data)
  }

  private fun matchDefaultAssociation(virtualFile: VirtualFile, data: PresentationData) {
    val fileInfo = VirtualFileInfo(virtualFile)
    val associations = DefaultFileIconProvider.associations

    val matchingAssociation = associations.findMatchingAssociation(fileInfo)
    if (matchingAssociation != null) {
      data.setIcon(MTIcons.getFileIcon(matchingAssociation.icon))
    }
  }

  private fun matchCustomAssociation(virtualFile: VirtualFile, data: PresentationData) {
    val fileInfo = VirtualFileInfo(virtualFile)
    val customFileAssociations = AtomAssocConfig.instance.customFileAssociations

    val matchingAssociation = customFileAssociations.findMatchingAssociation(fileInfo)
    if (matchingAssociation != null) {
      data.setIcon(CustomFileIcon(MTIcons.loadSVGIcon(matchingAssociation.icon)))
    }
  }

  override fun decorate(node: PackageDependenciesNode, cellRenderer: ColoredTreeCellRenderer) {}
}
