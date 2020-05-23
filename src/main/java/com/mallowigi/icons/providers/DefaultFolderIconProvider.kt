package com.mallowigi.icons.providers

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.icons.DirIcon
import com.mallowigi.icons.associations.Associations
import com.mallowigi.icons.associations.AssociationsFactory
import icons.MTIcons
import javax.swing.Icon

class DefaultFolderIconProvider : AbstractFileIconProvider() {
  private val LOG = Logger.getInstance(DefaultFolderIconProvider::class.java)

  override fun getIcon(iconPath: String): Icon? = MTIcons.getFolderIcon(iconPath)

  override fun isOfType(element: PsiElement): Boolean = element is PsiDirectory

  override fun isNotAppliable(): Boolean = !AtomFileIconsConfig.instance.isEnabledDirectories

  override fun getSource(): Associations = associations

  override fun getType(): IconType = IconType.FOLDER

  override fun isDefault(): Boolean = true

  override fun getIcon(element: PsiElement, flags: Int): Icon? {
    var icon = super.getIcon(element, flags) as? DirIcon

    // Hollow folders support
    if (!AtomFileIconsConfig.instance.isUseHollowFolders) {
      return icon?.closedIcon
    }

    val virtualFile = PsiUtilCore.getVirtualFile(element)

    if (icon != null && virtualFile != null) {
      val has = isFolderContainingOpenFiles(element, virtualFile)
      return if (has) icon.openedIcon else icon.closedIcon
    }
    return null
  }

  private fun isFolderContainingOpenFiles(element: PsiElement,
                                          virtualFile: VirtualFile): Boolean {
    val openFiles = FileEditorManager.getInstance(element.project).openFiles
    return openFiles.any { vf: VirtualFile -> vf.path.contains(virtualFile.getPath().toString()) }
  }

  companion object {
    val associations = AssociationsFactory.create("/iconGenerator/folder_associations.xml")
  }
}