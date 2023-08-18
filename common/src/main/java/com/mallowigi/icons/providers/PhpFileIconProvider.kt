package com.mallowigi.icons.providers

import com.intellij.ide.FileIconProvider
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.ElementBase
import com.intellij.ui.IconManager
import com.intellij.util.containers.ContainerUtil
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.mallowigi.config.select.AtomSelectConfig
import com.mallowigi.models.VirtualFileInfo
import icons.AtomIcons
import javax.swing.Icon

class PhpFileIconProvider() : FileIconProvider {
  override fun getIcon(virtualFile: VirtualFile, flags: Int, project: Project?): Icon? {
    if (project == null || !FileTypeRegistry.getInstance().isFileOfType(virtualFile, PhpFileType.INSTANCE)) return null

    when (val psiFile = PsiManager.getInstance(project).findFile(virtualFile)) {
      !is PhpFile -> return null

      else -> {
        val phpClass: PhpClass = extractOnlyClass(psiFile) ?: return matchAssociation(virtualFile)
        val icon: Icon = phpClass.icon
        return IconManager.getInstance().createLayeredIcon(psiFile, icon, ElementBase.transformFlags(psiFile, flags))
      }
    }
  }

  private fun matchAssociation(virtualFile: VirtualFile): Icon? {
    val fileInfo = VirtualFileInfo(virtualFile)
    val associations = AtomSelectConfig.instance.selectedFileAssociations

    val matchingAssociation = associations.findAssociation(fileInfo, includeIgnored = true)
    if (matchingAssociation != null) {
      val iconPath = matchingAssociation.icon
      val icon = AtomIcons.loadIconWithFallback(AtomIcons.getFileIcon(iconPath), iconPath)
      return AtomIcons.getLayeredIcon(icon, virtualFile)
    }
    return null
  }

  private fun extractOnlyClass(file: PhpFile): PhpClass? {
    val classes = ContainerUtil.filter(file.topLevelDefs.values()) { e: PhpNamedElement? -> e is PhpClass }
    if (classes.size == 1) {
      val aClass = classes[0] as PhpClass
      if (StringUtil.containsIgnoreCase(file.name, aClass.name)) {
        return aClass
      }
    }
    return null
  }

}
