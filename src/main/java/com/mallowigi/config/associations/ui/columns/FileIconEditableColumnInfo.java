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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.util.io.FileUtilRt;
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
import java.io.IOException;

public final class FileIconEditableColumnInfo extends TableModelEditor.EditableColumnInfo<Association, String> {
  private static final FileChooserDescriptor DESCRIPTOR =
    FileChooserDescriptorFactory.createSingleFileDescriptor(FileTypeManager.getInstance().getStdFileType("SVG"));
  private final Disposable parent;

  public FileIconEditableColumnInfo(final Disposable disposable) {
    super(AtomSettingsBundle.message("AssociationsForm.folderIconsTable.columns.icon"));
    parent = disposable;
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
    return new LocalPathCellEditor().fileChooserDescriptor(DESCRIPTOR).normalizePath(true);
  }

  @Override
  public TableCellRenderer getRenderer(final Association item) {
    if (item == null || item.getIcon().isEmpty() || !FileUtilRt.getExtension(item.getIcon()).equals("svg")) {
      return null;
    }
    return new IconTableCellRenderer<String>() {
      @Override
      protected Icon getIcon(@NotNull final String value, final JTable table, final int row) {
        try {
          return MTIcons.loadSVGIcon(value);
        }
        catch (final IOException e) {
          return null;
        }
      }

      @Override
      public String getText() {
        return PathUtil.getFileName(item.getIcon());
      }
    };
  }
}
