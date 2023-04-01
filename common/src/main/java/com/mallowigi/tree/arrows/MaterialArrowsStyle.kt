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

private const val MATERIAL_COLLAPSE: String = "/icons/mac/material/down.svg"
private const val MATERIAL_EXPAND: String = "/icons/mac/material/right.svg"
private const val MATERIAL_COLLAPSE_SELECTED: String = "/icons/mac/material/down_selected.svg"
private const val MATERIAL_EXPAND_SELECTED: String = "/icons/mac/material/right_selected.svg"
private const val MATERIAL_UNFOLD: String = "/icons/mac/material/up.svg"
private const val MATERIAL_UNFOLD_SELECTED: String = "/icons/mac/material/up_selected.svg"

private var MaterialDown: Icon = AtomIcons.load(MATERIAL_COLLAPSE)
private var MaterialRight: Icon = AtomIcons.load(MATERIAL_EXPAND)
private var MaterialDownSelected: Icon = AtomIcons.load(MATERIAL_COLLAPSE_SELECTED)
private var MaterialRightSelected: Icon = AtomIcons.load(MATERIAL_EXPAND_SELECTED)
private var MaterialUp: Icon = AtomIcons.load(MATERIAL_UNFOLD)
private var MaterialUpSelected: Icon = AtomIcons.load(MATERIAL_UNFOLD_SELECTED)

/** Material arrows style: Chevron. */
class MaterialArrowsStyle : ArrowsStyle {
  override val expandIcon: Icon
    get() = MaterialRight
  override val expandIconPath: String
    get() = MATERIAL_EXPAND

  override val collapseIcon: Icon
    get() = MaterialDown
  override val collapseIconPath: String
    get() = MATERIAL_COLLAPSE

  override val selectedExpandIcon: Icon
    get() = MaterialRightSelected
  override val selectedExpandIconPath: String
    get() = MATERIAL_EXPAND_SELECTED

  override val selectedCollapseIcon: Icon
    get() = MaterialDownSelected
  override val selectedCollapseIconPath: String
    get() = MATERIAL_COLLAPSE_SELECTED

  override val bottomCollapseIcon: Icon
    get() = MaterialUp
  override val bottomCollapseIconPath: String
    get() = MATERIAL_UNFOLD

  override val selectedBottomCollapseIcon: Icon
    get() = MaterialUpSelected
  override val selectedBottomCollapseIconPath: String
    get() = MATERIAL_UNFOLD_SELECTED

  override val pathId: @NonNls String
    get() = "material"
}
