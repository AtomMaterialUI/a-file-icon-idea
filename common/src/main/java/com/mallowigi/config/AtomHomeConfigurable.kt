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

package com.mallowigi.config

import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.mallowigi.config.AtomSettingsBundle.message
import icons.AtomIcons.LOGO
import org.jetbrains.annotations.NonNls

class AtomHomeConfigurable : BoundSearchableConfigurable(
  message("settings.titles.main"),
  "com.mallowigi.config.AtomHomeConfigurable",
) {
  private lateinit var main: DialogPanel

  override fun getId(): String = ID

  @Suppress("Detekt.LongMethod")
  override fun createPanel(): DialogPanel {
    main = panel {
      row {
        icon(LOGO).align(Align.CENTER)
      }

      row {
        label("Atom Material Icons")
          .bold()
          .align(Align.CENTER)
          .applyToComponent { font = font.deriveFont(32f) }
      }
    }
    return main
  }


  companion object {
    /** Configurable ID. */
    @NonNls
    const val ID: String = "com.mallowigi.config.AtomHomeConfigurable"

    @JvmStatic
    val instance: AtomHomeConfigurable by lazy { service() }
  }

}
