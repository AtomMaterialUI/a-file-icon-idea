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
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.packageDependencies.ui.PackageDependenciesNode
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.util.PlatformIcons
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.icons.special.CustomDirIcon
import com.mallowigi.icons.special.DirIcon
import icons.AtomIcons
import java.util.Objects
import javax.swing.Icon

/**
 * Hollow folders' decorator: Decorate directories as "open" when one of its files is open
 *
 */
class HollowFoldersDecorator : ProjectViewNodeDecorator {

  /**
   * Decorate nodes with icon associations
   */
  override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
    val file = node.virtualFile
    val project = node.project

    if (project != null && file != null && !project.isDisposed) {
      when {
        AtomFileIconsConfig.instance.isLowPowerMode      -> return
        !AtomFileIconsConfig.instance.isUseHollowFolders -> return
        !file.isDirectory                                -> return
        AtomFileIconsConfig.instance.isHideFolderIcons   -> return
        isFolderContainingOpenFiles(project, file)       -> setOpenDirectoryIcon(data, file, project)
      }

    }
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
      when {
        data.getIcon(true) is CustomDirIcon                                -> return
        data.getIcon(true) is DirIcon                                      -> {
          val openedIcon: Icon = (Objects.requireNonNull(data.getIcon(true)) as DirIcon).openedIcon
          data.setIcon(DirIcon(openedIcon))
        }
        ProjectRootManager.getInstance(project).fileIndex.isExcluded(file) -> data.setIcon(AtomIcons.EXCLUDED)
        ProjectRootsUtil.isModuleContentRoot(file, project)                -> data.setIcon(AtomIcons.MODULE)
        ProjectRootsUtil.isInSource(file, project)                         -> data.setIcon(AtomIcons.SOURCE)
        ProjectRootsUtil.isInTestSource(file, project)                     -> data.setIcon(AtomIcons.TEST)
        data.getIcon(false) == PlatformIcons.PACKAGE_ICON                  -> data.setIcon(PlatformIcons.PACKAGE_ICON)
        else                                                               -> data.setIcon(directoryIcon)
      }
    } catch (e: Exception) {
      thisLogger().warn(e.message)
    }
  }

  private fun isFolderContainingOpenFiles(project: Project, virtualFile: VirtualFile): Boolean {
    val openFiles = FileEditorManager.getInstance(project).openFiles
    return openFiles.any { vf: VirtualFile -> vf.path.contains(virtualFile.path) }
  }

  /**
   * Do nothing
   */
  override fun decorate(node: PackageDependenciesNode, cellRenderer: ColoredTreeCellRenderer): Unit = Unit

  companion object {
    @Volatile
    private var directory: Icon? = AtomIcons.Nodes2.FolderOpen

    /**
     * Default directory icon
     */
    val directoryIcon: Icon?
      get() {
        if (directory == null) directory = AtomIcons.Nodes2.FolderOpen
        return directory
      }
  }

}