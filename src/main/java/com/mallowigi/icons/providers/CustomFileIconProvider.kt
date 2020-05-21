package com.mallowigi.icons.providers

import com.intellij.psi.PsiFile
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.config.associations.AtomAssocConfig
import icons.MTIcons
import javax.swing.Icon

class CustomFileIconProvider : AbstractFileIconProvider() {
  override fun getSource() = AtomAssocConfig.instance.customFileAssociations

  override fun isOfType(element: Any): Boolean = element is PsiFile

  override fun getType(): IconType = IconType.FILE

  override fun isDefault(): Boolean = false

  override fun getIcon(iconPath: String): Icon? = MTIcons.loadSVGIcon(iconPath)

  override fun isNotAppliable() = !AtomFileIconsConfig.instance.isEnabledIcons

}