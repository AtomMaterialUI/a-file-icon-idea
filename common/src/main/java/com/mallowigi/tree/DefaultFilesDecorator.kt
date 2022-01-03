/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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
import com.intellij.util.ui.EmptyIcon
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.config.select.AtomSelectConfig
import com.mallowigi.models.VirtualFileInfo
import icons.AtomIcons

/**
 * New custom files decorator
 *
 */
class DefaultFilesDecorator : ProjectViewNodeDecorator {

  /**
   * Do nun'
   */
  override fun decorate(node: PackageDependenciesNode, cellRenderer: ColoredTreeCellRenderer): Unit = Unit

  /**
   * Update node icon
   *
   */
  override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
    val file = node.virtualFile
    val project = node.project

    if (project != null && !project.isDisposed) {
      when {
        file == null                                 -> return
        file.isDirectory                             -> return
        AtomFileIconsConfig.instance.isHideFileIcons -> hideIcon(data)
        !AtomFileIconsConfig.instance.isEnabledIcons -> return
        else                                         -> matchAssociation(file, data)
      }

    }
  }

  private fun matchAssociation(virtualFile: VirtualFile, data: PresentationData) {
    val fileInfo = VirtualFileInfo(virtualFile)
    val associations = AtomSelectConfig.instance.selectedFileAssociations

    val matchingAssociation = associations.findAssociation(fileInfo)
    if (matchingAssociation != null) {
      val iconPath = matchingAssociation.icon
      val icon = AtomIcons.loadIconWithFallback(AtomIcons.getFileIcon(iconPath), iconPath)
      data.setIcon(icon)
    }
  }

  private fun hideIcon(data: PresentationData) = data.setIcon(EmptyIcon.ICON_0)

}
