package com.mallowigi.tree

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.packageDependencies.ui.PackageDependenciesNode
import com.intellij.ui.ColoredTreeCellRenderer
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.config.associations.AtomAssocConfig
import com.mallowigi.icons.providers.DefaultFileIconProvider
import com.mallowigi.icons.special.CustomFileIcon
import com.mallowigi.models.VirtualFileInfo
import icons.MTIcons

class PythonDecorator : ProjectViewNodeDecorator {
  override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
    val virtualFile = node.virtualFile ?: return
    val extensions = listOf("py", "pt", "pyx", "webpy", "pyc", "mako")

    if (!AtomFileIconsConfig.instance.isEnabledIcons) return
    if (virtualFile.extension !in extensions) return

    matchCustomAssociation(virtualFile, data)
    matchDefaultAssociation(virtualFile, data)
  }

  private fun matchDefaultAssociation(virtualFile: VirtualFile, data: PresentationData) {
    val fileInfo = VirtualFileInfo(virtualFile)
    val associations = DefaultFileIconProvider.associations

    val matchingAssociation = associations.findMatchingAssociation(fileInfo)
    if (matchingAssociation != null) {
      data.setIcon(MTIcons.getFileIcon(matchingAssociation.icon))
    }
  }

  private fun matchCustomAssociation(virtualFile: VirtualFile, data: PresentationData) {
    val fileInfo = VirtualFileInfo(virtualFile)
    val customFileAssociations = AtomAssocConfig.instance.customFileAssociations

    val matchingAssociation = customFileAssociations.findMatchingAssociation(fileInfo)
    if (matchingAssociation != null) {
      data.setIcon(CustomFileIcon(MTIcons.loadSVGIcon(matchingAssociation.icon)))
    }
  }

  override fun decorate(node: PackageDependenciesNode, cellRenderer: ColoredTreeCellRenderer) {}
}