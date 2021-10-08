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

import com.intellij.openapi.util.Comparing
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import com.mallowigi.icons.associations.Association

/**
 * Table Model for the Association table
 *
 * @constructor
 *
 * @param columnNames the list of [ColumnInfo]
 * @param items Associations
 */
internal class AssociationTableModel<T : Association>(columnNames: Array<ColumnInfo<*, *>>, items: List<T>) :
  ListTableModel<T>(columnNames, items) {

  internal var dataChangedListener: AssociationsDataChangedListener<T>? = null

  /**
   * Set the value at the given row/column
   *
   * @param aValue value to set
   * @param rowIndex row number
   * @param columnIndex column number
   */
  override fun setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int) {
    if (rowIndex < rowCount) {
      val columnInfo = columnInfos[columnIndex] as ColumnInfo<T, Any>
      val item = getItem(rowIndex)
      val oldValue = columnInfo.valueOf(item)

      val comparator = when (columnInfo.columnClass) {
        String::class.java -> Comparing.strEqual(oldValue as String?, aValue as String)
        else               -> Comparing.equal(oldValue, aValue)
      }

      if (!comparator) {
        columnInfo.setValue(item, aValue)
        dataChangedListener?.dataChanged(columnInfo, rowIndex)
      }
    }
  }

}

