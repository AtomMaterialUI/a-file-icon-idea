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
package com.mallowigi.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.options.ShowSettingsUtil
import com.mallowigi.config.associations.ui.internal.AssociationsTableModelEditor.Companion.DEFAULT_FOLDER_COLOR
import com.mallowigi.config.associations.ui.internal.AssociationsTableModelEditor.Companion.DEFAULT_ICON_COLOR
import com.mallowigi.config.associations.ui.internal.AssociationsTableModelEditor.Companion.DEFAULT_PRIORITY
import com.mallowigi.config.select.AtomSelectConfig
import com.mallowigi.config.select.AtomSelectConfigurable
import com.mallowigi.icons.associations.RegexAssociation

class AddFolderAssociationAction : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
    if (virtualFile == null) return

    val fileName = virtualFile.name
    val fileNameWithoutExtension = virtualFile.nameWithoutExtension.replaceFirstChar { it.uppercase() }
    val fileExtension = virtualFile.extension ?: ""

    val association = RegexAssociation().apply {
      name = "$fileNameWithoutExtension ${fileExtension.uppercase()}"
      pattern = fileName
      icon = "/folder.svg"
      priority = DEFAULT_PRIORITY
      folderColor = DEFAULT_FOLDER_COLOR
      folderIconColor = DEFAULT_ICON_COLOR
      touched = true
    }

    AtomSelectConfig.instance.selectedFolderAssociations.addAssociation(association)
    AtomSelectConfig.instance.selectedFolderOpenAssociations.addAssociation(association)

    ShowSettingsUtil.getInstance().showSettingsDialog(
      e.project,
      AtomSelectConfigurable::class.java
    )
  }

  override fun update(event: AnActionEvent) {
    // Get the selected file array from the context
    val virtualFiles = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)

    // Only make the action available when exactly one file is selected
    event.presentation.isEnabledAndVisible = virtualFiles != null && virtualFiles.size == 1 && virtualFiles[0].isDirectory
  }

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
