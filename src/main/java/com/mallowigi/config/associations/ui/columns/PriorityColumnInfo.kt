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
package com.mallowigi.config.associations.ui.columns

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.cellvalidators.StatefulValidatingCellEditor
import com.intellij.openapi.ui.cellvalidators.ValidatingTableCellRendererWrapper
import com.intellij.util.ui.table.TableModelEditor.EditableColumnInfo
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.icons.associations.Association
import javax.swing.JTextField
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

/**
 * Editable column for the priority
 * @property parent the Parent class
 * @property editable whether the column should be editable
 */
class PriorityColumnInfo(private val parent: Disposable, private val editable: Boolean) :
  EditableColumnInfo<Association, Int>(message("AssociationsForm.folderIconsTable.columns.priority")) {

  /**
   * The value of the column is the priority
   *
   * @param item the [Association]
   * @return the priority
   */
  override fun valueOf(item: Association): Int = item.priority

  /**
   * Set the [Association]'s priority. Must be > 0
   *
   * @param item the [Association]
   * @param value the new value
   */
  override fun setValue(item: Association, value: Int) {
    item.priority = value
  }

  /**
   * Creates an editor for the priority, with empty value validation
   *
   * @param item the [Association]
   * @return the [TableCellEditor]
   */
  override fun getEditor(item: Association): TableCellEditor {
    val cellEditor = JTextField()
    return StatefulValidatingCellEditor(cellEditor, parent)
  }

  /**
   * Creates a renderer for the priority with validation
   *
   * @param item the [Association]
   * @return the [TableCellRenderer]
   */
  override fun getRenderer(item: Association): TableCellRenderer? {
    return ValidatingTableCellRendererWrapper(DefaultTableCellRenderer())
      .withCellValidator { value: Any?, _: Int, _: Int -> validate(value as String?) }
  }

  /**
   * Whether the cell is editable
   *
   * @param item the [Association]
   * @return true if editable
   */
  override fun isCellEditable(item: Association): Boolean = editable

  /**
   * Returns the relevant validation message
   *
   * @param value
   * @return
   */
  private fun validate(value: String?): ValidationInfo? {
    return when {
      value == null || value == "" -> ValidationInfo(message("AtomAssocConfig.PriorityEditor.empty"))
      value.toIntOrNull() == null  -> ValidationInfo(message("AtomAssocConfig.PriorityEditor.invalidNumber"))
      value.toInt() <= 0           -> ValidationInfo(message("AtomAssocConfig.PriorityEditor.wrong"))
      else                         -> null
    }
  }
}
