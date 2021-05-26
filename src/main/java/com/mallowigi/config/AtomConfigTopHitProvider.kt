/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Chris Magnussen and Elior Boukhobza
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

import com.intellij.ide.ui.OptionsSearchTopHitProvider.ApplicationLevelProvider
import com.intellij.ide.ui.PublicMethodBasedOptionDescription
import com.intellij.ide.ui.search.BooleanOptionDescription
import com.intellij.ide.ui.search.OptionDescription
import com.intellij.openapi.util.text.StringUtil
import com.mallowigi.config.AtomSettingsBundle.message
import org.jetbrains.annotations.NonNls
import java.util.*
import java.util.function.Supplier

/**
 * Provide commands for Search Everything Top Hit commands
 */
class AtomConfigTopHitProvider : ApplicationLevelProvider {
  @NonNls
  override fun getId(): String {
    return "atomconfig"
  }

  override fun getOptions(): Collection<OptionDescription> {
    return OPTION_DESCRIPTIONS
  }

  companion object {
    @NonNls
    private val OPTION_DESCRIPTIONS: Collection<OptionDescription> = Collections.unmodifiableCollection(
      listOf(
        option(getText("SettingsForm.accentColorCheckbox.text"), "isAccentColorEnabled", "setAccentColorEnabled"),
        option(
          getText("SettingsForm.enableDirectoryIconsCheckbox.text"),
          "isEnabledDirectories",
          "setEnabledDirectories"
        ),
        option(getText("SettingsForm.enableFileIconsCheckbox.text"), "isEnabledIcons", "setEnabledIcons"),
        option(getText("SettingsForm.enablePSIIconsCheckbox.text"), "isEnabledPsiIcons", "setEnabledPsiIcons"),
        option(getText("SettingsForm.enableUIIconsCheckbox.text"), "isEnabledUIIcons", "setEnabledUIIcons"),
        option(getText("SettingsForm.hideFileIconsCheckbox.text"), "isHideFileIcons", "setHideFileIcons"),
        option(getText("SettingsForm.hideFolderIconsCheckbox.text"), "isHideFolderIcons", "setHideFolderIcons"),
        option(getText("SettingsForm.hollowFoldersCheckbox.text"), "isUseHollowFolders", "setUseHollowFolders"),
        option(getText("SettingsForm.monochromeCheckbox.text"), "isMonochromeIcons", "setMonochromeIcons"),
        option(getText("SettingsForm.themedColorCheckbox.text"), "isThemedColorEnabled", "setThemedColorEnabled")
      )
    )

    private fun getText(property: String): String {
      return StringUtil.stripHtml(message(property), false)
    }

    private fun option(@NonNls option: String, getter: String, setter: String): BooleanOptionDescription {
      return object : PublicMethodBasedOptionDescription(message("option.prefix") + option,
        AtomConfigurable.ID, getter, setter, Supplier { AtomFileIconsConfig.instance }) {
        override fun getInstance(): Any {
          return AtomFileIconsConfig.instance
        }

        override fun fireUpdated() {
          AtomFileIconsConfig.instance.fireChanged()
        }
      }
    }
  }
}
