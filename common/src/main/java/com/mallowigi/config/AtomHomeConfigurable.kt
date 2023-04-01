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
import com.intellij.ui.components.ActionLink
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.panel
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.utils.findSettingsPage
import icons.AtomIcons.LOGO
import org.jetbrains.annotations.NonNls

/** Home Configurable. */
class AtomHomeConfigurable : BoundSearchableConfigurable(
  message("settings.titles.main"),
  "com.mallowigi.config.AtomHomeConfigurable",
) {
  private lateinit var main: DialogPanel

  /** Configurable ID. */
  override fun getId(): String = ID

  /** Create the options panel. */
  @Suppress("Detekt.LongMethod")
  override fun createPanel(): DialogPanel {
    main = panel {
      row {
        icon(LOGO).align(Align.CENTER)
      }

      row {
        label(message("settings.titles.main"))
          .bold()
          .align(Align.CENTER)
          .applyToComponent { font = font.deriveFont(TITLE_FONT_SIZE) }
      }

      row {
        text(message("HomeForm.description1"))
          .align(Align.CENTER)
          .applyToComponent { font = font.deriveFont(PARAGRAPH_FONT_SIZE) }
      }.topGap(TopGap.MEDIUM)

      row {
        text(message("HomeForm.description2"))
          .align(Align.CENTER)
          .applyToComponent { font = font.deriveFont(PARAGRAPH_FONT_SIZE) }
      }

      row {
        panel {
          row {
            link(message("settings.title")) {
              findSettingsPage(it.source as ActionLink, AtomSettingsConfigurable.ID)
            }

            link(message("AtomSelectForm.title")) {
              findSettingsPage(it.source as ActionLink, AtomSettingsConfigurable.ID)
            }
          }
        }.align(Align.CENTER)
      }
    }
    return main
  }


  companion object {
    /** Configurable ID. */
    @NonNls
    const val ID: String = "AtomHomeConfigurable"

    /** Title Font size. */
    private const val TITLE_FONT_SIZE: Float = 32f

    /** Title Font size. */
    private const val PARAGRAPH_FONT_SIZE: Float = 16f

    /** INSTANCE. */
    @JvmStatic
    val instance: AtomHomeConfigurable by lazy { service() }
  }

}
