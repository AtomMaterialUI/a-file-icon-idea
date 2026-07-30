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
package com.mallowigi.utils

import javassist.ClassPool
import javassist.expr.ExprEditor
import javassist.expr.NewExpr

class HackComponent {
  init {
    hackBigIcons()
  }

  @Suppress("CallToSuspiciousStringMethod")
  private fun hackBigIcons() {
    try {
      val cp = ClassPool(true)
      cp.importPackage("javax.swing")

      val uiClass = cp["com.intellij.ui.svg.JSvgDocumentFactoryKt"]
      uiClass.getDeclaredMethod("buildDocument").apply {
        instrument(object : ExprEditor() {
          override fun edit(e: NewExpr) {
            if ("com.intellij.ui.svg.ParsedSvgDocument" != e.className) return
            // language=JShellLanguage
            e.replace(
              $$"""{
                String atomW = $3;
                String atomH = $4;
                Object atomSize = UIManager.get("AtomIcons.customIconSize");
                if (atomSize != null) {
                    if ("16".equals(atomW) || "16px".equals(atomW)) atomW = atomSize.toString();
                    if ("16".equals(atomH) || "16px".equals(atomH)) atomH = atomSize.toString();
                }
                $_ = $proceed($1, $2, atomW, atomH, $5);
            }""".trimMargin()
            )
          }
        })
      }
      uiClass.toClass()
    } catch (e: Throwable) {
      e.printStackTrace()
    }
  }
}
