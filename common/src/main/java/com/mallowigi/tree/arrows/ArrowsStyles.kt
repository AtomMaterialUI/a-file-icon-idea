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

import org.jetbrains.annotations.NonNls
import javax.swing.Icon

/**
 * Enum for arrow styles
 *
 * @property type description
 * @property arrowsStyle enum value
 */
enum class ArrowsStyles(@param:NonNls public val type: String, private val arrowsStyle: ArrowsStyle) {
  /** Material Style (chevrons) */
  MATERIAL("Material", MaterialArrowsStyle()),

  /** Darcula Style (triangle) */
  DARCULA("Darcula", DarculaArrowsStyle()),

  /** Plus-Minus Style. */
  PLUSMINUS("Plus-Minus", PlusMinusArrowsStyle()),

  /** Arrows Style. */
  ARROWS("Arrows", ArrowsArrowsStyle()),

  /** No Arrows. */
  NONE("None", NoneArrowsStyle());

  /** Current style's expanded icon. */
  val expandIcon: Icon
    get() = arrowsStyle.expandIcon

  /** Current style's collapsed icon. */
  val collapseIcon: Icon
    get() = arrowsStyle.collapseIcon

  /** Current style's selected expanded icon. */
  val selectedExpandIcon: Icon
    get() = arrowsStyle.selectedExpandIcon

  /** Current style's selected collapsed icon. */
  val selectedCollapseIcon: Icon
    get() = arrowsStyle.selectedCollapseIcon

  /** Bottom collapse icon. */
  val bottomCollapseIcon: Icon
    get() = arrowsStyle.bottomCollapseIcon

  /** Selected bottom collapse icon. */
  val selectedBottomCollapseIcon: Icon
    get() = arrowsStyle.selectedBottomCollapseIcon

  /** Icon to show in settings. */
  val icon: Icon
    get() = expandIcon

  override fun toString(): String = type

}
