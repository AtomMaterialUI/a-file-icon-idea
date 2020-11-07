/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2020 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.tree

import com.intellij.icons.AllIcons
import com.intellij.ide.IconProvider
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.file.PsiDirectoryFactory
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.PlatformIcons
import com.mallowigi.config.AtomFileIconsConfig.Companion.instance
import icons.MTIcons
import javax.swing.Icon

class SpecialFoldersIconProvider : IconProvider() {
  override fun getIcon(element: PsiElement, flags: Int): Icon? {
    if (!instance.isEnabledDirectories) {
      return null
    }
    val virtualFile = PsiUtilCore.getVirtualFile(element)
    val project = element.project

    return when {
      ProjectRootManager.getInstance(project).fileIndex.isExcluded(virtualFile!!) -> MTIcons.EXCLUDED
      ProjectRootsUtil.findUnloadedModuleByContentRoot(virtualFile, project) != null -> AllIcons.Modules.UnloadedModule
      ProjectRootsUtil.isModuleContentRoot(virtualFile, project) -> MTIcons.MODULE
      isValidPackage(element, project) -> PlatformIcons.PACKAGE_ICON
      ProjectRootsUtil.isInSource(virtualFile, project) -> MTIcons.SOURCE
      ProjectRootsUtil.isInTestSource(virtualFile, project) -> MTIcons.TEST
      else -> directoryIcon
    }
  }

  private fun isValidPackage(directory: PsiElement,
                             project: Project): Boolean {
    val factory = if (project.isDisposed) null else PsiDirectoryFactory.getInstance(project)
    return factory != null &&
        directory is PsiDirectory &&
        factory.isPackage(directory) &&
        factory.isValidPackageName(factory.getQualifiedName(directory, false))
  }

  companion object {
    @Volatile
    private var directory: Icon? = MTIcons.Nodes2.FolderOpen

    private val directoryIcon: Icon?
      get() {
        if (directory == null) directory = MTIcons.Nodes2.FolderOpen
        return directory
      }
  }
}