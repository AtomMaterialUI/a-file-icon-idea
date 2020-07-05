package com.mallowigi.tree

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.utils.refresh

class RefreshTreeListener : FileEditorManagerListener {
  override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
    if (AtomFileIconsConfig.instance.isUseHollowFolders) {
      refresh(source.project)
    }
  }

  override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
    if (AtomFileIconsConfig.instance.isUseHollowFolders) {
      refresh(source.project)
    }
  }
}
