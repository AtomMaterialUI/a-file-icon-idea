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
package com.mallowigi.icons

import com.intellij.ide.AppLifecycleListener
import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.util.SVGLoader
import com.mallowigi.config.listeners.AtomConfigNotifier
import com.mallowigi.icons.svgpatchers.MainSvgPatcher
import com.mallowigi.utils.SvgLoaderHacker.collectOtherPatcher
import com.mallowigi.utils.getPluginId

/** Listener for SVG Patchers. */
class AtomSVGPatchersListener : DynamicPluginListener, AppLifecycleListener, DumbAware {

  /** Dispose on app closing. */
  override fun appClosing(): Unit = disposeComponent()

  /** Init on app started. */
  override fun appStarted(): Unit = initComponent()

  /** Init on plugin loaded. */
  override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor): Unit = initComponent()

  /** Dispose on plugin unloaded. */
  override fun pluginUnloaded(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
    if (getPluginId() != pluginDescriptor.pluginId) return
    disposeComponent()
  }

  private fun applySvgPatchers() = MainSvgPatcher.instance.applySvgPatchers()

  private fun disposeComponent() = ApplicationManager.getApplication().messageBus.connect().disconnect()

  @Suppress("UnstableApiUsage")
  private fun initComponent() {
    val otherPatcher = collectOtherPatcher()
    MainSvgPatcher.instance.addPatcher(otherPatcher)
    SVGLoader.colorPatcherProvider = MainSvgPatcher.instance

    // Listen for changes on the settings
    val connect = ApplicationManager.getApplication().messageBus.connect()
    connect.run {
      subscribe(LafManagerListener.TOPIC, LafManagerListener { applySvgPatchers() })
      subscribe(AtomConfigNotifier.TOPIC, AtomConfigNotifier { applySvgPatchers() })
    }

    ApplicationManager.getApplication().invokeLater { LafManager.getInstance().updateUI() }
  }

}
