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
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.util.PathUtil
import com.intellij.util.ui.table.IconTableCellRenderer
import com.mallowigi.icons.associations.Association
import icons.MTIcons
import java.io.IOException
import javax.swing.Icon
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

/**
 * Icon editable column info
 *
 * @constructor Create empty Icon editable column info
 */
@Suppress("unused")
class FolderIconEditableColumnInfo(private val parent: Disposable, private val editable: Boolean) :
  IconEditableColumnInfo(parent, editable) {

  override fun getRenderer(item: Association?): TableCellRenderer? {
    if (item == null || item.icon.isEmpty() || FileUtilRt.getExtension(item.icon) != "svg") {
      return null
    } else {
      return object : IconTableCellRenderer<String>() {
        override fun getIcon(value: String, table: JTable, row: Int): Icon? {
          return try {
            MTIcons.getFolderIcon(value)
          } catch (e: IOException) {
            null
          }
        }

        override fun getText(): String = PathUtil.getFileName(item.icon)
      }
    }
  }
}
