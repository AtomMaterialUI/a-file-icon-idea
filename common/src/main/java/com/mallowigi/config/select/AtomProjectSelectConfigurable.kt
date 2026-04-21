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

package com.mallowigi.config.select

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
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

/** Configurable for Project Custom Associations. */
class AtomProjectSelectConfigurable(project: Project) : BoundSearchableConfigurable(
  message("AtomProjectSelectForm.title"),
  "com.mallowigi.config.AtomProjectSelectConfig",
), Disposable {
  private var main: DialogPanel
  private val settings = AtomProjectSelectConfig.getInstance(project)
  private lateinit var tabbedPane: JBTabbedPane

  // Panels for the tables
  private var fileAssociationsPanel: JPanel
  private var folderAssociationsPanel: JPanel
  private var folderOpenAssociationsPanel: JPanel
  private var psiAssociationsPanel: JPanel

  // Search boxes
  private var fileSearch: SearchTextField = SearchTextField()
  private var folderSearch: SearchTextField = SearchTextField()
  private var folderOpenSearch: SearchTextField = SearchTextField()
  private var psiSearch: SearchTextField = SearchTextField()

  // Tables
  private lateinit var fileIconsTable: JComponent
  private lateinit var folderIconsTable: JComponent
  private lateinit var folderOpenIconsTable: JComponent
  private lateinit var psiIconsTable: JComponent

  // Editors
  private var fileAssociationsEditor: AssociationsTableModelEditor? = null
  private var folderAssociationsEditor: AssociationsTableModelEditor? = null
  private var folderOpenAssociationsEditor: AssociationsTableModelEditor? = null
  private var psiAssociationsEditor: AssociationsTableModelEditor? = null

  // Columns
  private val fileColumns = arrayOf<ColumnInfo<*, *>>(
    EnabledColumnInfo(),
    TouchedColumnInfo(),
    NameEditableColumnInfo(this, true),
    PatternEditableColumnInfo(this, true),
    FileIconEditableColumnInfo(this, true),
    CustomIconColumnInfo(),
    PriorityColumnInfo(this, true),
    IconColorEditableColumnInfo(this)
  )

  private val folderColumns = arrayOf<ColumnInfo<*, *>>(
    EnabledColumnInfo(),
    TouchedColumnInfo(),
    NameEditableColumnInfo(this, true),
    PatternEditableColumnInfo(this, true),
    FolderIconEditableColumnInfo(this, true),
    CustomIconColumnInfo(),
    PriorityColumnInfo(this, true),
    FolderColorEditableColumnInfo(this),
    FolderIconColorEditableColumnInfo(this)
  )

  private val folderOpenColumns = arrayOf<ColumnInfo<*, *>>(
    EnabledColumnInfo(),
    TouchedColumnInfo(),
    NameEditableColumnInfo(this, true),
    PatternEditableColumnInfo(this, true),
    FolderIconEditableColumnInfo(this, true),
    CustomIconColumnInfo(),
    PriorityColumnInfo(this, true),
    FolderColorEditableColumnInfo(this),
    FolderIconColorEditableColumnInfo(this)
  )

  private val psiColumns = arrayOf<ColumnInfo<*, *>>(
    EnabledColumnInfo(),
    TouchedColumnInfo(),
    NameEditableColumnInfo(this, true),
    PsiPathEditableColumnInfo(true),
    PsiIconEditableColumnInfo(this, true),
    CustomIconColumnInfo(),
    PriorityColumnInfo(this, true)
  )

  init {
    createFileIconsTable()
    createFolderIconsTable()
    createFolderOpenIconsTable()
    createPsiIconsTable()

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

    folderOpenAssociationsPanel = panel {
      row {
        cell(folderOpenSearch)
          .align(Align.FILL)
      }

      row {
        cell(folderOpenIconsTable)
          .resizableColumn()
          .align(Align.FILL)
      }
    }

    psiAssociationsPanel = panel {
      row {
        cell(psiSearch)
          .align(Align.FILL)
      }

      row {
        cell(psiIconsTable)
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

    fileSearch.textEditor.emptyText.text = message("fileSearch.placeholder")
    folderSearch.textEditor.emptyText.text = message("fileSearch.placeholder")
    folderOpenSearch.textEditor.emptyText.text = message("fileSearch.placeholder")
    psiSearch.textEditor.emptyText.text = message("fileSearch.placeholder")

    tabbedPane.addTab(message("SelectForm.fileAssociationsPanel.tab.title"), fileAssociationsPanel)
    tabbedPane.addTab(message("SelectForm.folderAssociationsPanel.tab.title"), folderAssociationsPanel)
    tabbedPane.addTab(message("SelectForm.folderOpenAssociationsPanel.tab.title"), folderOpenAssociationsPanel)
    tabbedPane.addTab(message("SelectForm.psiAssociationsPanel.tab.title"), psiAssociationsPanel)
  }

  /** Configurable display name. */
  @Nls
  override fun getDisplayName(): String = message("AtomProjectSelectForm.title")

  /** Configurable ID. */
  override fun getId(): String = ID

  /** Create the file icons table. */
  override fun createPanel(): DialogPanel {
    loadAssociations()
    return main
  }

  /** Dispose editors. */
  override fun dispose() {
    fileAssociationsEditor = null
    folderAssociationsEditor = null
    folderOpenAssociationsEditor = null
    psiAssociationsEditor = null
  }

  private fun resetSettings() {
    if (Messages.showOkCancelDialog(
        /* message = */ message("SelectForm.resetDialog.text"),
        /* title = */ message("SelectForm.resetDialog.title"),
        /* okText = */ message("SelectForm.resetDialog.ok"),
        /* cancelText = */ message("SelectForm.resetDialog.cancel"),
        /* icon = */ Messages.getQuestionIcon()
      ) != Messages.OK
    ) return

    settings.reset()

    ApplicationManager.getApplication().invokeLater {
      if (fileAssociationsEditor != null) {
        (fileAssociationsEditor ?: return@invokeLater).reset(settings.selectedFileAssociations.getTheAssociations())
      }
      if (folderAssociationsEditor != null) {
        (folderAssociationsEditor ?: return@invokeLater).reset(settings.selectedFolderAssociations.getTheAssociations())
      }
      if (folderOpenAssociationsEditor != null) {
        (folderOpenAssociationsEditor
          ?: return@invokeLater).reset(settings.selectedFolderOpenAssociations.getTheAssociations())
      }
      if (psiAssociationsEditor != null) {
        (psiAssociationsEditor
          ?: return@invokeLater).reset(settings.selectedPsiAssociations.getTheAssociations())
      }
    }
  }

  /** Apply. */
  override fun apply() {
    super.apply()
    settings.apply(getFileAssociations(), getFolderAssociations(), getFolderOpenAssociations(), getPsiAssociations())
  }

  /** Detect if settings have been modified. */
  override fun isModified(): Boolean {
    var isModified = super.isModified()
    if (fileAssociationsEditor != null) {
      isModified = isModified || settings.isFileIconsModified(fileAssociationsEditor!!.getModel().items)
    }
    if (folderAssociationsEditor != null) {
      isModified = isModified || settings.isFolderIconsModified(folderAssociationsEditor!!.getModel().items)
    }
    if (folderOpenAssociationsEditor != null) {
      isModified = isModified || settings.isFolderOpenIconsModified(folderOpenAssociationsEditor!!.getModel().items)
    }
    if (psiAssociationsEditor != null) {
      isModified = isModified || settings.isPsiIconsModified(psiAssociationsEditor!!.getModel().items)
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
      if (folderOpenAssociationsEditor != null) {
        (folderOpenAssociationsEditor
          ?: return@invokeLater).reset(settings.selectedFolderOpenAssociations.getTheAssociations())
      }
      if (psiAssociationsEditor != null) {
        (psiAssociationsEditor
          ?: return@invokeLater).reset(settings.selectedPsiAssociations.getTheAssociations())
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

  private fun getFolderOpenAssociations(): SelectedAssociations {
    assert(folderOpenAssociationsEditor != null)
    return SelectedAssociations(IconType.FOLDER_OPEN, folderOpenAssociationsEditor!!.getModel().allItems)
  }

  private fun getPsiAssociations(): SelectedAssociations {
    assert(psiAssociationsEditor != null)
    return SelectedAssociations(IconType.PSI, psiAssociationsEditor!!.getModel().allItems)
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
    folderAssociationsEditor = AssociationsTableModelEditor(
      folderColumns,
      itemEditor,
      message("no.folder.associations"),
      folderSearch,
      IconType.FOLDER
    )
    folderIconsTable = (folderAssociationsEditor ?: return).createComponent()
  }

  private fun createFolderOpenIconsTable() {
    val itemEditor = AssociationsTableItemEditor()
    folderOpenAssociationsEditor = AssociationsTableModelEditor(
      folderOpenColumns,
      itemEditor,
      message("no.folder.associations"),
      folderOpenSearch,
      IconType.FOLDER_OPEN
    )
    folderOpenIconsTable = (folderOpenAssociationsEditor ?: return).createComponent()
  }

  private fun createPsiIconsTable() {
    val itemEditor = AssociationsTableItemEditor()
    psiAssociationsEditor = AssociationsTableModelEditor(
      psiColumns,
      itemEditor,
      message("AssociationsForm.psiIconsTable.emptyText"),
      psiSearch,
      IconType.PSI
    )
    psiIconsTable = (psiAssociationsEditor ?: return).createComponent()
  }

  companion object {
    /** Configurable ID. */
    @NonNls
    const val ID: String = "AtomProjectSelectConfig"
  }
}
