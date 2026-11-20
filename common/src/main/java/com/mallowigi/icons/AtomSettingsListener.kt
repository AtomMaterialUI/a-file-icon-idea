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
 */
package com.mallowigi.icons

import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.ui.UISettingsListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileTypes.FileTypeEvent
import com.intellij.openapi.fileTypes.FileTypeListener
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.JDOMUtil
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.ui.UIUtil
import com.intellij.util.xmlb.XmlSerializer
import com.mallowigi.config.AtomSettingsConfig
import com.mallowigi.config.listeners.AtomConfigNotifier
import com.mallowigi.config.listeners.AtomSelectNotifier
import com.mallowigi.config.select.AtomSelectConfig
import com.mallowigi.icons.patchers.AbstractIconPatcher
import com.mallowigi.icons.services.IconFilterManager
import com.mallowigi.icons.services.IconPatchersManager
import com.mallowigi.icons.services.AssociationResolver
import com.mallowigi.tree.OpenFileDirectoryTracker
import com.mallowigi.tree.arrows.ArrowsStyles
import com.mallowigi.utils.getPluginId
import com.mallowigi.utils.refreshOpenedProjects
import com.mallowigi.utils.refreshUI
import org.jdom.Element

/** Listener for Settings Changes. */
class AtomSettingsListener : DynamicPluginListener, ProjectActivity, DumbAware {
  private var connection: MessageBusConnection? = null
  private var fileIconSettings: AtomSettingsConfig? = null
  private var globalAssociations: Element? = null

  /** Init on plugin loaded. */
  override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor) {
    initComponent()
    AssociationResolver.instance.invalidate()
  }

  /** Dispose on plugin unloaded. */
  override fun pluginUnloaded(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
    AssociationResolver.instance.invalidate()
    if (pluginDescriptor.pluginId == getPluginId()) {
      disposeComponent()
    }
  }

  private fun disposeComponent() {
    AbstractIconPatcher.clearCache()
    AssociationResolver.instance.invalidate()
    connection?.disconnect()
    connection = null
  }

  private fun initComponent() {
    if (connection != null) return

    val connect = ApplicationManager.getApplication().messageBus.connect()
    connection = connect

    with(connect) {
      subscribe(DynamicPluginListener.TOPIC, this@AtomSettingsListener)

      subscribe(UISettingsListener.TOPIC, UISettingsListener { IconFilterManager.applyFilter() })

      subscribe(AtomConfigNotifier.TOPIC, AtomConfigNotifier { onSettingsChanged(refreshFileIcons = fileIconSettingsChanged()) })

      subscribe(AtomSelectNotifier.TOPIC, AtomSelectNotifier { onSettingsChanged(refreshFileIcons = associationsChanged(it)) })

      subscribe(FileTypeManager.TOPIC, object : FileTypeListener {
        override fun fileTypesChanged(event: FileTypeEvent) {
          AssociationResolver.instance.invalidate()
          IconPatchersManager.instance.updateIcons()
        }
      })
    }

    fileIconSettings = fileIconSettingsSnapshot()
    globalAssociations = XmlSerializer.serialize(AtomSelectConfig.instance)

    UIUtil.invokeLaterIfNeeded { IconFilterManager.applyFilter() }
  }

  /**
   * Refreshing file icons fires a file type change, which re-applies the icon patchers and makes the IDE re-scan the project for indexing.
   * Changes that only affect how icons are painted skip it and only repaint.
   */
  private fun onSettingsChanged(refreshFileIcons: Boolean) {
    if (!refreshFileIcons) {
      UIUtil.invokeLaterIfNeeded {
        IconFilterManager.applyFilter()
        refreshUI()
      }
      return
    }

    AssociationResolver.instance.invalidate()

    if (AtomSettingsConfig.instance.isUseHollowFolders) {
      OpenFileDirectoryTracker.refreshOpenProjects()
    }

    UIUtil.invokeLaterIfNeeded {
      IconPatchersManager.instance.updateFileIcons()
      refreshUI()
      refreshOpenedProjects()
    }
  }

  /** Whether the settings changed other than in the ones applied at paint time. */
  private fun fileIconSettingsChanged(): Boolean {
    val current = fileIconSettingsSnapshot()
    val changed = current != fileIconSettings

    fileIconSettings = current
    return changed
  }

  /** The settings without the icon filters and the tree arrows, which are applied at paint time. */
  private fun fileIconSettingsSnapshot(): AtomSettingsConfig = AtomSettingsConfig().apply {
    copyFrom(AtomSettingsConfig.instance)
    isMonochromeIcons = false
    monochromeColor = null
    isSaturatedIcons = false
    saturation = 0
    isOpacityIcons = false
    opacity = 0
    arrowsStyle = ArrowsStyles.MATERIAL
  }

  /**
   * Whether the global associations changed. A synchronized state always looks changed to [AtomSelectConfig.loadState], so compare the
   * serialized state instead. Project associations (`null`) are not tracked and always count as changed.
   */
  private fun associationsChanged(config: AtomSelectConfig?): Boolean {
    if (config == null) return true

    val current = XmlSerializer.serialize(config)
    val changed = !JDOMUtil.areElementsEqual(current, globalAssociations)

    globalAssociations = current
    return changed
  }

  /** Run on project open. */
  override suspend fun execute(project: Project): Unit = initComponent()

}
