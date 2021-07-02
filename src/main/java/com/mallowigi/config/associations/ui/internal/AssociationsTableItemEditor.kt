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
package com.mallowigi.config.associations.ui.internal

import com.intellij.util.Function
import com.intellij.util.ui.table.TableModelEditor.DialogItemEditor
import com.mallowigi.icons.associations.RegexAssociation

/**
 * Associations table item editor
 *
 */
class AssociationsTableItemEditor : DialogItemEditor<RegexAssociation> {
  override fun getItemClass(): Class<out RegexAssociation> = RegexAssociation::class.java

  override fun clone(item: RegexAssociation, forInPlaceEditing: Boolean): RegexAssociation {
    return RegexAssociation(
      item.name,
      item.icon,
      item.enabled,
      item.matcher
    )
  }

  override fun applyEdited(oldItem: RegexAssociation, newItem: RegexAssociation) {
    oldItem.apply(newItem)
  }

  override fun isEditable(item: RegexAssociation): Boolean = !item.isEmpty

  override fun isEmpty(item: RegexAssociation): Boolean = item.isEmpty

  override fun edit(
    item: RegexAssociation,
    mutator: Function<in RegexAssociation, out RegexAssociation>,
    isAdd: Boolean
  ) {
    val settings = clone(item, true)
    mutator.`fun`(item).apply(settings)
  }

}
