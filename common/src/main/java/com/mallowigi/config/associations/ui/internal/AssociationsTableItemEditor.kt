/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.config.associations.ui.internal

import com.intellij.util.Function
import com.intellij.util.ui.CollectionItemEditor
import com.intellij.util.ui.table.TableModelEditor.DialogItemEditor
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.PsiAssociation
import com.mallowigi.icons.associations.RegexAssociation

/** Editor for the [Association] table cells. */
class AssociationsTableItemEditor : DialogItemEditor<Association>, CollectionItemEditor<Association> {

  /**
   * Apply changes to the edited item.
   *
   * @param oldItem the item to modify
   * @param newItem the changes
   */
  override fun applyEdited(oldItem: Association, newItem: Association): Unit = oldItem.apply(newItem)

  /**
   * Duplicate an item.
   *
   * @param item the [Association]
   * @param forInPlaceEditing if editing in place (true)
   * @return a clone of the association
   */
  override fun clone(item: Association, forInPlaceEditing: Boolean): Association {
    val association = when (item) {
      is PsiAssociation -> {
        val psi = PsiAssociation()
        psi.path = item.path
        psi
      }

      else              -> {
        val reg = RegexAssociation()
        reg.pattern = (item as RegexAssociation).pattern
        reg
      }
    }

    with(association) {
      iconType = item.iconType
      name = item.name
      icon = item.icon
      enabled = item.enabled
      priority = item.priority
      matcher = item.matcher
      touched = item.touched
      iconColor = item.iconColor
      folderColor = item.folderColor
      folderIconColor = item.folderIconColor
    }

    return association
  }

  /**
   * Edits an item.
   *
   * @param item the [Association]
   * @param mutator a function to mutate the item
   * @param isAdd if in add mode
   */
  override fun edit(
    item: Association,
    mutator: Function<in Association, out Association>,
    isAdd: Boolean,
  ) {
    val settings = clone(item, true)
    mutator.`fun`(item).apply(settings)
  }

  /** Class of the items. */
  override fun getItemClass(): Class<out Association> = Association::class.java

  /**
   * Do not allow editing empty items.
   *
   * @param item the [Association]
   * @return true if editable
   */
  override fun isEditable(item: Association): Boolean = !item.isEmpty

  /**
   * Determines what constitues an empty [Association].
   *
   * @param item the [Association]
   * @return true if empty
   */
  override fun isEmpty(item: Association): Boolean = item.isEmpty

  /** Whether item can be removed. */
  override fun isRemovable(item: Association): Boolean = item.touched

}
