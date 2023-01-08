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
import java.lang.reflect.Method

/** Super hacking class to change static fields! */
@Suppress("unused", "HardCodedStringLiteral")
object StaticPatcher {
  @Throws(NoSuchFieldException::class, IllegalAccessException::class)
  @JvmStatic
  fun setFinalStatic(cls: Class<*>, fieldName: String, newValue: Any) {
    val fields = cls.declaredFields
    for (field in fields) {
      if (field.name == fieldName) {
        setFinalStatic(field, newValue)
        return
      }
    }
  }

  /** Set final. */
  @JvmStatic
  @Throws(NoSuchFieldException::class, IllegalAccessException::class)
  fun setFinalStatic(field: Field, newValue: Any) {
    field.isAccessible = true
    FieldHelper.makeNonFinal(field)

    field[null] = newValue

    FieldHelper.makeFinal(field)
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
  fun setFinal(instance: Any, field: Field, newValue: Any) {
    field.isAccessible = true
    FieldHelper.makeNonFinal(field)

    field[instance] = newValue

    FieldHelper.makeFinal(field)
    field.isAccessible = false
  }

  /**
   * Invoke method
   *
   * @param method the method to invoke
   * @param instance the instance to invoke upon
   */
  @JvmStatic
  fun invokeMethod(method: Method, instance: Any) {
    method.isAccessible = true
    method.invoke(instance)
    method.isAccessible = false
  }

  /**
   * Invoke method
   *
   * @param cls the class to extract the method from
   * @param methodName the method to invoke
   * @param instance the instance to invoke upon
   */
  @JvmStatic
  fun invokeMethod(cls: Class<*>, methodName: String, instance: Any) {
    val method = cls.getDeclaredMethod(methodName)
    method.isAccessible = true
    method.invoke(instance)
    method.isAccessible = false
  }

}
