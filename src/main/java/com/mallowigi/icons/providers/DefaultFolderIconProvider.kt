package com.mallowigi.icons.providers

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.icons.associations.Associations
import com.mallowigi.icons.associations.AssociationsFactory
import icons.MTIcons
import javax.swing.Icon

class DefaultFolderIconProvider : AbstractFileIconProvider() {
  override fun getIcon(iconPath: String): Icon? = MTIcons.getFolderIcon(iconPath)

  override fun isOfType(element: PsiElement): Boolean = element is PsiDirectory

  override fun isNotAppliable(): Boolean = !AtomFileIconsConfig.instance.isEnabledDirectories

  override fun getSource(): Associations = associations

  override fun getType(): IconType = IconType.FOLDER

  override fun isDefault(): Boolean = true

  companion object {
    val associations = AssociationsFactory.create("/iconGenerator/folder_associations.xml")
  }
}