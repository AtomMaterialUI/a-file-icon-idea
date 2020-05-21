package com.mallowigi.icons.providers

import com.intellij.psi.PsiDirectory
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.config.associations.AtomAssocConfig
import icons.MTIcons
import javax.swing.Icon

class CustomFolderIconProvider : AbstractFileIconProvider() {
  override fun getSource() = AtomAssocConfig.instance.customFolderAssociations

  override fun getType(): IconType = IconType.FOLDER

  override fun isDefault(): Boolean = false

  override fun isOfType(element: Any): Boolean = element is PsiDirectory

  override fun getIcon(iconPath: String): Icon? = MTIcons.loadSVGIcon(iconPath)

  override fun isNotAppliable() = !AtomFileIconsConfig.instance.isEnabledDirectories

}