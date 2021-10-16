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
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.util.PathUtil
import com.intellij.util.ui.LocalPathCellEditor
import com.intellij.util.ui.table.IconTableCellRenderer
import com.intellij.util.ui.table.TableModelEditor.EditableColumnInfo
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.icons.associations.Association
import com.mallowigi.utils.getModifiedColor
import icons.AtomIcons
import java.io.IOException
import javax.swing.Icon
import javax.swing.JTable
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

/**
 * Abstract class for Icon Editable Column
 *
 * @constructor Create empty Icon editable column info
 */
@Suppress("unused")
abstract class IconEditableColumnInfo(private val parent: Disposable, private val editable: Boolean) :
  EditableColumnInfo<Association, String>(message("AssociationsForm.folderIconsTable.columns.icon")) {

  /**
   * Gets the column value from the path name
   *
   * @param item the [Association]
   * @return the full path
   */
  override fun valueOf(item: Association): String? = PathUtil.toSystemDependentName(item.icon)

  /**
   * Set column value (sets the icon from the path)
   *
   * @param item [Association] to set
   * @param value the path name
   */
  override fun setValue(item: Association, value: String?) {
    if (value != null) {
      item.touched = true
      item.icon = value
    }
  }

  /**
   * Creates an editor with a file chooser
   *
   * @param item the [Association]
   * @return the [TableCellEditor]
   */
  override fun getEditor(item: Association): TableCellEditor? =
    LocalPathCellEditor().fileChooserDescriptor(DESCRIPTOR).normalizePath(true)

  /**
   * Creates a [TableCellRenderer] that displays the icon with it's path
   *
   * @param item the [Association]
   * @return the [TableCellRenderer]
   */
  override fun getRenderer(item: Association): TableCellRenderer? {
    if (item.icon.isEmpty() || FileUtilRt.getExtension(item.icon) != "svg") return null

    return object : IconTableCellRenderer<String>() {
      override fun getIcon(value: String, table: JTable, row: Int): Icon? = try {
        val icon = loadIcon(value)
        if (icon.iconHeight == 1) AtomIcons.loadSVGIcon(value) else icon
      } catch (e: IOException) {
        null
      }

      override fun getText(): String = PathUtil.getFileName(item.icon)

      override fun repaint() {
        if (item.touched) {
          foreground = getModifiedColor()
        }
      }
    }
  }

  /**
   * Load icon from the relevant folder (files/folders)
   *
   * @param path
   * @return the icon
   */
  abstract fun loadIcon(path: String): Icon

  /**
   * Prevents cell to be editable
   *
   * @param item the [Association]
   * @return true if editable
   */
  override fun isCellEditable(item: Association): Boolean = editable

  companion object {
    private val DESCRIPTOR = FileChooserDescriptorFactory.createSingleFileDescriptor(
      FileTypeManager.getInstance().getStdFileType("SVG")
    )
  }

}
