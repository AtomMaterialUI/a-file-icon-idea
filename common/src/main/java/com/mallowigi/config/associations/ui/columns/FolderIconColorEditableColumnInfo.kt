/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.config.associations.ui.columns

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.cellvalidators.StatefulValidatingCellEditor
import com.intellij.ui.ColorUtil
import com.intellij.ui.components.fields.ExtendableTextField
import com.intellij.util.ui.table.TableModelEditor.EditableColumnInfo
import com.mallowigi.config.AtomSettingsBundle
import com.mallowigi.icons.associations.Association
import java.awt.Color
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

/** Column info for the icon of a **File Icon Association**. Displays the icon path alongside the icon. */
class FolderIconColorEditableColumnInfo(private val parent: Disposable) :
  EditableColumnInfo<Association, String>(AtomSettingsBundle.message("AssociationsForm.folderIconsTable.columns.iconColor")) {

  /** Returns the value to display in the column. */
  override fun valueOf(item: Association): String = item.folderIconColor ?: "808080"

  /**
   * Set column value (sets the color)
   *
   * @param item [Association] to set
   * @param value the color
   */
  override fun setValue(item: Association, value: String?) {
    if (value != null) {
      item.touched = true
      item.folderIconColor = value
    }
  }

  /** Returns the renderer for the column. */
  override fun getRenderer(item: Association): TableCellRenderer = object : DefaultTableCellRenderer() {
    override fun repaint() {
      background = ColorUtil.fromHex(item.folderIconColor ?: "808080")
      foreground = when (ColorUtil.isDark(background)) {
        true  -> Color.WHITE
        false -> Color.BLACK
      }
    }
  }

  /** Returns the editor for the column. */
  override fun getEditor(item: Association): TableCellEditor {
    val cellEditor = ExtendableTextField()
    return StatefulValidatingCellEditor(cellEditor, parent)
  }

  /** Is the column editable? */
  override fun isCellEditable(item: Association): Boolean = false

  /** Column width. */
  override fun getWidth(table: JTable?): Int = 50

  /** Needed. */
  override fun getComparator(): Comparator<Association> = Comparator.comparingInt { c: Association -> c.priority }
}
