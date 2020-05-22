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
package com.mallowigi.tree.arrows

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

enum class ArrowsStyles(@param:NonNls private val type: String, private val arrowsStyle: ArrowsStyle) {
  MATERIAL("Material", MaterialArrowsStyle()),
  DARCULA("Darcula", DarculaArrowsStyle()),
  PLUSMINUS("Plus-Minus", PlusMinusArrowsStyle()),
  ARROWS("Arrows", ArrowsArrowsStyle()),
  NONE("None", NoneArrowsStyle());

  override fun toString(): String = type

  val expandIcon: Icon?
    get() = arrowsStyle.expandIcon

  val collapseIcon: Icon?
    get() = arrowsStyle.collapseIcon

  val selectedExpandIcon: Icon?
    get() = arrowsStyle.selectedExpandIcon

  val selectedCollapseIcon: Icon?
    get() = arrowsStyle.selectedCollapseIcon

  val icon: Icon
    get() = expandIcon ?: IconLoader.getTransparentIcon(AllIcons.Ide.Link, 0.0f)

}