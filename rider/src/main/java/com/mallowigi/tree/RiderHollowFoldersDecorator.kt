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
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.PlatformIcons
import com.jetbrains.rider.projectView.views.solutionExplorer.SolutionExplorerCustomization
import com.jetbrains.rider.projectView.workspace.ProjectModelEntity
import com.jetbrains.rider.projectView.workspace.getVirtualFileAsParent
import com.jetbrains.rider.projectView.workspace.isDirectory
import com.mallowigi.config.AtomSettingsConfig
import com.mallowigi.icons.special.CustomDirIcon
import com.mallowigi.icons.special.DirIcon
import icons.AtomIcons
import java.util.*
import javax.swing.Icon

class RiderHollowFoldersDecorator(project: Project) : SolutionExplorerCustomization(project) {

  override fun updateNode(presentation: PresentationData, entity: ProjectModelEntity) {
    super.updateNode(presentation, entity)

    if (!project.isDisposed) {
      when {
        !AtomSettingsConfig.instance.isUseHollowFolders -> return
        !entity.isDirectory() -> return
        AtomSettingsConfig.instance.isHideFolderIcons -> return
        isFolderContainingOpenFiles(project, entity) -> setOpenDirectoryIcon(presentation)
      }

    }
  }

  /**
   * Set open directory icon according to the directory type
   *
   * @param data Presentation Data
   */
  private fun setOpenDirectoryIcon(data: PresentationData) {
    try {
      when {
        data.getIcon(true) is CustomDirIcon -> return
        data.getIcon(true) is DirIcon -> {
          val openedIcon: Icon = (Objects.requireNonNull(data.getIcon(true)) as DirIcon).openedIcon
          data.setIcon(DirIcon(openedIcon))
        }

        data.getIcon(false) == PlatformIcons.PACKAGE_ICON -> data.setIcon(PlatformIcons.PACKAGE_ICON)
        else -> data.setIcon(AtomIcons.Nodes2.FolderOpen)
      }
    } catch (e: Exception) {
      thisLogger().warn(e.message)
    }
  }

  private fun isFolderContainingOpenFiles(project: Project, entity: ProjectModelEntity): Boolean {
    val openFiles = FileEditorManager.getInstance(project).openFiles
    return openFiles.any { vf: VirtualFile -> vf.path.contains(entity.getVirtualFileAsParent()?.path ?: "-1") }
  }
}
