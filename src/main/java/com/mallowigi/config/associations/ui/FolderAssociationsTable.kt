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
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.providers.DefaultFolderIconProvider
import icons.MTIcons
import javax.swing.Icon
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel

internal class FolderAssociationsTable : JBTable(MyTableModel(DefaultFolderIconProvider.associations.associations)) {
  private fun initColumns() {
    val iconColumn = getColumnModel().getColumn(ICON_COLUMN)
    iconColumn.cellRenderer = object : IconTableCellRenderer<String>() {
      override fun getIcon(value: String, table: JTable, row: Int): Icon {
        return MTIcons.getFolderIcon(value)
      }
    }
  }

  private class MyTableModel(private val associations: List<Association>) : AbstractTableModel() {
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
      val entry = associations[rowIndex]
      return when (columnIndex) {
        NAME_COLUMN    -> entry.name
        ICON_COLUMN    -> entry.icon
        PATTERN_COLUMN -> entry.matcher
        else           -> null
      }
    }

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