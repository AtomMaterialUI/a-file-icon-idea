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

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.util.ui.AbstractTableCellEditor
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable

/** Custom icon cell editor. */
class CustomIconCellEditor : AbstractTableCellEditor() {
  private var value: String? = null

  override fun getCellEditorValue(): String? = value

  override fun getTableCellEditorComponent(
    table: JTable,
    value: Any?,
    isSelected: Boolean,
    row: Int,
    column: Int
  ): Component {
    this.value = value as? String

    val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
      .withFileFilter { it.extension != null && FileUtilRt.getExtension(it.name) == "svg" }

    FileChooser.chooseFile(descriptor, null, null) {
      this.value = it.path
      fireEditingStopped()
    }
    return JLabel()
  }

}
