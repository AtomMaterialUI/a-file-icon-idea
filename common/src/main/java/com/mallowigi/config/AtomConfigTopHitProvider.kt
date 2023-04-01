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
@file:Suppress("Detekt.TooManyFunctions")

package com.mallowigi.config

import com.intellij.application.options.editor.CheckboxDescriptor
import com.intellij.ide.ui.OptionsSearchTopHitProvider.ApplicationLevelProvider
import com.intellij.ide.ui.search.BooleanOptionDescription
import com.intellij.ide.ui.search.OptionDescription
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.text.StringUtil
import com.mallowigi.config.AtomSettingsBundle.message
import org.jetbrains.annotations.NonNls
import java.util.*

/** Text. */
@NlsContexts.Checkbox
fun getText(property: String): String =
  StringUtil.stripHtml(message("settings.titles.prefix", message(property)), false)

/** Config instance. */
fun getInstance(): AtomSettingsConfig = AtomSettingsConfig.instance

/** region Checkbox Definitions. */

/** Accent color enabled. */
fun cdAccentColorEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.accentColorCheckbox.text"),
  AtomSettingsConfig.instance::isAccentColorEnabled,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** Directory Icons. */
fun cdDirectoryIconsEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.enableDirectoryIconsCheckbox.text"),
  AtomSettingsConfig.instance::isEnabledDirectories,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** File Icons. */
fun cdFileIconsEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.enableFileIconsCheckbox.text"),
  AtomSettingsConfig.instance::isEnabledIcons,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** PSI Icons. */
fun cdPsiIconsEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.enablePSIIconsCheckbox.text"),
  AtomSettingsConfig.instance::isEnabledPsiIcons,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** UI Icons. */
fun cdUIIconsEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.enableUIIconsCheckbox.text"),
  AtomSettingsConfig.instance::isEnabledUIIcons,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** Hide File Icons. */
fun cdHideFileIconsEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.hideFileIconsCheckbox.text"),
  AtomSettingsConfig.instance::isHideFileIcons,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** Hide Folder Icons. */
fun cdHideFolderIconsEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.hideFolderIconsCheckbox.text"),
  AtomSettingsConfig.instance::isHideFolderIcons,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** Hollow Folders. */
fun cdHollowFoldersEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.hollowFoldersCheckbox.text"),
  AtomSettingsConfig.instance::isUseHollowFolders,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** Monochrome Icons. */
fun cdMonochromeIconsEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.monochromeCheckbox.text"),
  AtomSettingsConfig.instance::isMonochromeIcons,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** Themed Color. */
fun cdThemedColorEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.themedColorCheckbox.text"),
  AtomSettingsConfig.instance::isThemedColorEnabled,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** Custom Icon Size. */
fun cdCustomIconSizeEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.customIconSizeCheckbox.text"),
  AtomSettingsConfig.instance::hasCustomIconSize,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** Custom Line Height. */
fun cdCustomLineHeightEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.customLineHeightCheckbox.text"),
  AtomSettingsConfig.instance::hasCustomLineHeight,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }

/** Low Power Mode. */
fun cdLowPowerModeEnabled(): BooleanOptionDescription = CheckboxDescriptor(
  getText("SettingsForm.lowPowerSwitch.text"),
  AtomSettingsConfig.instance::isLowPowerMode,
).asOptionDescriptor { AtomSettingsConfig.instance.fireChanged() }
// endregion

/** Provide commands for Search Everything Top Hit commands. */
@Suppress("DialogTitleCapitalization")
class AtomConfigTopHitProvider : ApplicationLevelProvider {

  /** ID. */
  @NonNls
  override fun getId(): String = "atomconfig"

  /** Option list. */
  override fun getOptions(): Collection<OptionDescription> = OPTION_DESCRIPTIONS

  companion object {

    @NonNls
    private val OPTION_DESCRIPTIONS: Collection<OptionDescription> = Collections.unmodifiableCollection(
      listOf(
        cdAccentColorEnabled(),
        cdDirectoryIconsEnabled(),
        cdFileIconsEnabled(),
        cdPsiIconsEnabled(),
        cdUIIconsEnabled(),
        cdHideFileIconsEnabled(),
        cdHideFolderIconsEnabled(),
        cdHollowFoldersEnabled(),
        cdMonochromeIconsEnabled(),
        cdThemedColorEnabled(),
        cdCustomLineHeightEnabled(),
        cdCustomIconSizeEnabled(),
        cdLowPowerModeEnabled()
      )
    )

  }

}
