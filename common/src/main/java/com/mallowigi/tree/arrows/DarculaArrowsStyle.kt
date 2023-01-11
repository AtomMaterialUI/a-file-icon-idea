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

const val DARCULA_COLLAPSE: String = "/icons/mac/darcula/down.svg"
const val DARCULA_EXPAND: String = "/icons/mac/darcula/right.svg"
const val DARCULA_EXPAND_SELECTED: String = "/icons/mac/darcula/down_selected.svg"
const val DARCULA_COLLAPSE_SELECTED: String = "/icons/mac/darcula/right_selected.svg"
const val DARCULA_UNFOLD: String = "/icons/mac/darcula/up.svg"
const val DARCULA_UNFOLD_SELECTED: String = "/icons/mac/darcula/up_selected.svg"

var DarculaDown: Icon = AtomIcons.load(DARCULA_COLLAPSE)
var DarculaRight: Icon = AtomIcons.load(DARCULA_EXPAND)
var DarculaDownSelected: Icon = AtomIcons.load(DARCULA_EXPAND_SELECTED)
var DarculaRightSelected: Icon = AtomIcons.load(DARCULA_COLLAPSE_SELECTED)
var DarculaUp: Icon = AtomIcons.load(DARCULA_UNFOLD)
var DarculaUpSelected: Icon = AtomIcons.load(DARCULA_UNFOLD_SELECTED)

/** Darcula arrows style (triangle) */
class DarculaArrowsStyle : ArrowsStyle {
  override val expandIcon: Icon
    get() = DarculaRight
  override val expandIconPath: String
    get() = DARCULA_EXPAND

  override val collapseIcon: Icon
    get() = DarculaDown
  override val collapseIconPath: String
    get() = DARCULA_COLLAPSE

  override val selectedExpandIcon: Icon
    get() = DarculaRightSelected
  override val selectedExpandIconPath: String
    get() = DARCULA_EXPAND_SELECTED

  override val selectedCollapseIcon: Icon
    get() = DarculaDownSelected
  override val selectedCollapseIconPath: String
    get() = DARCULA_COLLAPSE_SELECTED

  override val bottomCollapseIcon: Icon
    get() = DarculaUp
  override val bottomIconPath: String
    get() = DARCULA_UNFOLD

  override val selectedBottomCollapseIcon: Icon
    get() = DarculaUp
  override val selectedBottomCollapseIconPath: String
    get() = DARCULA_UNFOLD_SELECTED

  override val pathId: @NonNls String
    get() = "darcula"
}
