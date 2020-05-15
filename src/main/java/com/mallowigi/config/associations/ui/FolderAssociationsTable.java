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

package com.mallowigi.config.associations.ui;

import com.intellij.openapi.ui.StripeTable;
import com.intellij.ui.TableSpeedSearch;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.table.IconTableCellRenderer;
import com.mallowigi.config.AtomSettingsBundle;
import com.mallowigi.icons.FileIconProvider;
import com.mallowigi.icons.associations.Association;
import icons.MTIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.util.List;

final class FolderAssociationsTable extends JBTable {
  private static final int NAME_COLUMN = 0;
  private static final int ICON_COLUMN = 1;
  private static final int PATTERN_COLUMN = 2;

  FolderAssociationsTable() {
    super(new MyTableModel(FileIconProvider.getDirAssociations().getAssociations()));
    StripeTable.apply(this);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    new TableSpeedSearch(this);

    setEnableAntialiasing(true);
    setPreferredScrollableViewportSize(JBUI.size(200, -1));
    initColumns();
  }

  private void initColumns() {
    final TableColumn iconColumn = getColumnModel().getColumn(ICON_COLUMN);
    iconColumn.setCellRenderer(new IconTableCellRenderer<String>() {
      @NotNull
      @Override
      protected Icon getIcon(@NotNull final String value, final JTable table, final int row) {
        return MTIcons.getFolderIcon(value);
      }
    });

  }

  @SuppressWarnings("FeatureEnvy")
  private static final class MyTableModel implements TableModel {
    private final List<? extends Association> associations;

    private MyTableModel(final List<? extends Association> associations) {
      this.associations = associations;
    }

    @Override
    public int getRowCount() {
      return associations.size();
    }

    @Override
    public int getColumnCount() {
      return 3;
    }

    @Override
    public String getColumnName(final int columnIndex) {
      switch (columnIndex) {
        case NAME_COLUMN:
          return AtomSettingsBundle.message("AssociationsForm.folderIconsTable.columns.name");
        case ICON_COLUMN:
          return AtomSettingsBundle.message("AssociationsForm.folderIconsTable.columns.icon");
        case PATTERN_COLUMN:
          return AtomSettingsBundle.message("AssociationsForm.folderIconsTable.columns.pattern");
        default:
          return "";
      }
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
      return String.class;
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
      return false;
    }

    @Override
    public @Nullable Object getValueAt(final int rowIndex, final int columnIndex) {
      final Association entry = associations.get(rowIndex);
      if (entry == null) {
        return null;
      }
      switch (columnIndex) {
        case NAME_COLUMN:
          return entry.getName();
        case ICON_COLUMN:
          return entry.getIcon();
        case PATTERN_COLUMN:
          return entry.getMatcher();
        default:
          return null;
      }
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {

    }

    @Override
    public void addTableModelListener(final TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(final TableModelListener l) {

    }
  }
}
