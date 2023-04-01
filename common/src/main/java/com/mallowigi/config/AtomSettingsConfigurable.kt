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
import com.intellij.openapi.ui.Messages
import com.intellij.ui.ColorPanel
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.OnOffButton
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.selected
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.config.AtomSettingsConfig.Companion.MAX_ICON_SIZE
import com.mallowigi.config.AtomSettingsConfig.Companion.MAX_LINE_HEIGHT
import com.mallowigi.config.AtomSettingsConfig.Companion.MAX_SATURATION
import com.mallowigi.config.AtomSettingsConfig.Companion.MIN_ICON_SIZE
import com.mallowigi.config.AtomSettingsConfig.Companion.MIN_LINE_HEIGHT
import com.mallowigi.config.extensions.bind
import com.mallowigi.tree.arrows.ArrowsStyles
import com.mallowigi.utils.getAccentFromTheme
import com.mallowigi.utils.getThemedFromTheme
import icons.AtomIcons
import icons.AtomIcons.Settings.ACCENT
import icons.AtomIcons.Settings.ARROWS
import icons.AtomIcons.Settings.FILES
import icons.AtomIcons.Settings.FOLDERS
import icons.AtomIcons.Settings.HIDE_FILES
import icons.AtomIcons.Settings.HIDE_FOLDERS
import icons.AtomIcons.Settings.LINE_HEIGHT
import icons.AtomIcons.Settings.MONOCHROME
import icons.AtomIcons.Settings.PSI
import icons.AtomIcons.Settings.SATURATION
import icons.AtomIcons.Settings.SIZE
import icons.AtomIcons.Settings.THEMED
import icons.AtomIcons.Settings.UI
import org.jetbrains.annotations.NonNls
import javax.swing.DefaultComboBoxModel
import javax.swing.Icon
import javax.swing.JList

/** Atom settings configurable. */
class AtomSettingsConfigurable : BoundSearchableConfigurable(
  message("settings.title"),
  "com.mallowigi.config.AtomSettingsConfigurable",
) {
  private lateinit var main: DialogPanel
  private val settings = AtomSettingsConfig.instance

  /** Settings ID. */
  override fun getId(): String = ID

  /** Create the settings panel. */
  @Suppress("Detekt.LongMethod")
  override fun createPanel(): DialogPanel {
    lateinit var fileIconsCheckbox: JBCheckBox
    lateinit var psiIconsCheckbox: JBCheckBox
    lateinit var accentColorCheckbox: JBCheckBox
    lateinit var themedColorCheckbox: JBCheckBox
    lateinit var customSizeCheckbox: JBCheckBox
    lateinit var customLineHeightCheckbox: JBCheckBox
    lateinit var saturatedCheckbox: JBCheckBox
    lateinit var monochromeCheckbox: JBCheckBox
    val arrowsRenderer = arrowsRenderer()

    main = panel {
      group(message("SettingsForm.groups.toggle")) {
        row {
          icon(FILES)
            .gap(RightGap.SMALL)
          fileIconsCheckbox = checkBox(message("SettingsForm.enableFileIconsCheckbox.text"))
            .bindSelected(settings::isEnabledIcons)
            .gap(RightGap.SMALL)
            .component
        }.rowComment(message("SettingsForm.enableFileIconsCheckbox.toolTipText"))

        row {
          icon(FOLDERS)
            .gap(RightGap.SMALL)
          checkBox(message("SettingsForm.enableDirectoryIconsCheckbox.text"))
            .bindSelected(settings::isEnabledDirectories)
            .gap(RightGap.SMALL)
            .component
        }.rowComment(message("SettingsForm.enableDirectoryIconsCheckbox.toolTipText"))

        row {
          icon(UI)
            .gap(RightGap.SMALL)
          checkBox(message("SettingsForm.enableUIIconsCheckbox.text"))
            .bindSelected(settings::isEnabledUIIcons)
            .gap(RightGap.SMALL)
            .component
        }.rowComment(message("SettingsForm.enableUIIconsCheckbox.toolTipText"))

        row {
          icon(PSI)
            .gap(RightGap.SMALL)
          psiIconsCheckbox = checkBox(message("SettingsForm.enablePSIIconsCheckbox.text"))
            .bindSelected(settings::isEnabledPsiIcons)
            .gap(RightGap.SMALL)
            .component
        }.rowComment(message("SettingsForm.enablePSIIconsCheckbox.toolTipText"))

        row {
          icon(AtomIcons.Settings.HOLLOW)
            .gap(RightGap.SMALL)
          checkBox(message("SettingsForm.hollowFoldersCheckbox.text"))
            .bindSelected(settings::isUseHollowFolders)
            .gap(RightGap.SMALL)
        }.rowComment(message("SettingsForm.hollowFoldersCheckbox.toolTipText"))

        collapsibleGroup(message("SettingsForm.iconPacks")) {
          twoColumnsRow(
            {
              icon(AtomIcons.Settings.RUBY)
                .gap(RightGap.SMALL)
              checkBox(message("SettingsForm.useRubyIcons.checkbox"))
                .bindSelected(settings::isUseRubyIcons)
                .enabledIf(psiIconsCheckbox.selected)
                .gap(RightGap.SMALL)
                .comment(message("SettingsForm.useRubyIcons.tooltip"))
            },
            {
              icon(AtomIcons.Settings.RAILS)
                .gap(RightGap.SMALL)
              checkBox(message("SettingsForm.useRailsIcons.checkbox"))
                .bindSelected(settings::isUseRailsIcons)
                .enabledIf(fileIconsCheckbox.selected)
                .gap(RightGap.SMALL)
                .comment(message("SettingsForm.useRailsIcons.tooltip"))
            }
          )

          twoColumnsRow(
            {
              icon(AtomIcons.Settings.ANGULAR)
                .gap(RightGap.SMALL)
              checkBox(message("SettingsForm.useAngularIcons.checkbox"))
                .bindSelected(settings::isUseAngularIcons)
                .enabledIf(fileIconsCheckbox.selected)
                .gap(RightGap.SMALL)
                .comment(message("SettingsForm.useAngularIcons.tooltip"))
            },
            {
              icon(AtomIcons.Settings.NEST)
                .gap(RightGap.SMALL)
              checkBox(message("SettingsForm.useNestIcons.checkbox"))
                .bindSelected(settings::isUseNestIcons)
                .enabledIf(fileIconsCheckbox.selected)
                .gap(RightGap.SMALL)
                .comment(message("SettingsForm.useNestIcons.tooltip"))
            }
          )

          twoColumnsRow(
            {
              icon(AtomIcons.Settings.REDUX)
                .gap(RightGap.SMALL)
              checkBox(message("SettingsForm.useReduxIcons.checkbox"))
                .bindSelected(settings::isUseReduxIcons)
                .enabledIf(fileIconsCheckbox.selected)
                .gap(RightGap.SMALL)
                .comment(message("SettingsForm.useReduxIcons.tooltip"))
            },
            {
              icon(AtomIcons.Settings.NGRX)
                .gap(RightGap.SMALL)
              checkBox(message("SettingsForm.useNgRxIcons.checkbox"))
                .bindSelected(settings::isUseNgRxIcons)
                .enabledIf(fileIconsCheckbox.selected)
                .gap(RightGap.SMALL)
                .comment(message("SettingsForm.useNgRxIcons.tooltip"))
            }
          )

          row {
            icon(AtomIcons.Settings.RECOIL)
              .gap(RightGap.SMALL)
            checkBox(message("SettingsForm.useRecoilIcons.checkbox"))
              .bindSelected(settings::isUseRecoilIcons)
              .enabledIf(fileIconsCheckbox.selected)
              .gap(RightGap.SMALL)
              .comment(message("SettingsForm.useRecoilIcons.tooltip"))
          }

          row {
            icon(AtomIcons.Settings.TESTS)
              .gap(RightGap.SMALL)
            checkBox(message("SettingsForm.useTestsIcons.checkbox"))
              .bindSelected(settings::isUseTestsIcons)
              .enabledIf(fileIconsCheckbox.selected)
              .gap(RightGap.SMALL)
              .comment(message("SettingsForm.useTestsIcons.tooltip"))
          }
        }.rowComment(message("SettingsForm.iconPacks.tooltip"))

        collapsibleGroup(message("SettingsForm.groups.hide")) {
          row {
            icon(HIDE_FILES)
              .gap(RightGap.SMALL)
            checkBox(message("SettingsForm.hideFileIconsCheckbox.text"))
              .bindSelected(settings::isHideFileIcons)
              .gap(RightGap.SMALL)
          }.rowComment(message("SettingsForm.hideFileIconsCheckbox.toolTipText"))

          row {
            icon(HIDE_FOLDERS)
              .gap(RightGap.SMALL)
            checkBox(message("SettingsForm.hideFolderIconsCheckbox.text"))
              .bindSelected(settings::isHideFolderIcons)
              .gap(RightGap.SMALL)
          }.rowComment(message("SettingsForm.hideFolderIconsCheckbox.toolTipText"))
        }
      }

      collapsibleGroup(message("SettingsForm.groups.icons")) {
        twoColumnsRow(
          {
            icon(ACCENT)
              .gap(RightGap.SMALL)
            accentColorCheckbox = checkBox(message("SettingsForm.accentColorCheckbox.text"))
              .bindSelected(settings::isAccentColorEnabled)
              .gap(RightGap.SMALL)
              .component
          },
          {
            cell(ColorPanel())
              .enabledIf(accentColorCheckbox.selected)
              .bind(settings::accentColor, getAccentFromTheme())
          }
        ).rowComment(message("SettingsForm.accentColorCheckbox.toolTipText"))

        twoColumnsRow(
          {
            icon(THEMED)
              .gap(RightGap.SMALL)
            themedColorCheckbox = checkBox(message("SettingsForm.themedColorCheckbox.text"))
              .bindSelected(settings::isThemedColorEnabled)
              .gap(RightGap.SMALL)
              .component
          },
          {
            cell(ColorPanel())
              .enabledIf(themedColorCheckbox.selected)
              .bind(settings::themedColor, getThemedFromTheme())
          }
        ).rowComment(message("SettingsForm.themedColorCheckbox.toolTipText"))

        collapsibleGroup(message("SettingsForm.groups.size")) {
          twoColumnsRow(
            {
              icon(SIZE)
                .gap(RightGap.SMALL)
              customSizeCheckbox = checkBox(message("SettingsForm.customIconSizeCheckbox.text"))
                .bindSelected(settings::hasCustomIconSize)
                .gap(RightGap.SMALL)
                .component
            },
            {
              spinner(MIN_ICON_SIZE..MAX_ICON_SIZE, 1)
                .bindIntValue(settings::customIconSize)
                .enabledIf(customSizeCheckbox.selected)
                .gap(RightGap.SMALL)
            }
          ).rowComment(message("SettingsForm.customIconSizeCheckbox.toolTipText"))

          twoColumnsRow(
            {
              icon(LINE_HEIGHT)
                .gap(RightGap.SMALL)
              customLineHeightCheckbox = checkBox(message("SettingsForm.customLineHeightCheckbox.text"))
                .bindSelected(settings::hasCustomLineHeight)
                .gap(RightGap.SMALL)
                .component
            },
            {
              spinner(MIN_LINE_HEIGHT..MAX_LINE_HEIGHT, 1)
                .bindIntValue(settings::customLineHeight)
                .enabledIf(customLineHeightCheckbox.selected)
                .gap(RightGap.SMALL)

            }
          ).rowComment(message("SettingsForm.customLineHeightCheckbox.toolTipText"))
        }
      }

      collapsibleGroup(message("SettingsForm.groups.other")) {
        twoColumnsRow(
          {
            icon(MONOCHROME)
              .gap(RightGap.SMALL)
            monochromeCheckbox = checkBox(message("SettingsForm.monochromeCheckbox.text"))
              .bindSelected(settings::isMonochromeIcons)
              .gap(RightGap.SMALL)
              .component
          },
          {
            cell(ColorPanel())
              .enabledIf(monochromeCheckbox.selected)
              .bind(settings::monochromeColor, AtomSettingsConfig.DEFAULT_MONOCHROME)
          }
        ).rowComment(message("SettingsForm.monochromeCheckbox.toolTipText"))

        twoColumnsRow(
          {
            icon(SATURATION)
              .gap(RightGap.SMALL)
            saturatedCheckbox = checkBox(message("SettingsForm.saturationCheckbox.text"))
              .bindSelected(settings::isSaturatedIcons)
              .gap(RightGap.SMALL)
              .component
          },
          {
            spinner(0..MAX_SATURATION, 1)
              .bindIntValue(settings::saturation)
              .enabledIf(saturatedCheckbox.selected)
              .gap(RightGap.SMALL)
          }
        ).rowComment(message("SettingsForm.saturationCheckbox.toolTipText"))

        twoColumnsRow(
          {
            icon(ARROWS)
              .gap(RightGap.SMALL)
            label(message("SettingsForm.arrowsStyleLabel.text"))
              .gap(RightGap.SMALL)
          },
          {
            val model = DefaultComboBoxModel(ArrowsStyles.values())
            comboBox(model, arrowsRenderer)
              .bindItem(settings::arrowsStyle) {
                settings.arrowsStyle = it ?: ArrowsStyles.MATERIAL
              }
          }
        ).rowComment(message("SettingsForm.arrowsStyleLabel.toolTipText"))
      }

      row {
        label(message("SettingsForm.lowPowerSwitch.text"))
          .gap(RightGap.SMALL)
        cell(OnOffButton())
          .bindSelected(settings::isLowPowerMode)
      }.rowComment(message("SettingsForm.lowPowerSwitch.toolTipText"))

      row {
        button(message("SettingsForm.resetDefaultsButton.text")) { resetSettings() }
          .resizableColumn()
          .align(AlignX.RIGHT)
      }
    }
    return main
  }

  private fun resetSettings() {
    if (Messages.showOkCancelDialog(
        message("SettingsForm.resetDefaultsButton.confirmation"),
        message("SettingsForm.resetDefaultsButton.confirmation.title"),
        message("SettingsForm.resetDefaultsButton.confirmation.ok"),
        message("SettingsForm.resetDefaultsButton.confirmation.cancel"),
        Messages.getQuestionIcon(),
      ) == Messages.OK) {
      settings.resetSettings()
      main.reset()
    }
  }

  private fun arrowsRenderer(): SimpleListCellRenderer<ArrowsStyles?> {
    val renderer = object : SimpleListCellRenderer<ArrowsStyles?>() {
      override fun customize(
        list: JList<out ArrowsStyles?>,
        value: ArrowsStyles?,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean,
      ) {
        if (value == null) return
        val baseIcon: Icon = value.icon
        icon = baseIcon
        text = value.type
      }
    }
    return renderer
  }

  /** Apply settings. */
  override fun apply() {
    super.apply()
    settings.apply()
  }

  companion object {
    /** Configurable ID. */
    @NonNls
    const val ID: String = "AtomSettingsConfigurable"

    /** Instance. */
    @JvmStatic
    val instance: AtomSettingsConfigurable by lazy { service() }
  }

}
