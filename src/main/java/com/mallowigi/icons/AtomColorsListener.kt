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
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.util.SVGLoader
import com.mallowigi.config.listeners.AtomConfigNotifier
import com.mallowigi.icons.svgpatchers.MainSvgPatcher
import com.mallowigi.utils.SvgLoaderHacker.collectOtherPatcher

/**
 * Apply a tint to the icons. This is used either for accented icons and themed icons.
 */
class AtomColorsListener : DynamicPluginListener, AppLifecycleListener, DumbAware {
  override fun appStarting(projectFromCommandLine: Project?): Unit = initComponent()

  override fun appClosing(): Unit = disposeComponent()

  override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor): Unit = initComponent()

  override fun pluginUnloaded(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
    if (PLUGIN_ID != pluginDescriptor.pluginId) return
    disposeComponent()
  }

  companion object {
    private val PLUGIN_ID = PluginId.getId("com.mallowigi")


    private fun initComponent() {
      val otherPatcher = collectOtherPatcher()
      MainSvgPatcher.instance.addPatcher(otherPatcher)
      SVGLoader.setColorPatcherProvider(MainSvgPatcher.instance)

      // Listen for changes on the settings
      val connect = ApplicationManager.getApplication().messageBus.connect()
      connect.subscribe(LafManagerListener.TOPIC, LafManagerListener { refreshColors() })
      connect.subscribe(AtomConfigNotifier.TOPIC, AtomConfigNotifier { refreshColors() })
    }

    private fun refreshColors() {
      MainSvgPatcher.instance.refreshColors()
    }

    private fun disposeComponent() = ApplicationManager.getApplication().messageBus.connect().disconnect()
  }
}
