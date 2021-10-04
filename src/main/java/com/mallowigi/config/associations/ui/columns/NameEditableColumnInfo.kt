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
import com.intellij.ui.components.fields.ExtendableTextField
import com.intellij.util.ui.table.TableModelEditor.EditableColumnInfo
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.icons.associations.Association
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

/**
 * Editable column info for [Association] name
 *
 * @property parent the Parent class
 * @property editable whether the column should be editable
 */
@Suppress("UnstableApiUsage")
class NameEditableColumnInfo(private val parent: Disposable, private val editable: Boolean) :
  EditableColumnInfo<Association, String>(message("AssociationsForm.folderIconsTable.columns.name")) {
  /**
   * The value of the column is the name
   *
   * @param item the [Association]
   * @return [Association] name
   */
  override fun valueOf(item: Association): String = item.name

  /**
   * Set [Association]'s name
   *
   * @param item the [Association]
   * @param value the new name
   */
  override fun setValue(item: Association, value: String) {
    item.name = value
  }

  /**
   * Creates an editor for the name, with empty value validation
   *
   * @param item the [Association]
   * @return the [TableCellEditor]
   */
  override fun getEditor(item: Association): TableCellEditor {
    val cellEditor = ExtendableTextField()
    return StatefulValidatingCellEditor(cellEditor, parent)
  }

  /**
   * Creates a renderer for the name: displays the name with a calidation tooltip if the name is empty
   *
   * @param item the [Association]
   * @return the [TableCellRenderer]
   */
  override fun getRenderer(item: Association): TableCellRenderer? {
    return ValidatingTableCellRendererWrapper(DefaultTableCellRenderer())
      .withCellValidator { value: Any?, _: Int, _: Int ->
        if (value == null || value == "") {
          return@withCellValidator ValidationInfo(message("AtomAssocConfig.NameEditor.empty"))
        } else {
          return@withCellValidator null
        }
      }
  }

  /**
   * Whether the cell is editable
   *
   * @param item
   * @return
   */
  override fun isCellEditable(item: Association): Boolean = editable
}
