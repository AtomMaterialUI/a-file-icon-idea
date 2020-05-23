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
package com.mallowigi.icons

import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilCore
import com.mallowigi.config.AtomFileIconsConfig.Companion.instance
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.AssociationsFactory
import icons.MTIcons
import javax.swing.Icon

/**
 * Provider for file icons
 */
class FileIconProvider : IconProvider(), DumbAware {
  override fun getIcon(element: PsiElement, flags: Int): Icon? {
    var icon: Icon? = null
    if (element is PsiDirectory) {
      icon = getDirectoryIcon(element)
    }
    else if (element is PsiFile) {
      icon = getFileIcon(element)
    }
    return icon
  }

  companion object {
    val associations = AssociationsFactory.create("/iconGenerator/icon_associations.xml")
    val dirAssociations = AssociationsFactory.create("/iconGenerator/folder_associations.xml")

    private fun getFileIcon(psiElement: PsiElement): Icon? {
      var icon: Icon? = null
      if (!instance.isEnabledIcons) {
        return null
      }
      val virtualFile = PsiUtilCore.getVirtualFile(psiElement)
      if (virtualFile != null) {
        val file: FileInfo = VirtualFileInfo(psiElement, virtualFile)
        val associationForFile = associations.findAssociation(file)
        icon = getIconForAssociation(file, associationForFile)
      }
      return icon
    }

    private fun getDirectoryIcon(psiElement: PsiElement): DirIcon? {
      var icon: DirIcon? = null
      if (!instance.isEnabledDirectories) {
        return null
      }
      val virtualFile = PsiUtilCore.getVirtualFile(psiElement)
      if (virtualFile != null) {
        val file: FileInfo = VirtualFileInfo(psiElement, virtualFile)
        icon = getDirectoryIconForAssociation(dirAssociations.findAssociation(file))
      }
      return icon
    }

    private fun getIconForAssociation(file: FileInfo,
                                      association: Association?): Icon? {
      val isInputInvalid = association == null || association.icon == null
      return if (isInputInvalid) null else loadIcon(file, association)
    }

    private fun getDirectoryIconForAssociation(association: Association?): DirIcon? {
      val isInputInvalid = association == null || association.icon == null
      return if (isInputInvalid) null else loadDirIcon(association)
    }

    /**
     * Load the association's icon
     */
    private fun loadIcon(file: FileInfo,
                         association: Association?): Icon? {
      var icon: Icon? = null
      try {
        val iconPath = association!!.icon
        icon = MTIcons.getFileIcon(iconPath)
      }
      catch (e: RuntimeException) {
        e.printStackTrace()
      }
      return icon
    }

    /**
     * Load the association's icon
     */
    private fun loadDirIcon(association: Association?): DirIcon? {
      var icon: DirIcon? = null
      try {
        val iconPath = association!!.icon
        icon = MTIcons.getFolderIcon(iconPath)
      }
      catch (e: RuntimeException) {
        e.printStackTrace()
      }
      return icon
    }
  }
}