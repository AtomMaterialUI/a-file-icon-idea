/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.config.select

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.Messages
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.ColumnInfo
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.config.associations.ui.columns.*
import com.mallowigi.config.associations.ui.internal.AssociationsTableItemEditor
import com.mallowigi.config.associations.ui.internal.AssociationsTableModelEditor
import com.mallowigi.icons.associations.SelectedAssociations
import com.mallowigi.models.IconType
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent
import javax.swing.JPanel

/** Configurable for Custom Associations. */
class AtomSelectConfigurable : BoundSearchableConfigurable(
  message("AtomSelectForm.title"),
  "com.mallowigi.config.AtomSelectConfig",
), Disposable {
  private var main: DialogPanel
  private val settings = AtomSelectConfig.instance
  private lateinit var tabbedPane: JBTabbedPane

  // Panels for the tables
  private var fileAssociationsPanel: JPanel
  private var folderAssociationsPanel: JPanel

  // Search boxes
  private var fileSearch: SearchTextField = SearchTextField()
  private var folderSearch: SearchTextField = SearchTextField()

  // Tables
  private lateinit var fileIconsTable: JComponent
  private lateinit var folderIconsTable: JComponent

  // Editors
  private var fileAssociationsEditor: AssociationsTableModelEditor? = null
  private var folderAssociationsEditor: AssociationsTableModelEditor? = null

  // Columns
  private val fileColumns = arrayOf<ColumnInfo<*, *>>(
    EnabledColumnInfo(),
    TouchedColumnInfo(),
    NameEditableColumnInfo(this, true),
    PatternEditableColumnInfo(this, true),
    FileIconEditableColumnInfo(this, true),
    PriorityColumnInfo(this, true),
    IconColorEditableColumnInfo(this))

  private val folderColumns = arrayOf<ColumnInfo<*, *>>(
    EnabledColumnInfo(),
    TouchedColumnInfo(),
    NameEditableColumnInfo(this, true),
    PatternEditableColumnInfo(this, true),
    FolderIconEditableColumnInfo(this, true),
    PriorityColumnInfo(this, true),
    FolderColorEditableColumnInfo(this),
    FolderIconColorEditableColumnInfo(this))

  init {
    createFileIconsTable()
    createFolderIconsTable()

    fileAssociationsPanel = panel {
      row {
        cell(fileSearch)
          .align(Align.FILL)
      }

      row {
        cell(fileIconsTable)
          .align(Align.FILL)
      }
    }

    folderAssociationsPanel = panel {
      row {
        cell(folderSearch)
          .align(Align.FILL)
      }

      row {
        cell(folderIconsTable)
          .resizableColumn()
          .align(Align.FILL)
      }
    }


    main = panel {
      row {
        comment(message("SelectForm.explanation.text"))
      }

      row {
        comment(message("SelectForm.customExplanation2.text"))
        link(message("SelectForm.link.text")) {
          BrowserUtil.browse(message("SelectForm.link.text"))
        }
      }

      row {
        tabbedPane = cell(JBTabbedPane())
          .resizableColumn()
          .align(Align.FILL)
          .component
      }

      row {
        comment(message("SelectForm.explanation2.text"))
      }

      row {
        button(message("SelectForm.resetButton.text")) { resetSettings() }
          .resizableColumn()
          .align(AlignX.RIGHT)
      }
    }

    fileSearch.textEditor.emptyText.setText(message("fileSearch.placeholder"))
    folderSearch.textEditor.emptyText.setText(message("fileSearch.placeholder"))

    tabbedPane.addTab(message("SelectForm.fileAssociationsPanel.tab.title"), fileAssociationsPanel)
    tabbedPane.addTab(message("SelectForm.folderAssociationsPanel.tab.title"), folderAssociationsPanel)
  }

  /** Configurable display name. */
  @Nls
  override fun getDisplayName(): String = message("AtomSelectForm.title")

  /** Configurable ID. */
  override fun getId(): String = ID

  @Suppress("Detekt.LongMethod")
  override fun createPanel(): DialogPanel {
    loadAssociations()

    return main
  }

  override fun dispose() {
    fileAssociationsEditor = null
    folderAssociationsEditor = null
  }

  private fun resetSettings() {
    if (Messages.showOkCancelDialog(
        /* message = */ message("SelectForm.resetDialog.text"),
        /* title = */ message("SelectForm.resetDialog.title"),
        /* okText = */ message("SelectForm.resetDialog.ok"),
        /* cancelText = */ message("SelectForm.resetDialog.cancel"),
        /* icon = */ Messages.getQuestionIcon()
      ) != Messages.OK) return

    settings.reset()

    ApplicationManager.getApplication().invokeLater {
      if (fileAssociationsEditor != null) {
        (fileAssociationsEditor ?: return@invokeLater).reset(settings.selectedFileAssociations.getTheAssociations())
      }
      if (folderAssociationsEditor != null) {
        (folderAssociationsEditor ?: return@invokeLater).reset(settings.selectedFolderAssociations.getTheAssociations())
      }
    }
  }

  override fun apply() {
    super.apply()
    settings.apply(getFileAssociations(), getFolderAssociations())
  }

  override fun isModified(): Boolean {
    var isModified = super.isModified()
    if (fileAssociationsEditor != null) {
      isModified = isModified || settings.isFileIconsModified(fileAssociationsEditor!!.getModel().items)
    }
    if (folderAssociationsEditor != null) {
      isModified = isModified || settings.isFolderIconsModified(folderAssociationsEditor!!.getModel().items)
    }
    return isModified
  }

  private fun loadAssociations() {
    ApplicationManager.getApplication().invokeLater {
      if (fileAssociationsEditor != null) {
        (fileAssociationsEditor ?: return@invokeLater).reset(settings.selectedFileAssociations.getTheAssociations())
      }
      if (folderAssociationsEditor != null) {
        (folderAssociationsEditor ?: return@invokeLater).reset(settings.selectedFolderAssociations.getTheAssociations())
      }
    }
  }

  private fun getFileAssociations(): SelectedAssociations {
    assert(fileAssociationsEditor != null)
    return SelectedAssociations(IconType.FILE, fileAssociationsEditor!!.getModel().allItems)
  }

  private fun getFolderAssociations(): SelectedAssociations {
    assert(folderAssociationsEditor != null)
    return SelectedAssociations(IconType.FOLDER, folderAssociationsEditor!!.getModel().allItems)
  }

  /** Create the file icons. */
  private fun createFileIconsTable() {
    val itemEditor = AssociationsTableItemEditor()
    fileAssociationsEditor = AssociationsTableModelEditor(
      fileColumns,
      itemEditor,
      message("no.file.associations"),
      fileSearch,
      IconType.FILE
    )
    fileIconsTable = (fileAssociationsEditor ?: return).createComponent()
  }

  /** Create the folder icons. */
  private fun createFolderIconsTable() {
    val itemEditor = AssociationsTableItemEditor()
    folderAssociationsEditor = AssociationsTableModelEditor(folderColumns,
      itemEditor,
      message("no.folder.associations"),
      folderSearch,
      IconType.FOLDER)
    folderIconsTable = (folderAssociationsEditor ?: return).createComponent()
  }

  companion object {
    /** Configurable ID. */
    @NonNls
    const val ID: String = "AtomSelectConfig"
  }
}
