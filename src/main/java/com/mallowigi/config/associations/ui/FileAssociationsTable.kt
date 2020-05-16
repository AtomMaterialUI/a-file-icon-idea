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
package com.mallowigi.config.associations.ui

import com.intellij.openapi.ui.StripeTable
import com.intellij.ui.TableSpeedSearch
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.table.IconTableCellRenderer
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.icons.FileIconProvider
import com.mallowigi.icons.associations.Association
import icons.MTIcons
import javax.swing.Icon
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

internal class FileAssociationsTable : JBTable(MyTableModel(FileIconProvider.getAssociations().associations)) {
  private fun initColumns() {
    val iconColumn = getColumnModel().getColumn(ICON_COLUMN)
    iconColumn.cellRenderer = object : IconTableCellRenderer<String>() {
      override fun getIcon(value: String, table: JTable, row: Int): Icon = MTIcons.getFileIcon(value)
    }
  }

  private class MyTableModel(private val associations: List<Association>) : TableModel {
    override fun getRowCount(): Int = associations.size

    override fun getColumnCount(): Int = 3

    override fun getColumnName(columnIndex: Int): String {
      return when (columnIndex) {
        NAME_COLUMN    -> message("AssociationsForm.folderIconsTable.columns.name")
        ICON_COLUMN    -> message("AssociationsForm.folderIconsTable.columns.icon")
        PATTERN_COLUMN -> message("AssociationsForm.folderIconsTable.columns.pattern")
        else           -> ""
      }
    }

    override fun getColumnClass(columnIndex: Int): Class<*> = String::class.java

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = false

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
      val entry = associations[rowIndex] ?: return null
      return when (columnIndex) {
        NAME_COLUMN    -> entry.name
        ICON_COLUMN    -> entry.icon
        PATTERN_COLUMN -> entry.matcher
        else           -> null
      }
    }

    override fun setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int) {}
    override fun addTableModelListener(l: TableModelListener) {}
    override fun removeTableModelListener(l: TableModelListener) {}

  }

  companion object {
    private const val NAME_COLUMN = 0
    private const val ICON_COLUMN = 1
    private const val PATTERN_COLUMN = 2
  }

  init {
    StripeTable.apply(this)
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    TableSpeedSearch(this)
    setEnableAntialiasing(true)
    preferredScrollableViewportSize = JBUI.size(200, -1)
    initColumns()
  }
}