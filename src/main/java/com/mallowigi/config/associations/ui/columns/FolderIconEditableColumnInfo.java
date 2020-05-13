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

package com.mallowigi.config.associations.ui.columns;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.util.PathUtil;
import com.intellij.util.ui.LocalPathCellEditor;
import com.intellij.util.ui.table.IconTableCellRenderer;
import com.intellij.util.ui.table.TableModelEditor;
import com.mallowigi.config.AtomSettingsBundle;
import com.mallowigi.icons.associations.Association;
import icons.MTIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public final class FolderIconEditableColumnInfo extends TableModelEditor.EditableColumnInfo<Association, String> {
  private static final FileChooserDescriptor APP_FILE_CHOOSER_DESCRIPTOR =
    FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor();

  public FolderIconEditableColumnInfo() {
    super(AtomSettingsBundle.message("AssociationsForm.folderIconsTable.columns.icon"));
  }

  @Override
  public String valueOf(final Association item) {
    return PathUtil.toSystemDependentName(item.getIcon());
  }

  @Override
  public void setValue(final Association item, final String value) {
    item.setIcon(value);
  }

  @Nullable
  @Override
  public TableCellEditor getEditor(final Association item) {
    return new LocalPathCellEditor().fileChooserDescriptor(APP_FILE_CHOOSER_DESCRIPTOR).normalizePath(true);
  }

  @NotNull
  @Override
  public TableCellRenderer getRenderer(final Association item) {
    return new IconTableCellRenderer<String>() {
      @NotNull
      @Override
      protected Icon getIcon(@NotNull final String value, final JTable table, final int row) {
        return MTIcons.getFolderIcon(value);
      }
    };
  }
}
