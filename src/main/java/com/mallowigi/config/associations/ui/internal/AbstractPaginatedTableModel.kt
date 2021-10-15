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

package com.mallowigi.config.associations.ui.internal

import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import javax.swing.RowSorter
import javax.swing.SortOrder
import kotlin.math.ceil
import kotlin.math.max

abstract class AbstractPaginatedTableModel<T>(
  columnNames: Array<ColumnInfo<*, *>>,
  items: List<T>,
  initialPageSize: Int = 20,
) : ListTableModel<T>(columnNames, items) {
  init {
    require(initialPageSize > 0) { "Page size must be positive, was $initialPageSize" }
  }

  var pageIndex: Int = 0
    private set

  var pageSize: Int = initialPageSize
    private set

  val pageCount: Int
    get() = max(1, ceil(getDataSize().toDouble() / pageSize).toInt())

  val isOnFirstPage: Boolean
    get() = pageIndex == 0
  val isOnLastPage: Boolean
    get() = pageIndex == pageCount - 1

  /**
   * Returns the size of the entire data set.
   *
   * This is used for page calculation.
   */
  fun getDataSize(): Int = items.size

  /**
   * Returns the value for the cell at [dataIndex] and [columnIndex].
   *
   * This is used by [getValueAt] for mapping a row in the current page to a data entry.
   *
   * @param dataIndex index of the data entry in the entire data set.
   */
  fun getDataValueAt(dataIndex: Int, columnIndex: Int): Any {
    val columnInfo = columnInfos[columnIndex]
    val item = getItem(dataIndex)

    return columnInfo.valueOf(item)!!

  }

  /**
   * Sorts the entire data set.
   *
   * Traditional JTables only sort the rows in view. When data are paginated we want to sort the entire data set instead of just the current
   * page. This is called on sort order change to pre-sort data before pagination logic kicks in.
   *
   * @param sortKeys current sort keys of the table's [RowSorter]
   */
  fun sortData(sortKeys: List<RowSorter.SortKey>) {
    if (sortKeys.isNotEmpty()) {
      val sortKey = sortKeys[0]
      if (sortKey.sortOrder == SortOrder.ASCENDING) {
        items.sortBy { sortKey.column }
      } else {
        items.sortByDescending { sortKey.column }
      }
    }
  }

  override fun getRowCount(): Int {
    return if (isOnLastPage) getDataSize() - pageSize * (pageCount - 1) else pageSize
  }

  override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
    val dataIndex = rowIndex + pageIndex * pageSize
    return getDataValueAt(dataIndex, columnIndex)
  }

  fun goToFirstPage() {
    if (!isOnFirstPage) {
      pageIndex = 0
      fireTableDataChanged()
    }
  }

  fun goToLastPage() {
    if (!isOnLastPage) {
      pageIndex = pageCount - 1
      fireTableDataChanged()
    }
  }

  fun goToPrevPage() {
    if (!isOnFirstPage) {
      --pageIndex
      fireTableDataChanged()
    }
  }

  fun goToNextPage() {
    if (!isOnLastPage) {
      ++pageIndex
      fireTableDataChanged()
    }
  }

  fun updatePageSize(newPageSize: Int) {
    if (newPageSize != pageSize) {
      pageSize = newPageSize
      // Page size has changed so reset the page index.
      pageIndex = 0
      fireTableDataChanged()
    }
  }
}
