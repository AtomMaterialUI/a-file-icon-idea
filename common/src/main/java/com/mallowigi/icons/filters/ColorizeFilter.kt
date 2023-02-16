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
package com.mallowigi.icons.filters

import java.awt.Color
import java.awt.image.RGBImageFilter

/** Applies a filter to the IDE to colorize the icons with a color. */
abstract class ColorizeFilter : RGBImageFilter() {
  abstract val color: Color

  @Suppress("Detekt:MagicNumber", "HardCodedStringLiteral")
  override fun filterRGB(x: Int, y: Int, rgb: Int): Int {
    val myBase = Color.RGBtoHSB(color.red, color.green, color.blue, null)
    // Get color components
    val r = rgb shr 16 and 0xFF
    val g = rgb shr 8 and 0xFF
    val b = rgb and 0xFF
    val hsb = FloatArray(3)
    Color.RGBtoHSB(r, g, b, hsb)
    val endColor = Color.HSBtoRGB(myBase[0], myBase[1] * hsb[1], myBase[2] * hsb[2])
    return rgb and -0x1000000 or (endColor and 0xFFFFFF)
  }
}
