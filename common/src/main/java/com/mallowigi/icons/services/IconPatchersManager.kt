/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2024 Elior "Mallowigi" Boukhobza
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
 */

package com.mallowigi.icons.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.IconPathPatcher
import com.intellij.util.ui.JBUI
import com.mallowigi.config.AtomSettingsConfig
import com.mallowigi.icons.patchers.AbstractIconPatcher
import com.mallowigi.icons.services.IconFilterManager.applyFilter
import javax.swing.UIManager

/** Icon patchers manager. */
@Service(Service.Level.APP)
class IconPatchersManager {

  private val iconPathPatchers = IconPatchersFactory.create()
  private val installedPatchers: MutableCollection<IconPathPatcher> = HashSet(100)

  /** Init the patchers. */
  fun init() {
    val atomFileIconsConfig = AtomSettingsConfig.instance

    fixRunIcons()

    installPathPatchers(atomFileIconsConfig.isEnabledUIIcons)
    installPSIPatchers(atomFileIconsConfig.isEnabledPsiIcons)
    installFileIconsPatchers(atomFileIconsConfig.isEnabledIcons)
  }

  /** Update all trees. */
  fun updateFileIcons() {
    ApplicationManager.getApplication().invokeLater {
      val app = ApplicationManager.getApplication()
      app.runWriteAction { FileTypeManagerEx.getInstanceEx().fireFileTypesChanged() }
      applyFilter()
    }
  }

  /** Update patchers on save. */
  fun updateIcons() {
    AbstractIconPatcher.clearCache()
    fixRunIcons()

    val atomFileIconsConfig = AtomSettingsConfig.instance
    updatePathPatchers(atomFileIconsConfig.isEnabledUIIcons)
    updatePSIPatchers(atomFileIconsConfig.isEnabledPsiIcons)
    updateFileIconsPatchers(atomFileIconsConfig.isEnabledIcons)
  }

  fun fixRunIcons() {
    if (!AtomSettingsConfig.instance.fixActionButtonsColor) return;

    val resources = setOf(
      "RunToolbar.Debug.activeBackground",
      "RunToolbar.Profile.activeBackground",
      "RunToolbar.Run.activeBackground",
      "RunWidget.Debug.activeBackground",
      "RunWidget.Profile.activeBackground",
      "RunWidget.Run.activeBackground",
      "RunWidget.Running.background",
      "RunWidget.Running.leftHoverBackground",
      "RunWidget.StopButton.leftHoverBackground",
      "RunWidget.hoverBackground",
      "RunWidget.leftHoverBackground",
      "RunWidget.runningBackground",
      "RunWidget.Running.leftPressedBackground",
      "RunWidget.StopButton.leftPressedBackground",
      "RunWidget.leftPressedBackground",
      "RunWidget.pressedBackground",
      "RunWidget.StopButton.background",
      "RunWidget.background",
      "RunWidget.stopBackground"
    )
    resources.forEach {
      UIManager.put(it, JBUI.CurrentTheme.ActionButton.pressedBackground())
    }
  }

  private fun installFileIconsPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.filePatchers) {
      installPathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  private fun installPSIPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.glyphPatchers) {
      installPathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  private fun installPathPatcher(patcher: AbstractIconPatcher, enabled: Boolean) {
    installedPatchers.add(patcher)
    IconLoader.installPathPatcher(patcher)
    patcher.enabled = enabled
  }

  private fun installPathPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.iconPatchers) {
      installPathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  private fun updateFileIconsPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.filePatchers) {
      updatePathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  private fun updatePSIPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.glyphPatchers) {
      updatePathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  private fun updatePathPatcher(patcher: AbstractIconPatcher, enabled: Boolean) {
    patcher.enabled = enabled
  }

  private fun updatePathPatchers(enabled: Boolean) {
    for (externalPatcher in iconPathPatchers.iconPatchers) {
      updatePathPatcher(externalPatcher as AbstractIconPatcher, enabled)
    }
  }

  companion object {
    @JvmStatic
    val instance: IconPatchersManager by lazy { service() }
  }
}
