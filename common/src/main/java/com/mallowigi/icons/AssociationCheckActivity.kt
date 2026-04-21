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

package com.mallowigi.icons

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.startup.ProjectActivity
import com.mallowigi.config.BundledAssociations
import com.mallowigi.config.select.AtomSelectConfig
import com.mallowigi.icons.associations.RegexAssociation
import com.mallowigi.models.IconType
import com.mallowigi.models.VirtualFileInfo

/** Activity for checking if a disabled association should be enabled. */
class AssociationCheckActivity : ProjectActivity {
  override suspend fun execute(project: Project) {
    val bundledAssociations = BundledAssociations.instance
    val config = AtomSelectConfig.instance

    val fileCandidateAssocs = getCandidates(bundledAssociations, IconType.FILE)
    val folderCandidateAssocs = getCandidates(bundledAssociations, IconType.FOLDER)

    if (fileCandidateAssocs.isEmpty() && folderCandidateAssocs.isEmpty()) return

    val matchedFileAssocs = mutableSetOf<RegexAssociation>()
    val matchedFolderAssocs = mutableSetOf<RegexAssociation>()

    ProjectFileIndex.getInstance(project).iterateContent { vFile ->
      val fileInfo = VirtualFileInfo(vFile)
      if (vFile.isDirectory) {
        folderCandidateAssocs.forEach { assoc ->
          if (!matchedFolderAssocs.contains(assoc) && assoc.matches(fileInfo)) {
            matchedFolderAssocs.add(assoc)
          }
        }
      } else {
        fileCandidateAssocs.forEach { assoc ->
          if (!matchedFileAssocs.contains(assoc) && assoc.matches(fileInfo)) {
            matchedFileAssocs.add(assoc)
          }
        }
      }
      // Continue until all candidates are matched or no more files
      !(matchedFileAssocs.size == fileCandidateAssocs.size && matchedFolderAssocs.size == folderCandidateAssocs.size)
    }

    if (matchedFileAssocs.isNotEmpty() || matchedFolderAssocs.isNotEmpty()) {
      matchedFileAssocs.forEach { it.enabled = true; it.touched = true }
      matchedFolderAssocs.forEach { it.enabled = true; it.touched = true }

      config.apply(
        config.selectedFileAssociations,
        config.selectedFolderAssociations,
        config.selectedFolderOpenAssociations
      )
    }
  }

  private fun getCandidates(bundled: BundledAssociations, iconType: IconType): List<RegexAssociation> =
    bundled.getList(iconType).filter { it.defaultState == "false" && !it.touched && !it.enabled }

}
