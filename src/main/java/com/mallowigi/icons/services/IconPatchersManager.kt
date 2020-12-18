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

  fun init() {
    val atomFileIconsConfig = instance
    IconLoader.installPathPatcher(checkStyleIconPatcher)

    installPathPatchers(atomFileIconsConfig.isEnabledUIIcons)
    installPSIPatchers(atomFileIconsConfig.isEnabledPsiIcons)
    installFileIconsPatchers(atomFileIconsConfig.isEnabledIcons)
  }

  fun updateIcons() {
    AbstractIconPatcher.clearCache()
    val atomFileIconsConfig = instance
    updatePathPatchers(atomFileIconsConfig.isEnabledUIIcons)
    updatePSIPatchers(atomFileIconsConfig.isEnabledPsiIcons)
    updateFileIconsPatchers(atomFileIconsConfig.isEnabledIcons)
  }

  private fun installPathPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.iconPatchers!!) {
      installPathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  private fun installPSIPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.glyphPatchers!!) {
      installPathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  private fun installFileIconsPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.filePatchers!!) {
      installPathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  private fun updatePathPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.iconPatchers!!) {
      updatePathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  private fun updatePSIPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.glyphPatchers!!) {
      updatePathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  private fun updateFileIconsPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.filePatchers!!) {
      updatePathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  private fun removePathPatchers() {
    for (iconPathPatcher in installedPatchers) {
      removePathPatcher(iconPathPatcher)
    }
//    removePathPatcher(checkStyleIconPatcher)
    installedPatchers.clear()
    IconLoader.clearCache()
  }

  private fun removePathPatcher(patcher: IconPathPatcher) {
    IconLoader.removePathPatcher(patcher)
  }

  private fun installPathPatcher(patcher: AbstractIconPatcher, enabled: Boolean) {
    installedPatchers.add(patcher)
    IconLoader.installPathPatcher(patcher)
    patcher.enabled = enabled;
  }

  private fun updatePathPatcher(patcher: AbstractIconPatcher, enabled: Boolean) {
    patcher.enabled = enabled;
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
