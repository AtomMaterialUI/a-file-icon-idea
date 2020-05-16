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
import com.intellij.openapi.ui.cellvalidators.ValidatingTableCellRendererWrapper;
import com.intellij.util.ui.table.TableModelEditor;
import com.mallowigi.config.AtomSettingsBundle;
import com.mallowigi.config.associations.ui.internal.RegexpEditor;
import com.mallowigi.icons.associations.Association;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
public final class PatternEditableColumnInfo extends TableModelEditor.EditableColumnInfo<Association, String> {
  private final Disposable parent;

  public PatternEditableColumnInfo(final Disposable disposable) {
    super(AtomSettingsBundle.message("AssociationsForm.folderIconsTable.columns.pattern"));
    parent = disposable;
  }

  private static ValidationInfo validate(final Object value, final int row, final int column) {
    if (value == null || value.equals("")) {
      return new ValidationInfo(AtomSettingsBundle.message("you.must.enter.a.name"));
    }
    else if (!isValidPattern(value.toString())) {
      return new ValidationInfo(AtomSettingsBundle.message("please.enter.a.valid.pattern"));
    }
    else {
      return null;
    }
  }

  @Override
  public String valueOf(final Association item) {
    return item.getMatcher();
  }

  @Override
  public void setValue(final Association item, final String value) {
    item.setMatcher(value);
  }

  @Override
  public @NotNull TableCellEditor getEditor(final Association item) {
    return new RegexpEditor();
  }

  private static boolean isValidPattern(final String pattern) {
    try {
      Pattern.compile(pattern);
      return true;
    }
    catch (final RuntimeException e) {
      return false;
    }
  }

  @Override
  public @Nullable TableCellRenderer getRenderer(final Association item) {
    return new ValidatingTableCellRendererWrapper(new DefaultTableCellRenderer())
      .withCellValidator(PatternEditableColumnInfo::validate);
  }

}
