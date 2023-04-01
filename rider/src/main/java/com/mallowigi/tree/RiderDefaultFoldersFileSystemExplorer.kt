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
package com.mallowigi.tree

import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ui.EmptyIcon
import com.jetbrains.rider.projectView.views.FileSystemNodeBase
import com.jetbrains.rider.projectView.views.fileSystemExplorer.FileSystemExplorerCustomization
import com.mallowigi.config.AtomSettingsConfig
import com.mallowigi.config.select.AtomSelectConfig
import com.mallowigi.models.VirtualFileInfo
import icons.AtomIcons

/**
 * Rider Custom folders decorator.
 *
 * @param project
 * @constructor
 */
class RiderDefaultFoldersFileSystemExplorer(project: Project) : FileSystemExplorerCustomization(project) {
  /** Update node icon. */
  override fun updateNode(
    presentation: PresentationData,
    virtualFile: VirtualFile,
    node: FileSystemNodeBase,
  ) {
    super.updateNode(presentation, virtualFile, node)
    if (!project.isDisposed) {
      when {
        !virtualFile.isDirectory -> return
        AtomSettingsConfig.instance.isHideFolderIcons -> hideIcon(presentation)
        !AtomSettingsConfig.instance.isEnabledDirectories -> return
        else -> matchAssociation(virtualFile, presentation)
      }
    }
  }

  /**
   * If the file is in a folder that has an association, then set the icon to
   * the associated icon
   *
   * @param virtualFile The file that is being rendered.
   * @param data PresentationData – This is the data that will be used to
   *     display the file in the project view.
   */
  private fun matchAssociation(virtualFile: VirtualFile, data: PresentationData) {
    val fileInfo = VirtualFileInfo(virtualFile)
    val associations = AtomSelectConfig.instance.selectedFolderAssociations

    val matchingAssociation = associations.findAssociation(fileInfo)
    if (matchingAssociation != null) {
      val iconPath = matchingAssociation.icon
      val icon = AtomIcons.loadIconWithFallback(AtomIcons.getFolderIcon(iconPath), iconPath)
      data.setIcon(icon)
    }
  }

  /**
   * It takes a PresentationData object and returns a new PresentationData
   * object with the icon set to EmptyIcon.ICON_0
   *
   * @param data PresentationData – The presentation data of the message.
   */
  private fun hideIcon(data: PresentationData) = data.setIcon(EmptyIcon.ICON_0)
}
