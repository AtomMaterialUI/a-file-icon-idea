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
package com.mallowigi.utils

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/** Super hacking class to change static fields! */
@Suppress("unused", "HardCodedStringLiteral")
object StaticPatcher {

  /** Set final. */
  @JvmStatic
  @Throws(NoSuchFieldException::class, IllegalAccessException::class)
  fun setFinal(instance: Any, field: Field, newValue: Any) {
    field.isAccessible = true
    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

    field[instance] = newValue
    modifiersField.setInt(field, field.modifiers or Modifier.FINAL)
    modifiersField.isAccessible = false
    field.isAccessible = false
  }

  /**
   * Rewrites a class's static field with a new value by field
   *
   * @param field the Field to change
   * @param newValue the new value
   */
  @JvmStatic
  @Throws(NoSuchFieldException::class, IllegalAccessException::class)
  fun setFinalStatic(field: Field, newValue: Any?) {
    field.isAccessible = true
    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

    field[null] = newValue
    modifiersField.setInt(field, field.modifiers or Modifier.FINAL)
    modifiersField.isAccessible = false

    field.isAccessible = false
  }

}
