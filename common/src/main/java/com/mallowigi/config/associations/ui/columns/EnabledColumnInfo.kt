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

import com.intellij.util.ui.table.TableModelEditor.EditableColumnInfo
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.icons.associations.Association
import javax.swing.DefaultCellEditor
import javax.swing.JCheckBox
import javax.swing.JTable
import javax.swing.table.TableCellEditor

/**
 * Column info for the enabled state
 */
class EnabledColumnInfo :
  EditableColumnInfo<Association, Boolean>(message("AssociationsForm.folderIconsTable.columns.enabled")) {

  /**
   * Gets the enabled state of the [Association]
   */
  override fun valueOf(item: Association): Boolean = item.enabled

  /**
   * Enable the [Association]
   */
  override fun setValue(item: Association, value: Boolean) {
    item.touched = true
    item.enabled = value
  }

  /**
   * Edit this state by a checkbox
   */
  override fun getEditor(item: Association): TableCellEditor = DefaultCellEditor(JCheckBox())

  /**
   * The column class of this state
   */
  override fun getColumnClass(): Class<Boolean> = Boolean::class.java

  /**
   * Sets the table width for a checkbox
   */
  override fun getWidth(table: JTable?): Int = 16

  /**
   * No name for this column
   */
  override fun getName(): String = ""

}
