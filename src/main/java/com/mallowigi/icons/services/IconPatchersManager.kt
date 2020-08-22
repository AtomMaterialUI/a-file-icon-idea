package com.mallowigi.icons.services

import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.IconPathPatcher
import com.intellij.ui.GuiUtils
import com.mallowigi.config.AtomFileIconsConfig.Companion.instance
import com.mallowigi.icons.IconPatchersFactory
import com.mallowigi.icons.patchers.AbstractIconPatcher
import com.mallowigi.icons.patchers.CheckStyleIconPatcher
import com.mallowigi.icons.services.IconFilterManager.applyFilter
import java.util.*


object IconPatchersManager {
  private val iconPathPatchers = IconPatchersFactory.create()
  private val installedPatchers: MutableCollection<IconPathPatcher> = HashSet(100)
  private val checkStyleIconPatcher = CheckStyleIconPatcher()

  fun updateIcons() {
    AbstractIconPatcher.clearCache()
    removePathPatchers()
    val atomFileIconsConfig = instance
    if (atomFileIconsConfig.isEnabledUIIcons) {
      IconLoader.installPathPatcher(checkStyleIconPatcher)
      installPathPatchers()
    }
    if (atomFileIconsConfig.isEnabledPsiIcons) {
      installPSIPatchers()
    }
    if (atomFileIconsConfig.isEnabledIcons) {
      installFileIconsPatchers()
    }
  }

  private fun installPathPatchers() {
    for (externalPatcher in iconPathPatchers.iconPatchers) {
      installPathPatcher(externalPatcher)
    }
  }

  private fun installPSIPatchers() {
    for (externalPatcher in iconPathPatchers.glyphPatchers) {
      installPathPatcher(externalPatcher)
    }
  }

  private fun installFileIconsPatchers() {
    for (externalPatcher in iconPathPatchers.filePatchers) {
      installPathPatcher(externalPatcher)
    }
  }

  private fun removePathPatchers() {
    for (iconPathPatcher in installedPatchers) {
      removePathPatcher(iconPathPatcher)
    }
    installedPatchers.clear()
    IconLoader.clearCache()
  }

  private fun removePathPatcher(patcher: IconPathPatcher) {
    IconLoader.removePathPatcher(patcher)
  }

  private fun installPathPatcher(patcher: IconPathPatcher) {
    installedPatchers.add(patcher)
    IconLoader.installPathPatcher(patcher)
  }

  fun updateFileIcons() {
    GuiUtils.invokeLaterIfNeeded({
      val app = ApplicationManager.getApplication()
      app.runWriteAction { FileTypeManagerEx.getInstanceEx().fireFileTypesChanged() }
      app.runWriteAction { ActionToolbarImpl.updateAllToolbarsImmediately() }
      applyFilter()
    }, ModalityState.NON_MODAL)
  }
}
