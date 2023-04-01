/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Elior "Mallowigi" Boukhobza
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
 */
package com.mallowigi.tree.arrows

import icons.AtomIcons
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

private const val ARROW_COLLAPSE: String = "/icons/mac/arrow/down.svg"
private const val ARROW_EXPAND: String = "/icons/mac/arrow/right.svg"
private const val ARROW_COLLAPSE_SELECTED: String = "/icons/mac/arrow/down_selected.svg"
private const val ARROW_EXPAND_SELECTED: String = "/icons/mac/arrow/right_selected.svg"
private const val ARROW_UNFOLD: String = "/icons/mac/arrow/up.svg"
private const val ARROW_UNFOLD_SELECTED: String = "/icons/mac/arrow/up_selected.svg"

private var Down: Icon = AtomIcons.load(ARROW_COLLAPSE)
private var Right: Icon = AtomIcons.load(ARROW_EXPAND)
private var DownSelected: Icon = AtomIcons.load(ARROW_COLLAPSE_SELECTED)
private var RightSelected: Icon = AtomIcons.load(ARROW_EXPAND_SELECTED)
private var Up: Icon = AtomIcons.load(ARROW_UNFOLD)
private var UpSelected: Icon = AtomIcons.load(ARROW_UNFOLD_SELECTED)

/** Arrows Arrow style. */
class ArrowsArrowsStyle : ArrowsStyle {
  override val expandIcon: Icon
    get() = Right
  override val expandIconPath: String
    get() = ARROW_EXPAND

  override val collapseIcon: Icon
    get() = Down
  override val collapseIconPath: String
    get() = ARROW_COLLAPSE

  override val selectedExpandIcon: Icon
    get() = RightSelected
  override val selectedExpandIconPath: String
    get() = ARROW_EXPAND_SELECTED

  override val selectedCollapseIcon: Icon
    get() = DownSelected
  override val selectedCollapseIconPath: String
    get() = ARROW_COLLAPSE_SELECTED

  override val bottomCollapseIcon: Icon
    get() = Up
  override val bottomCollapseIconPath: String
    get() = ARROW_UNFOLD

  override val selectedBottomCollapseIcon: Icon
    get() = UpSelected
  override val selectedBottomCollapseIconPath: String
    get() = ARROW_UNFOLD_SELECTED

  override val pathId: @NonNls String
    get() = "arrow"
}
