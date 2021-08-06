/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Elior "Mallowigi" Boukhobza
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
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.config.associations.AtomAssocConfig
import com.mallowigi.config.listeners.AssocConfigNotifier
import com.mallowigi.config.listeners.AtomConfigNotifier
import com.mallowigi.icons.patchers.AbstractIconPatcher
import com.mallowigi.icons.services.IconFilterManager.applyFilter
import com.mallowigi.icons.services.IconPatchersManager.init
import com.mallowigi.icons.services.IconPatchersManager.updateFileIcons
import com.mallowigi.icons.services.IconPatchersManager.updateIcons
import com.mallowigi.utils.refreshOpenedProjects

/**
 * Component in charge of replacing icons and apply filters
 *
 */
class IconReplacerComponent : DynamicPluginListener, AppLifecycleListener, DumbAware {
  override fun appStarting(projectFromCommandLine: Project?) {
    initComponent()
  }

  override fun appClosing() {
    disposeComponent()
  }

  override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor) {
    initComponent()
  }

  override fun pluginUnloaded(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
    disposeComponent()
  }

  private fun onSettingsChanged() {
    updateFileIcons()
    updateIcons()
    LafManager.getInstance().updateUI()
    refreshOpenedProjects()
  }

  private fun disposeComponent() {
    AbstractIconPatcher.clearCache()
    ApplicationManager.getApplication().messageBus.connect().disconnect()
  }

  private fun initComponent() {
    init()
    val connect = ApplicationManager.getApplication().messageBus.connect()
    with(connect) {
      subscribe(UISettingsListener.TOPIC, UISettingsListener { applyFilter() })

      subscribe(AtomConfigNotifier.TOPIC, object : AtomConfigNotifier {
        override fun configChanged(atomFileIconsConfig: AtomFileIconsConfig?) = onSettingsChanged()
      })

      subscribe(AssocConfigNotifier.TOPIC, object : AssocConfigNotifier {
        override fun assocChanged(config: AtomAssocConfig?) = onSettingsChanged()
      })

      subscribe(FileTypeManager.TOPIC, object : FileTypeListener {
        override fun fileTypesChanged(event: FileTypeEvent) = updateIcons()
      })

      subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
        override fun projectOpened(project: Project) = updateIcons()
      })
    }

    ApplicationManager.getApplication().invokeLater { applyFilter() }
  }
}
