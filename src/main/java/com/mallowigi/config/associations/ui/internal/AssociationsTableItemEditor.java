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

package com.mallowigi.config.associations.ui.internal;

import com.intellij.util.Function;
import com.intellij.util.ui.table.TableModelEditor;
import com.mallowigi.icons.associations.Association;
import com.mallowigi.icons.associations.RegexAssociation;
import org.jetbrains.annotations.NotNull;

public final class AssociationsTableItemEditor implements TableModelEditor.DialogItemEditor<Association> {

  @Override
  public @NotNull Class getItemClass() {
    return RegexAssociation.class;
  }

  @SuppressWarnings("FeatureEnvy")
  @Override
  public Association clone(@NotNull final Association item, final boolean forInPlaceEditing) {
    return new RegexAssociation(forInPlaceEditing ? item.getName() : "",
                                forInPlaceEditing ? item.getMatcher() : "",
                                forInPlaceEditing ? item.getIcon() : "");
  }

  @Override
  public void edit(@NotNull final Association item,
                   @NotNull final Function<Association, Association> mutator,
                   final boolean isAdd) {
    final Association settings = clone(item, true);
    mutator.fun(item).apply(settings);
  }

  @Override
  public void applyEdited(@NotNull final Association oldItem, @NotNull final Association newItem) {
    oldItem.apply(newItem);
  }

  @Override
  public boolean isEditable(@NotNull final Association item) {
    return !item.isEmpty();
  }

}
