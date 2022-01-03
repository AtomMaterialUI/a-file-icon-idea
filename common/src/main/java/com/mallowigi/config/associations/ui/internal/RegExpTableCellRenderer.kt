/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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

package com.mallowigi.config.associations.ui.internal

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.intellij.ui.EditorTextField
import com.intellij.util.ui.JBUI
import org.intellij.lang.regexp.RegExpFileType
import java.awt.Component
import javax.swing.BorderFactory
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

/**
 * Preview regexps in the table
 *
 */
class RegExpTableCellRenderer : DefaultTableCellRenderer() {
  private val project: Project
    get() = ProjectManager.getInstance().defaultProject

  private val factory: PsiFileFactory
    get() = PsiFileFactory.getInstance(project)

  private val documentManager: PsiDocumentManager
    get() = PsiDocumentManager.getInstance(project)

  /**
   * Regex editor for the table cell
   */
  override fun getTableCellRendererComponent(
    table: JTable?,
    value: Any?,
    isSelected: Boolean,
    hasFocus: Boolean,
    row: Int,
    column: Int,
  ): Component {
    val psiFile =
      factory.createFileFromText(
        /* language = */ RegExpFileType.INSTANCE.language,
        /* text = */ value as String
      )
    val editorTextField: EditorTextField

    editorTextField = object : EditorTextField(
      /* document = */ documentManager.getDocument(psiFile),
      /* project = */ project,
      /* fileType = */ RegExpFileType.INSTANCE,
      /* isViewer = */ true,
      /* oneLineMode = */ true) {
      override fun shouldHaveBorder(): Boolean = false
    }

    if (!table!!.isShowing) {
      editorTextField.ensureWillComputePreferredSize()
    }

    editorTextField.putClientProperty("JComboBox.isTableCellEditor", java.lang.Boolean.TRUE)
    editorTextField.isDoubleBuffered = true
    editorTextField.ignoreRepaint = true
    editorTextField.border =
      if (hasFocus || isSelected) BorderFactory.createLineBorder(table.selectionBackground) else JBUI.Borders.empty(1)
    if (isSelected && PsiDocumentManager.getInstance(project).getDocument(psiFile) != null) {
      val bg = table.selectionBackground
      val fg = table.selectionForeground
      editorTextField.background = bg
      editorTextField.foreground = fg
      editorTextField.setAsRendererWithSelection(bg, fg)
    }
    return editorTextField
  }
}

