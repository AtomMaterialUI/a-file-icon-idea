/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.config

import com.intellij.ide.ui.search.SearchableOptionContributor
import com.intellij.ide.ui.search.SearchableOptionProcessor
import com.intellij.openapi.options.Configurable
import com.mallowigi.config.AtomSettingsBundle.message

/**
 * Provider for Searchable options
 */
class AtomOptionContributor : SearchableOptionContributor() {
  override fun processOptions(processor: SearchableOptionProcessor) {
    val configurable: Configurable = AtomConfigurable()
    val displayName = configurable.displayName
    val strings = listOf(
      //region Strings
      message("SettingsForm.accentColorCheckbox.text"),
      message("SettingsForm.arrowsStyleLabel.text"),
      message("SettingsForm.enableDirectoryIconsCheckbox.text"),
      message("SettingsForm.enableFileIconsCheckbox.text"),
      message("SettingsForm.enablePSIIconsCheckbox.text"),
      message("SettingsForm.enableUIIconsCheckbox.text"),
      message("SettingsForm.hideFileIconsCheckbox.text"),
      message("SettingsForm.hideFolderIconsCheckbox.text"),
      message("SettingsForm.hollowFoldersCheckbox.text"),
      message("SettingsForm.monochromeCheckbox.text"),
      message("SettingsForm.themedColorCheckbox.text")
      //endregion
    )
    for (s in strings) {
      processor.addOptions(s, null, displayName, AtomConfigurable.ID, displayName, true)
    }
  }
}
