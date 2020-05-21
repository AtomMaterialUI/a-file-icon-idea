package com.mallowigi.icons.providers

import com.intellij.psi.PsiFile
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.icons.associations.Associations
import com.mallowigi.icons.associations.AssociationsFactory
import icons.MTIcons
import javax.swing.Icon

class DefaultFileIconProvider : AbstractFileIconProvider() {
  override fun getType(): IconType = IconType.FILE

  override fun isDefault(): Boolean = true

  override fun getSource(): Associations = associations

  override fun getIcon(iconPath: String): Icon? = MTIcons.getFileIcon(iconPath)

  override fun isNotAppliable() = !AtomFileIconsConfig.instance.isEnabledIcons

  override fun isOfType(element: Any): Boolean = element is PsiFile

  companion object {
    val associations = AssociationsFactory.create("/iconGenerator/icon_associations.xml")
  }
}