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

package com.mallowigi.icons.providers

import com.intellij.ide.IconProvider
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.changes.FilePathIconProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.Associations
import com.mallowigi.models.FileInfo
import com.mallowigi.models.IconType
import com.mallowigi.models.VirtualFileInfo
import com.mallowigi.utils.toOptional
import javax.swing.Icon

/**
 * Abstract file icon provider
 *
 * @constructor Create empty Abstract file icon provider
 */
abstract class AbstractFileIconProvider : IconProvider(), FilePathIconProvider {
  /**
   * Get the icon for the given psiElement
   * @param element The psiElement to get the icon for
   * @param flags The flags (unused)
   */
  override fun getIcon(element: PsiElement, flags: Int): Icon? {
    if (isNotApplicable()) return null

    if (isOfType(element)) return findIcon(element)
    return null
  }

  /**
   * Get the icon for the given filePath, or null if no association foun
   * @param filePath the file path
   * @param project The current project
   */
  override fun getIcon(filePath: FilePath, project: Project?): Icon? {
    if (isNotApplicable()) return null

    return findIcon(filePath)
  }

  /**
   * Find icon for a psiElement
   *
   * @param element the psi element
   * @return icon if found
   */
  private fun findIcon(element: PsiElement): Icon? {
    var icon: Icon? = null
    val virtualFile = PsiUtilCore.getVirtualFile(element)

    if (virtualFile != null) {
      val file: FileInfo = VirtualFileInfo(virtualFile)
      val association = findAssociation(file)
      icon = getIconForAssociation(association)
    }
    return icon
  }

  /**
   * Find icon for a given path
   *
   * @param filePath the filePath
   * @return icon if found
   */
  private fun findIcon(filePath: FilePath): Icon? {
    var icon: Icon? = null
    val virtualFile = filePath.virtualFile

    if (virtualFile != null) {
      val file: FileInfo = VirtualFileInfo(virtualFile)
      val association = findAssociation(file)
      icon = getIconForAssociation(association)
    }
    return icon
  }

  private fun getIconForAssociation(association: Association?): Icon? {
    return association.toOptional()
      .map { loadIcon(association) }
      .orElseGet { null }
  }

  private fun loadIcon(association: Association?): Icon? {
    var icon: Icon? = null
    try {
      val iconPath = (association ?: return null).icon
      icon = getIcon(iconPath)
    } catch (e: RuntimeException) {
      thisLogger().error(e)
    }
    return icon
  }

  private fun findAssociation(file: FileInfo): Association? = getSource().findAssociation(file)

  /**
   * Checks whether psiElement is of type (PsiFile/PsiDirectory) defined by this provider
   *
   * @param element the psi element
   * @return true if element is of type defined by this provider
   */
  abstract fun isOfType(element: PsiElement): Boolean

  /**
   * Determine whether this provider is applicable
   *
   * @return true if not applicable
   */
  abstract fun isNotApplicable(): Boolean

  /**
   * Get the source of associations
   *
   * @return the [Associations] source
   */
  abstract fun getSource(): Associations

  /**
   * Get icon of an icon path
   *
   * @param iconPath the icon path to check
   * @return icon if there is an [Association] for this path
   */
  abstract fun getIcon(iconPath: String): Icon?

  /**
   * Return the [IconType] of this provider
   *
   */
  abstract fun getType(): IconType

  /**
   * Whether this provider is for default associations
   *
   * @return true if default assoc provider
   */
  abstract fun isDefault(): Boolean
}
