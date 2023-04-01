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
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.PlatformIcons
import com.mallowigi.config.AtomSettingsConfig
import com.mallowigi.icons.special.CustomDirIcon
import com.mallowigi.icons.special.DirIcon
import icons.AtomIcons
import java.util.*
import javax.swing.Icon

/**
 * Hollow folders' decorator: Decorate directories as "open" when one of
 * its files is open.
 */
class HollowFoldersDecorator : ProjectViewNodeDecorator {
  /** Decorate nodes with icon associations. */
  override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
    val file = node.virtualFile
    val project = node.project

    if (project != null && file != null && !project.isDisposed) {
      when {
//        AtomFileIconsConfig.instance.isLowPowerMode      -> return
        !AtomSettingsConfig.instance.isUseHollowFolders -> return
        !file.isDirectory -> return
        AtomSettingsConfig.instance.isHideFolderIcons -> return
        isFolderContainingOpenFiles(project, file) -> setOpenDirectoryIcon(data, file, project)
      }

    }
  }

  private fun isFolderContainingOpenFiles(project: Project, virtualFile: VirtualFile): Boolean {
    val openFiles = FileEditorManager.getInstance(project).openFiles
    return openFiles.any { vf: VirtualFile -> vf.path.contains(virtualFile.path) }
  }

  /**
   * Set open directory icon according to the directory type
   *
   * @param data Presentation Data
   * @param file data about the directory
   * @param project current project
   */
  private fun setOpenDirectoryIcon(data: PresentationData, file: VirtualFile, project: Project) {
    try {
      if (data.getIcon(/* open = */ true) is CustomDirIcon) return

      val icon = when {
        data.getIcon(/* open = */ true) is DirIcon -> {
          val openedIcon: Icon = (Objects.requireNonNull(data.getIcon(true)) as DirIcon).openedIcon
          DirIcon(openedIcon)
        }

        ProjectRootManager.getInstance(project).fileIndex.isExcluded(file) -> AtomIcons.EXCLUDED
        ProjectRootsUtil.isModuleContentRoot(file, project) -> AtomIcons.MODULE
        ProjectRootsUtil.isInSource(file, project) -> AtomIcons.SOURCE
        ProjectRootsUtil.isInTestSource(file, project) -> AtomIcons.TEST
        data.getIcon(/* open = */ false) == PlatformIcons.PACKAGE_ICON -> PlatformIcons.PACKAGE_ICON
        else -> directoryIcon
      }

      val layeredIcon = AtomIcons.getLayeredIcon(icon, file)
      data.setIcon(layeredIcon)
    } catch (e: Exception) {
      thisLogger().warn(e.message)
    }
  }

  companion object {
    /** Default directory icon. */
    val directoryIcon: Icon
      get() = AtomIcons.Nodes2.FolderOpen
  }

}
