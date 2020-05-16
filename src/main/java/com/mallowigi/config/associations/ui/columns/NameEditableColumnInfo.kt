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
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.ui.cellvalidators.StatefulValidatingCellEditor;
import com.intellij.openapi.ui.cellvalidators.ValidatingTableCellRendererWrapper;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.intellij.util.ui.table.TableModelEditor;
import com.mallowigi.config.AtomSettingsBundle;
import com.mallowigi.icons.associations.Association;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("UnstableApiUsage")
public final class NameEditableColumnInfo extends TableModelEditor.EditableColumnInfo<Association, String> {
  private final Disposable parent;

  public NameEditableColumnInfo(final Disposable parent) {
    super(AtomSettingsBundle.message("AssociationsForm.folderIconsTable.columns.name"));
    this.parent = parent;
  }

  @Override
  public String valueOf(final Association item) {
    return item.getName();
  }

  @Override
  public void setValue(final Association item, final String value) {
    item.setName(value);
  }

  @Override
  public @NotNull TableCellEditor getEditor(final Association item) {
    final ExtendableTextField cellEditor = new ExtendableTextField();

    return new StatefulValidatingCellEditor(cellEditor, parent);
  }

  @Override
  public @Nullable TableCellRenderer getRenderer(final Association item) {
    return new ValidatingTableCellRendererWrapper(new DefaultTableCellRenderer())
      .withCellValidator((value, row, column) -> {
        if (value == null || value.equals("")) {
          return new ValidationInfo(AtomSettingsBundle.message("you.must.enter.a.name"));
        }
        else {
          return null;
        }
      });
  }

}
