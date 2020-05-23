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

package com.mallowigi.icons.providers

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.Associations
import com.mallowigi.models.FileInfo
import com.mallowigi.models.VirtualFileInfo
import com.mallowigi.services.toOptional
import org.jetbrains.annotations.Nullable
import javax.swing.Icon

abstract class AbstractFileIconProvider : IconProvider() {
  override fun getIcon(element: PsiElement, flags: Int): Icon? {
    if (isNotAppliable()) {
      return null;
    }

    if (isOfType(element)) return findIcon(element)
    return null
  }

  private fun findIcon(element: PsiElement): Icon? {
    var icon: Icon? = null
    val virtualFile = PsiUtilCore.getVirtualFile(element)

    if (virtualFile != null) {
      val file: FileInfo = VirtualFileInfo(element, virtualFile)
      val association = findAssociation(file)
      icon = getIconForAssociation(association)
    }
    return icon
  }

  protected fun getIconForAssociation(association: Association?): Icon? {
    return association.toOptional()
      .map { loadIcon(association) }
      .orElseGet { null }
  }

  private fun loadIcon(association: Association?): Icon? {
    var icon: Icon? = null
    try {
      val iconPath = association!!.icon
      icon = getIcon(iconPath)
    }
    catch (e: RuntimeException) {
      e.printStackTrace()
    }
    return icon
  }

  protected fun findAssociation(file: FileInfo): @Nullable Association? {
    return getSource().findAssociation(file)
  }

  abstract fun isOfType(element: PsiElement): Boolean

  abstract fun isNotAppliable(): Boolean

  abstract fun getSource(): Associations

  abstract fun getIcon(iconPath: String): Icon?

  abstract fun getType(): IconType

  abstract fun isDefault(): Boolean
}