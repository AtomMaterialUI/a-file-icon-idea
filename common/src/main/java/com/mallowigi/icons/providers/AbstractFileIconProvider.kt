/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2024 Elior "Mallowigi" Boukhobza
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
 */

package com.mallowigi.icons.providers

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.Associations
import com.mallowigi.models.FileInfo
import com.mallowigi.models.IconType
import com.mallowigi.models.VirtualFileInfo
import javax.swing.Icon

/** Abstract file icon provider. */
abstract class AbstractFileIconProvider : IconProvider() {
  /**
   * Get the icon for the given psiElement
   *
   * @param element The psiElement to get the icon for
   * @param flags The flags (unused)
   */
  override fun getIcon(element: PsiElement, flags: Int): Icon? = when {
    isNotApplicable() -> null
    isOfType(element) -> findIcon(element)
    else -> null
  }

  /**
   * Find icon for a psiElement
   *
   * @param element the psi element
   * @return icon if found
   */
  private fun findIcon(element: PsiElement): Icon? {
    val virtualFile = PsiUtilCore.getVirtualFile(element)
    return virtualFile?.let {
      val file: FileInfo = VirtualFileInfo(it)
      val association = findAssociation(file)
      getIconForAssociation(association)
    }
  }

  /**
   * Get icon for association
   *
   * @param association
   * @return
   */
  private fun getIconForAssociation(association: Association?): Icon? = association?.let { loadIcon(it) }

  /**
   * Load icon
   *
   * @param association
   * @return
   */
  private fun loadIcon(association: Association): Icon? =
    CacheIconProvider.instance.iconCache.getOrPut(association.icon) { getIcon(association.icon) }

  /**
   * Find association
   *
   * @param file
   * @return
   */
  private fun findAssociation(file: FileInfo): Association? = getSource().findAssociation(file)

  /**
   * Checks whether psiElement is of type (PsiFile/PsiDirectory) defined by
   * this provider
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

  /** Return the [IconType] of this provider. */
  abstract fun getType(): IconType

  /**
   * Whether this provider is for default associations
   *
   * @return true if default assoc provider
   */
  abstract fun isDefault(): Boolean
}
