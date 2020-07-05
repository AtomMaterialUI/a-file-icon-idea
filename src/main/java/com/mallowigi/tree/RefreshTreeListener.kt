package com.mallowigi.tree

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.mallowigi.utils.refreshOpenedProjects

class RefreshTreeListener : FileEditorManagerListener {
  override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
    refreshOpenedProjects()
  }

  override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
    refreshOpenedProjects()
  }
}
