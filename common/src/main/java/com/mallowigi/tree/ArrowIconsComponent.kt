/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package com.mallowigi.tree

import com.intellij.ide.AppLifecycleListener
import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.mallowigi.config.AtomFileIconsConfig.Companion.instance
import com.mallowigi.config.listeners.AtomConfigNotifier
import javax.swing.SwingUtilities
import javax.swing.UIManager

/** Arrow icons component: replace arrows. */
class ArrowIconsComponent : DynamicPluginListener, AppLifecycleListener {

  /** Dispose on app closing. */
  override fun appClosing(): Unit = disposeComponent()

  /** Init on app started. */
  override fun appStarted(): Unit = initComponent()

  /** Init on plugin loaded. */
  override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor): Unit = initComponent()

  /** Dispose on plugin unloaded. */
  override fun pluginUnloaded(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean): Unit = disposeComponent()

  private fun disposeComponent() = ApplicationManager.getApplication().messageBus.connect().disconnect()

  private fun initComponent() {
    replaceArrowIcons()
    val connect = ApplicationManager.getApplication().messageBus.connect()

    connect.run {
      subscribe(AtomConfigNotifier.TOPIC, AtomConfigNotifier { replaceArrowIcons() })
      subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
        override fun projectOpened(project: Project) = replaceArrowIcons()
      })
      subscribe(LafManagerListener.TOPIC, LafManagerListener { replaceArrowIcons() })
    }
  }

  private fun replaceArrowIcons() {
    val defaults = UIManager.getLookAndFeelDefaults()
    val arrowsStyle = instance.arrowsStyle
    defaults["Tree.collapsedIcon"] = arrowsStyle.expandIcon
    defaults["Tree.expandedIcon"] = arrowsStyle.collapseIcon
    defaults["Tree.collapsedSelectedIcon"] = arrowsStyle.selectedExpandIcon
    defaults["Tree.expandedSelectedIcon"] = arrowsStyle.selectedCollapseIcon
    SwingUtilities.invokeLater { ActionToolbarImpl.updateAllToolbarsImmediately() }
  }

}
