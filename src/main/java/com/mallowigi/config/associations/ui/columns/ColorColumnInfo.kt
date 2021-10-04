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
import com.intellij.ui.ColorUtil
import com.intellij.util.ui.table.TableModelEditor.EditableColumnInfo
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.icons.associations.Association
import java.awt.Color
import javax.swing.JTextField
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

/**
 * Editable column for the color
 * @property parent the Parent class
 * @property editable whether the column should be editable
 */
class ColorColumnInfo(private val parent: Disposable, private val editable: Boolean) :
  EditableColumnInfo<Association, Color?>(message("AssociationsForm.folderIconsTable.columns.color")) {

  /**
   * The value of the column is the color
   *
   * @param item the [Association]
   * @return the color
   */
  override fun valueOf(item: Association): Color? = item.color

  /**
   * Set the [Association]'s color. Must be > 0
   *
   * @param item the [Association]
   * @param value the new value
   */
  override fun setValue(item: Association, value: Color?) {
    item.color = value
  }

  /**
   * Creates an editor for the color, with empty value validation
   *
   * @param item the [Association]
   * @return the [TableCellEditor]
   */
  override fun getEditor(item: Association): TableCellEditor {
    val cellEditor = JTextField()
    return StatefulValidatingCellEditor(cellEditor, parent)
  }

  /**
   * Creates a renderer for the color with validation
   *
   * @param item the [Association]
   * @return the [TableCellRenderer]
   */
  override fun getRenderer(item: Association): TableCellRenderer? =
    ValidatingTableCellRendererWrapper(object : DefaultTableCellRenderer() {
      override fun repaint() {
        if (item.color != null) {
          background = item.color
          foreground = Color.white
          text = ColorUtil.toHex(background)
        }
      }
    })
      .withCellValidator { value: Any?, _: Int, _: Int -> validate(value as Color?) }

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
  private fun validate(value: Color?): ValidationInfo? {
    return when {
      value == null -> ValidationInfo(message("AtomAssocConfig.ColorEditor.empty"))
//      value.toIntOrNull() == null  -> ValidationInfo(message("AtomAssocConfig.PriorityEditor.invalidNumber"))
      else          -> null
    }
  }
}
