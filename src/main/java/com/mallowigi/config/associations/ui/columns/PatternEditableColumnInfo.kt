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
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.cellvalidators.ValidatingTableCellRendererWrapper
import com.intellij.ui.components.fields.ExtendableTextField
import com.intellij.util.ui.table.TableModelEditor.EditableColumnInfo
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.config.associations.ui.internal.RegexpEditor
import com.mallowigi.icons.associations.Association
import java.util.regex.Pattern
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

/**
 * Editable column info for association pattern
 *
 * @property parent
 */
@Suppress("UnstableApiUsage")
class PatternEditableColumnInfo(private val parent: Disposable, private val editable: Boolean) :
  EditableColumnInfo<Association, String>(message("AssociationsForm.folderIconsTable.columns.pattern")) {
  override fun valueOf(item: Association): String = item.matcher

  override fun setValue(item: Association, value: String?) {
    item.matcher = value!!
  }

  override fun getEditor(item: Association): TableCellEditor {
    val cellEditor = ExtendableTextField()

    return RegexpEditor(cellEditor, parent)
  }

  override fun getRenderer(item: Association): TableCellRenderer? {
    return ValidatingTableCellRendererWrapper(DefaultTableCellRenderer())
      .withCellValidator { value: Any?, _: Int, _: Int -> validate(value) }
  }

  override fun isCellEditable(item: Association): Boolean = editable

  private fun validate(value: Any?): ValidationInfo? {
    return when {
      value == null || value == ""      -> ValidationInfo(message("AtomAssocConfig.PatternEditor.empty"))
      !isValidPattern(value.toString()) -> ValidationInfo(message("AtomAssocConfig.PatternEditor.invalid"))
      else                              -> null
    }
  }

  private fun isValidPattern(pattern: String): Boolean = try {
    Pattern.compile(pattern)
    true
  } catch (e: RuntimeException) {
    false
  }

}
