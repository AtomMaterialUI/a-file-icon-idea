/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Elior "Mallowigi" Boukhobza
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

package com.mallowigi.icons.patchers

import com.intellij.openapi.project.ProjectManager
import com.mallowigi.config.select.AtomProjectSelectConfig
import com.mallowigi.config.select.AtomSelectConfig
import com.mallowigi.models.PsiFileInfo

/** PSI Icon Patcher to apply overrides. */
class PsiIconPathPatcher : AbstractIconPatcher() {

  override val pathToAppend: String = ""
  override val pathToRemove: String = ""

  /**
   * Patch the icon path if there is an override.
   *
   * @param path the path to patch
   * @param classLoader the classloader of the icon
   * @return the patched path if found, or null
   */
  override fun patchPath(path: String, classLoader: ClassLoader?): String? {
    if (!enabled) return null

    val fileInfo = PsiFileInfo(path)

    // Check project-level overrides first
    val openProjects = ProjectManager.getInstance().openProjects
    for (project in openProjects) {
      val projectConfig = AtomProjectSelectConfig.getInstance(project)
      val projectMatch = projectConfig.selectedPsiAssociations.findAssociation(fileInfo, true)
      if (projectMatch != null) {
        return projectMatch.icon
      }
    }

    // Then check global overrides
    val globalConfig = AtomSelectConfig.instance
    val globalMatch = globalConfig.selectedPsiAssociations.findAssociation(fileInfo, true)
    if (globalMatch != null) {
      return globalMatch.icon
    }

    return null
  }
}