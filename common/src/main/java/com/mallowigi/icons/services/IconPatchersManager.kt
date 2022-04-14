/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */

package com.mallowigi.icons.services

import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.IconPathPatcher
import com.intellij.ui.ExperimentalUI
import com.mallowigi.config.AtomFileIconsConfig.Companion.instance
import com.mallowigi.icons.patchers.AbstractIconPatcher
import com.mallowigi.icons.services.IconFilterManager.applyFilter

/** Icon patchers manager. */
object IconPatchersManager {
  private val iconPathPatchers = IconPatchersFactory.create()
  private val installedPatchers: MutableCollection<IconPathPatcher> = HashSet(100)

  /** Init the patchers. */
  fun init() {
    val atomFileIconsConfig = instance

    fixExperimentalUI()

    installPathPatchers(atomFileIconsConfig.isEnabledUIIcons)
    installPSIPatchers(atomFileIconsConfig.isEnabledPsiIcons)
    installFileIconsPatchers(atomFileIconsConfig.isEnabledIcons)
  }

  @Suppress("UnstableApiUsage")
  private fun fixExperimentalUI() {
    if (!ExperimentalUI.isNewUI()) return

    val forName = Class.forName("com.intellij.ui.ExperimentalUI")
    forName.declaredFields.forEach {
      if (it.name == "iconPathPatcher") {
        it.isAccessible = true
        val patcher = it.get(null)
        IconLoader.removePathPatcher(patcher as IconPathPatcher)
      }
    }
  }

  /** Update patchers on save. */
  fun updateIcons() {
    AbstractIconPatcher.clearCache()
    fixExperimentalUI()

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

  private fun installPathPatcher(patcher: AbstractIconPatcher, enabled: Boolean) {
    installedPatchers.add(patcher)
    IconLoader.installPathPatcher(patcher)
    patcher.enabled = enabled
  }

  private fun updatePathPatcher(patcher: AbstractIconPatcher, enabled: Boolean) {
    patcher.enabled = enabled
  }

  /** Update all trees. */
  fun updateFileIcons() {
    ApplicationManager.getApplication().invokeLater {
      val app = ApplicationManager.getApplication()
      app.runWriteAction { FileTypeManagerEx.getInstanceEx().fireFileTypesChanged() }
      app.runWriteAction { ActionToolbarImpl.updateAllToolbarsImmediately() }
      applyFilter()
    }
  }
}
