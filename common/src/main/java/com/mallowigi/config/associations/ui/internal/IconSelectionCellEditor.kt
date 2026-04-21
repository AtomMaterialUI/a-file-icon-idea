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
package com.mallowigi.config.associations.ui.internal

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.ComboboxSpeedSearch
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.util.PathUtil
import com.intellij.util.ui.AbstractTableCellEditor
import java.awt.Component
import javax.swing.Icon
import javax.swing.JTable

/** Icon selection cell editor. */
class IconSelectionCellEditor(icons: List<String>, private val loadIcon: (String) -> Icon) : AbstractTableCellEditor() {

  private val comboBox = ComboBox(icons.toTypedArray())
  private val iconCache = mutableMapOf<String, Icon?>()

  init {
    comboBox.isEditable = true
    comboBox.renderer = object : SimpleListCellRenderer<String>() {
      override fun customize(
        list: javax.swing.JList<out String>,
        value: String?,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean
      ) {
        if (value == null) return

        text = PathUtil.getFileName(value)
        icon = iconCache.computeIfAbsent(value) {
          try {
            loadIcon(value)
          } catch (e: Exception) {
            null
          }
        }
      }
    }
    ComboboxSpeedSearch.installSpeedSearch(comboBox) { it }
  }

  override fun getCellEditorValue(): String? = comboBox.item

  override fun getTableCellEditorComponent(
    table: JTable,
    value: Any?,
    isSelected: Boolean,
    row: Int,
    column: Int
  ): Component {
    comboBox.item = value as? String ?: ""
    return comboBox
  }
}
