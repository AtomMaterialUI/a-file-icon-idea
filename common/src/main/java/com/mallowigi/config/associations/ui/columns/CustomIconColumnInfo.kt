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

package com.mallowigi.config.associations.ui.columns

import com.intellij.icons.AllIcons
import com.intellij.util.ui.table.IconTableCellRenderer
import com.intellij.util.ui.table.TableModelEditor.EditableColumnInfo
import com.mallowigi.config.associations.ui.internal.CustomIconCellEditor
import com.mallowigi.icons.associations.Association
import javax.swing.Icon
import javax.swing.JTable
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

/** Custom icon column info. */
class CustomIconColumnInfo : EditableColumnInfo<Association, String>("") {
  private val editor = CustomIconCellEditor()

  override fun valueOf(item: Association): String = item.icon

  override fun setValue(item: Association, value: String?) {
    if (value != null) {
      item.icon = value
      item.touched = true
    }
  }

  override fun getEditor(item: Association): TableCellEditor = editor

  override fun getRenderer(item: Association): TableCellRenderer = object : IconTableCellRenderer<String>() {
    override fun getIcon(value: String, table: JTable, row: Int): Icon = AllIcons.General.OpenDisk
  }

  override fun getWidth(table: JTable?): Int = 32

  override fun isCellEditable(item: Association): Boolean = true
}
