/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.tree.arrows

import icons.AtomIcons
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

const val PLUS_MINUS_EXPAND: String = "/icons/mac/plusminus/plus.svg"
const val PLUS_MINUS_COLLAPSE: String = "/icons/mac/plusminus/minus.svg"
const val PLUS_MINUS_EXPAND_SELECTED: String = "/icons/mac/plusminus/plus_selected.svg"
const val PLUS_MINUS_COLLAPSE_SELECTED: String = "/icons/mac/plusminus/minus_selected.svg"

var Plus: Icon = AtomIcons.load(PLUS_MINUS_EXPAND)
var Minus: Icon = AtomIcons.load(PLUS_MINUS_COLLAPSE)
var PlusSelected: Icon = AtomIcons.load(PLUS_MINUS_EXPAND_SELECTED)
var MinusSelected: Icon = AtomIcons.load(PLUS_MINUS_COLLAPSE_SELECTED)

/** Plus-minus arrows style. */
class PlusMinusArrowsStyle : ArrowsStyle {
  override val expandIcon: Icon
    get() = Plus
  override val expandIconPath: String
    get() = PLUS_MINUS_EXPAND

  override val collapseIcon: Icon
    get() = Minus
  override val collapseIconPath: String
    get() = PLUS_MINUS_COLLAPSE

  override val selectedExpandIcon: Icon
    get() = PlusSelected
  override val selectedExpandIconPath: String
    get() = PLUS_MINUS_EXPAND_SELECTED

  override val selectedCollapseIcon: Icon
    get() = MinusSelected
  override val selectedCollapseIconPath: String
    get() = PLUS_MINUS_COLLAPSE_SELECTED

  override val bottomCollapseIcon: Icon
    get() = Minus
  override val bottomIconPath: String
    get() = PLUS_MINUS_COLLAPSE

  override val selectedBottomCollapseIcon: Icon
    get() = MinusSelected
  override val selectedBottomCollapseIconPath: String
    get() = PLUS_MINUS_COLLAPSE_SELECTED

  override val pathId: @NonNls String
    get() = "plusminus"
}
