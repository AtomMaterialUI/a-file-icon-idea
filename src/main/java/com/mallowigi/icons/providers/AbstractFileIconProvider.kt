package com.mallowigi.icons.providers

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.mallowigi.icons.FileInfo
import com.mallowigi.icons.VirtualFileInfo
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.Associations
import com.mallowigi.toOptional
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