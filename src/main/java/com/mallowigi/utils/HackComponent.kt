/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 - 2020 Chris Magnussen and Elior Boukhobza
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
package com.mallowigi.utils

import com.intellij.openapi.ui.Divider
import javassist.CannotCompileException
import javassist.ClassClassPath
import javassist.ClassPool
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

object HackComponent {
  private fun hackIconLoader() {
    // Hack method
    try {
      val cp = ClassPool(true)
      cp.insertClassPath(ClassClassPath(Divider::class.java))
      val imageDataResolverClass = cp["com.intellij.openapi.util.IconLoader\$ImageDataResolverImpl"]
      val loadImage = imageDataResolverClass.getDeclaredMethod("loadImage")
      loadImage.instrument(object : ExprEditor() {
        @Throws(CannotCompileException::class)
        override fun edit(m: MethodCall) {
          if ("charAt" == m.methodName) {
            m.replace("{ \$_ = '/'; \$proceed(\$\$); }")
          }
        }
      })
      imageDataResolverClass.toClass()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  init {
    hackIconLoader()
  }
}