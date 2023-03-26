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
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.ColumnInfo
import com.mallowigi.config.AtomSettingsBundle
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
  AtomSettingsBundle.message("AtomSelectForm.title"),
  "com.mallowigi.config.AtomSelectConfig",
), Disposable {
  private lateinit var main: DialogPanel
  private val settings = AtomSelectConfig.instance
  private lateinit var tabbedPane: JBTabbedPane

  // Panels for the tables
  private var fileAssociationsPanel: JPanel = JPanel()
  private var folderAssociationsPanel: JPanel = JPanel()

  // Search boxes
  private var fileSearch: SearchTextField = SearchTextField()
  private var folderSearch: SearchTextField = SearchTextField()

  // Tables
  private var fileIconsTable: JComponent? = null
  private var folderIconsTable: JComponent? = null

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

  fun init() {
    createTables()
    fileSearch.textEditor.emptyText.setText(message("fileSearch.placeholder"))
    folderSearch.textEditor.emptyText.setText(message("fileSearch.placeholder"))
    setFormState()
  }

  /** Configurable display name. */
  @Nls
  override fun getDisplayName(): String = AtomSettingsBundle.message("AtomSelectForm.title")

  /** Configurable ID. */
  override fun getId(): String = ID

  @Suppress("Detekt.LongMethod")
  override fun createPanel(): DialogPanel {
    main = panel {
      group("Associations Editor") {
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
          tabbedPane = tabbedPaneHeader(
            listOf(
              message("SelectForm.fileAssociationsPanel.tab.title"),
              message("SelectForm.folderAssociationsPanel.tab.title"),
            ),
          ).component
        }
      }
    }

    init()

    return main
  }

  override fun apply() {
    super.apply()
  }

  override fun dispose() {
    fileAssociationsEditor = null
    folderAssociationsEditor = null
  }

  override fun reset() {
    settings.reset()
    super.reset()

    ApplicationManager.getApplication().invokeLater {
      if (fileAssociationsEditor != null) {
        (fileAssociationsEditor ?: return@invokeLater).reset(settings.selectedFileAssociations.getTheAssociations())
      }
      if (folderAssociationsEditor != null) {
        (folderAssociationsEditor ?: return@invokeLater).reset(settings.selectedFolderAssociations.getTheAssociations())
      }
    }
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

  fun setFormState() {
    ApplicationManager.getApplication().invokeLater {
      if (fileAssociationsEditor != null) {
        (fileAssociationsEditor ?: return@invokeLater).reset(settings.selectedFileAssociations.getTheAssociations())
      }
      if (folderAssociationsEditor != null) {
        (folderAssociationsEditor ?: return@invokeLater).reset(settings.selectedFolderAssociations.getTheAssociations())
      }
    }
  }

  fun getFileAssociations(): SelectedAssociations {
    assert(fileAssociationsEditor != null)
    return SelectedAssociations(IconType.FILE, fileAssociationsEditor!!.getModel().allItems)
  }

  fun getFolderAssociations(): SelectedAssociations {
    assert(folderAssociationsEditor != null)
    return SelectedAssociations(IconType.FOLDER, folderAssociationsEditor!!.getModel().allItems)
  }

  private fun createTables() {
    createFileIconsTable()
    createFolderIconsTable()
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
    ApplicationManager.getApplication().invokeLater {
      fileIconsTable = (fileAssociationsEditor ?: return@invokeLater).createComponent()
      fileAssociationsPanel.add(fileIconsTable, "cell 0 1") //NON-NLS
    }
  }

  /** Create the folder icons. */
  private fun createFolderIconsTable() {
    val itemEditor = AssociationsTableItemEditor()
    folderAssociationsEditor = AssociationsTableModelEditor(folderColumns,
      itemEditor,
      message("no.folder.associations"),
      folderSearch,
      IconType.FOLDER)
    ApplicationManager.getApplication().invokeLater {
      folderIconsTable = (folderAssociationsEditor ?: return@invokeLater).createComponent()
      folderAssociationsPanel.add(folderIconsTable, "cell 0 1") //NON-NLS
    }
  }

  companion object {
    /** Configurable ID. */
    @NonNls
    const val ID: String = "AtomSelectConfig"
  }
}
