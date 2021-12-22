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

package com.mallowigi.utils

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.JBColor
import com.mallowigi.config.AtomSettingsBundle

/**
 * Refresh
 *
 * @param project
 */
fun refresh(project: Project?) {
  if (project != null) {
    val view = ProjectView.getInstance(project)
    if (view != null) {
      view.refresh()
      if (view.currentProjectViewPane != null) view.currentProjectViewPane.updateFromRoot(true)
    }
  }
}

/**
 * Refresh opened projects
 *
 */
fun refreshOpenedProjects() {
  val projects: Array<Project> = ProjectManager.getInstance().openProjects
  for (project in projects) refresh(project)
}

/**
 * Get plugin descriptor
 *
 */
fun getPlugin(): IdeaPluginDescriptor? = PluginManagerCore.getPlugin(getPluginId())

/**
 * Plugin ID
 */
fun getPluginId(): PluginId = PluginId.getId("com.mallowigi")

/**
 * Get current plugin version
 *
 */
fun getVersion(): String {
  val plugin: IdeaPluginDescriptor? = getPlugin()
  return if (plugin != null) plugin.version else AtomSettingsBundle.message("plugin.version")
}

/**
 * Modified color
 */
fun getModifiedColor(): JBColor = JBColor.namedColor("Tree.modifiedItemForeground", JBColor.BLUE)
