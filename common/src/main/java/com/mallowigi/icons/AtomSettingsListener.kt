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
package com.mallowigi.icons

import com.intellij.ide.AppLifecycleListener
import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.UISettingsListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.FileTypeEvent
import com.intellij.openapi.fileTypes.FileTypeListener
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.util.ui.UIUtil
import com.mallowigi.config.listeners.AtomConfigNotifier
import com.mallowigi.config.listeners.AtomSelectNotifier
import com.mallowigi.icons.patchers.AbstractIconPatcher
import com.mallowigi.icons.services.IconFilterManager
import com.mallowigi.icons.services.IconPatchersManager
import com.mallowigi.utils.refreshOpenedProjects

/**
 * Listener for Settings Changes
 *
 */
class AtomSettingsListener : DynamicPluginListener, AppLifecycleListener, DumbAware {
  /**
   * Init on app started
   */
  @Suppress("UnstableApiUsage")
  override fun appStarted(): Unit = initComponent()

  /**
   * Dispose on app closing
   */
  override fun appClosing(): Unit = disposeComponent()

  /**
   * Init on plugin loaded
   */
  override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor): Unit = initComponent()

  /**
   * Dispose on plugin unloaded
   */
  override fun pluginUnloaded(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean): Unit = disposeComponent()

  private fun onSettingsChanged() {
    IconPatchersManager.updateFileIcons()
    IconPatchersManager.updateIcons()
    LafManager.getInstance().updateUI()
    refreshOpenedProjects()
  }

  private fun disposeComponent() {
    AbstractIconPatcher.clearCache()
    ApplicationManager.getApplication().messageBus.connect().disconnect()
  }

  private fun initComponent() {
    IconPatchersManager.init()
    val connect = ApplicationManager.getApplication().messageBus.connect()
    with(connect) {
      subscribe(UISettingsListener.TOPIC, UISettingsListener { IconFilterManager.applyFilter() })

      subscribe(AtomConfigNotifier.TOPIC, AtomConfigNotifier { onSettingsChanged() })

      subscribe(AtomSelectNotifier.TOPIC, AtomSelectNotifier { onSettingsChanged() })

      subscribe(FileTypeManager.TOPIC, object : FileTypeListener {
        override fun fileTypesChanged(event: FileTypeEvent) = IconPatchersManager.updateIcons()
      })

      

      subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
        override fun projectOpened(project: Project) = IconPatchersManager.updateIcons()
      })
    }

    UIUtil.invokeLaterIfNeeded { IconFilterManager.applyFilter() }
  }
}
