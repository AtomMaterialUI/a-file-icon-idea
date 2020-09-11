package com.mallowigi.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.mallowigi.config.AtomFileIconsConfig.Companion.instance

class RefreshIconsAction : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    CONFIG.fireChanged()
  }

  companion object {
    private val CONFIG = instance
  }
}