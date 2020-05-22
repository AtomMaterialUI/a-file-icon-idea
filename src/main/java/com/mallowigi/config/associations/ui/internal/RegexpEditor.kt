/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2020 Elior "Mallowigi" Boukhobza
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package com.mallowigi.config.associations.ui.internal

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.cellvalidators.StatefulValidatingCellEditor
import com.intellij.openapi.ui.cellvalidators.ValidatingTableCellRendererWrapper
import com.intellij.openapi.util.Disposer
import com.intellij.ui.EditorTextField
import org.intellij.lang.regexp.RegExpFileType
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.util.function.Consumer
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.KeyStroke
import javax.swing.table.TableCellEditor

class RegexpEditor(textField: JTextField,
                   parent: Disposable) : StatefulValidatingCellEditor(textField, parent), TableCellEditor {
  private var editor: EditorTextField
  private var myDocument: Document? = null
  private val stateUpdater = Consumer { _: ValidationInfo? -> }

  init {
    // Creates a regex editor
    editor = EditorTextField(ProjectManager.getInstance().defaultProject, RegExpFileType.INSTANCE)
    editor.setOneLineMode(true)
    // Register enter and escape keys
    editor.registerKeyboardAction(ActionListener { _: ActionEvent? -> stopCellEditing() },
      KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
      JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    editor.registerKeyboardAction(ActionListener { _: ActionEvent? -> cancelCellEditing() },
      KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
      JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)

    myDocument = editor.document
    clickCountToStart = 2

    // Install validations
    ComponentValidator(parent).withValidator(this).installOn(editor)

    // Reset validations when document changes but revalidates once blurreed
    val dl: DocumentListener = object : DocumentListener {
      override fun documentChanged(event: DocumentEvent) {
        editor.putClientProperty(ValidatingTableCellRendererWrapper.CELL_VALIDATION_PROPERTY, null)
        ComponentValidator.getInstance(editor).ifPresent { obj: ComponentValidator -> obj.revalidate() }
      }
    }

    myDocument!!.addDocumentListener(dl)
    Disposer.register(parent, Disposable { myDocument!!.removeDocumentListener(dl) })
  }

  override fun getTableCellEditorComponent(table: JTable,
                                           value: Any,
                                           isSelected: Boolean,
                                           row: Int,
                                           column: Int): Component {

    editor.text = value.toString()
    // Install validations on renderer
    val renderer = table.getCellRenderer(row, column)
      .getTableCellRendererComponent(table, value, isSelected, true, row, column) as JComponent

    val validationProperty = renderer.getClientProperty(ValidatingTableCellRendererWrapper.CELL_VALIDATION_PROPERTY)
    if (validationProperty != null) {
      val cellInfo = validationProperty as ValidationInfo
      // Add validation info
      editor.putClientProperty(ValidatingTableCellRendererWrapper.CELL_VALIDATION_PROPERTY, cellInfo.forComponent(editor))

      // Revalidates
      ComponentValidator.getInstance(editor).ifPresent { obj: ComponentValidator -> obj.revalidate() }
    }
    return editor
  }

  override fun getCellEditorValue(): Any {
    return myDocument!!.text
  }

  override fun stopCellEditing(): Boolean {
    // Revalidates on blur
    ComponentValidator.getInstance(editor).ifPresent { obj: ComponentValidator -> obj.revalidate() }
    fireEditingStopped()
    return true
  }

  override fun cancelCellEditing() {
    // Revalidates on cancel
    ComponentValidator.getInstance(editor).ifPresent { obj: ComponentValidator -> obj.revalidate() }
    fireEditingCanceled()
  }

  /**
   * Executes validations
   */
  override fun get(): ValidationInfo? {
    val validationProperty = editor.getClientProperty(ValidatingTableCellRendererWrapper.CELL_VALIDATION_PROPERTY)
    if (validationProperty != null) {
      val info = validationProperty as ValidationInfo
      stateUpdater.accept(info)
      return info
    }
    return null
  }

  override fun getComponent(): Component {
    return editor
  }
}