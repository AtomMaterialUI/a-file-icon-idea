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
 */
package com.mallowigi.tree

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VirtualFile

/**
 * Tracks directories corresponding to currently open files in a project.
 * Maintains a set of directory paths derived from the hierarchy of files
 * open in the editor.
 *
 * This service is scoped to a project and ensures data consistency when
 * accessed concurrently. It also provides a mechanism to refresh the tracked
 * directories across open projects.
 *
 */
@Service(Service.Level.PROJECT)
class OpenFileDirectoryTracker(
  private val project: Project,
) {
  @Volatile
  private var openDirectoryPaths: Set<String> = emptySet()

  @Volatile
  private var initialized = false

  /**
   * Checks if the given directory is among the tracked directories corresponding to open files.
   */
  fun contains(directory: VirtualFile): Boolean {
    ensureInitialized()
    return directory.path in openDirectoryPaths
  }

  /**
   * For every open file, we keep the list of its ancestors so that we know which folders we need to "hollow"
   *
   * This way, rather than always check every folder, we only check the ones we registered here.
   */
  fun refresh(): Boolean = synchronized(this) {
    val updatedPaths = HashSet<String>()

    FileEditorManager.getInstance(project).openFiles.forEach { openFile ->
      var parent = openFile.parent
      while (parent != null) {
        updatedPaths += parent.path
        parent = parent.parent
      }
    }

    val changed = !initialized || openDirectoryPaths != updatedPaths
    openDirectoryPaths = updatedPaths
    initialized = true
    changed
  }

  private fun ensureInitialized() {
    if (!initialized) refresh()
  }

  companion object {
    fun getInstance(project: Project): OpenFileDirectoryTracker = project.service()

    fun refreshOpenProjects() {
      ProjectManager.getInstance().openProjects
        .filterNot(Project::isDisposed)
        .forEach { getInstance(it).refresh() }
    }
  }
}
