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

  abstract fun isOfType(element: Any): Boolean

  private fun findIcon(element: PsiElement): Icon? {
    var icon: Icon? = null
    val virtualFile = PsiUtilCore.getVirtualFile(element)

    if (virtualFile != null) {
      val file: FileInfo = VirtualFileInfo(element, virtualFile)
      val fileAssociation = findFileAssociation(file)
      icon = getIconForAssociation(fileAssociation)
    }
    return icon
  }

  private fun getIconForAssociation(fileAssociation: Association?): Icon? {
    return fileAssociation.toOptional()
      .map { loadIcon(fileAssociation) }
      .orElseGet { null }
  }

  private fun loadIcon(fileAssociation: Association?): Icon? {
    var icon: Icon? = null
    try {
      val iconPath = fileAssociation!!.icon
      icon = getIcon(iconPath)
    }
    catch (e: RuntimeException) {
      e.printStackTrace()
    }
    return icon
  }

  private fun findFileAssociation(file: FileInfo): @Nullable Association? {
    return getSource().findAssociation(file)
  }

  abstract fun isNotAppliable(): Boolean

  abstract fun getSource(): Associations

  abstract fun getIcon(iconPath: String): Icon?

  abstract fun getType(): IconType

  abstract fun isDefault(): Boolean
}