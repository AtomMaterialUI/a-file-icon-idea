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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import com.mallowigi.config.listeners.AtomConfigNotifier
import com.mallowigi.config.ui.SettingsForm
import com.mallowigi.tree.arrows.ArrowsStyles
import com.mallowigi.utils.getAccentFromTheme
import com.mallowigi.utils.getThemedFromTheme
import com.mallowigi.utils.getValue

/** Atom file icons config. */
@Suppress("TooManyFunctions")
@State(
  name = "AtomFileIconsConfig",
  storages = [Storage("a-file-icons.xml")],
  category = SettingsCategory.UI
)
class AtomFileIconsConfig : BaseState(), PersistentStateComponent<AtomFileIconsConfig> {
  private var firstRun: Boolean = true

  /** Whether the file icons are enabled. */
  var isEnabledIcons: Boolean by property(true)

  /** Whether the folder icons are enabledd. */
  var isEnabledDirectories: Boolean by property(true)

  /** Whether the UI icons are enabled. */
  var isEnabledUIIcons: Boolean by property(true)

  /** Whether the monochrome icons are enabled. */
  var isMonochromeIcons: Boolean by property(false)

  /** Whether the saturated icons are enabled. */
  var isSaturatedIcons: Boolean by property(false)

  /** Whether the opacity icons are enabled. */
  var isOpacityIcons: Boolean by property(false)

  /** The monochrome color. */
  var monochromeColor: String? by string(DEFAULT_MONOCHROME)

  /** The saturation. */
  var saturation: Int by property(DEFAULT_SATURATION)

  /** The opacity. */
  var opacity: Int by property(DEFAULT_OPACITY)

  /** Whether the PSI icons are enabled. */
  var isEnabledPsiIcons: Boolean by property(true)

  /** Whether file icons are hidden. */
  var isHideFileIcons: Boolean by property(false)

  /** Whether folder icons are hidden. */
  var isHideFolderIcons: Boolean by property(false)

  /** Whether the hollow folders are enabled. */
  var isUseHollowFolders: Boolean by property(true)

  /** Style of tree expand arrows. */
  var arrowsStyle: ArrowsStyles by enum(ArrowsStyles.MATERIAL)

  /** Whether custom accent color is enabled. */
  var isAccentColorEnabled: Boolean by property(false)

  /** Custom accent color. */
  var accentColor: String? by string(accentColorFromTheme)

  /** Whether custom theme color is enabled. */
  var isThemedColorEnabled: Boolean by property(false)

  /** Custom theme color. */
  var themedColor: String? by string(themedColorFromTheme)

  /** Whether custom icon size is enabled. */
  var hasCustomIconSize: Boolean by property(false)

  /** Whether custom line height is enabled. */
  var hasCustomLineHeight: Boolean by property(false)

  /** Custom icon size. */
  var customIconSize: Int by property(DEFAULT_ICON_SIZE)

  /** Custom line height. */
  var customLineHeight: Int by property(DEFAULT_LINE_HEIGHT)

  /** Whether low power mode is enabled. */
  var isLowPowerMode: Boolean by property(true)

  /** Config state. */
  override fun getState(): AtomFileIconsConfig = this

  /** Load config state from XML. */
  override fun loadState(state: AtomFileIconsConfig): Unit {
    val changed = state != this
    XmlSerializerUtil.copyBean(state, this)

    if (changed && !firstRun) {
      ApplicationManager.getApplication().invokeAndWait { fireChanged() }
    }
    firstRun = false
  }

  /** Fire event when settings are changed. */
  fun fireChanged() {
    ApplicationManager.getApplication().messageBus
      .syncPublisher(AtomConfigNotifier.TOPIC)
      .configChanged(this)
  }

  fun apply() {
    fireChanged()
  }

  /**
   * Apply settings
   *
   * @param form
   */
  fun applySettings(form: SettingsForm) {
    isEnabledIcons = form.isEnabledIcons
    isEnabledDirectories = form.isEnabledDirectories
    isEnabledUIIcons = form.isEnabledUIIcons
    isMonochromeIcons = form.isEnabledMonochromeIcons
    monochromeColor = form.monochromeColor
    isEnabledPsiIcons = form.isEnabledPsiIcons
    isHideFileIcons = form.isHiddenFileIcons
    isHideFolderIcons = form.isHiddenFolderIcons
    isUseHollowFolders = form.isHollowFoldersEnabled
    arrowsStyle = form.arrowsStyle
    isAccentColorEnabled = form.isAccentColorEnabled
    accentColor = form.accentColor
    isThemedColorEnabled = form.isThemedColorEnabled
    themedColor = form.themedColor
    hasCustomIconSize = form.hasCustomIconSize
    hasCustomLineHeight = form.hasCustomLineHeight
    customIconSize = form.customIconSize
    customLineHeight = form.customLineHeight
    isLowPowerMode = form.isLowPowerMode
    fireChanged()
  }

  /** Reset settings. */
  fun resetSettings() {
    isEnabledIcons = true
    isEnabledDirectories = true
    isEnabledUIIcons = true
    isMonochromeIcons = false
    monochromeColor = DEFAULT_MONOCHROME // NON-NLS
    isEnabledPsiIcons = true
    isHideFileIcons = false
    isHideFolderIcons = false
    isUseHollowFolders = true
    arrowsStyle = ArrowsStyles.MATERIAL
    isAccentColorEnabled = false
    accentColor = accentColorFromTheme
    isThemedColorEnabled = false
    themedColor = themedColorFromTheme
    hasCustomIconSize = false
    customIconSize = DEFAULT_ICON_SIZE
    hasCustomLineHeight = false
    customLineHeight = DEFAULT_LINE_HEIGHT
    saturation = DEFAULT_SATURATION
    isSaturatedIcons = false
    opacity = DEFAULT_OPACITY
    isOpacityIcons = false
    isLowPowerMode = true
    fireChanged()
  }


  //region File Icons
  /** Is enabled icons changed. */
  fun isEnabledIconsChanged(isEnabledIcons: Boolean): Boolean = this.isEnabledIcons != isEnabledIcons

  /** Toggle enabled icons. */
  fun toggleEnabledIcons() {
    isEnabledIcons = !isEnabledIcons
  }
  //endregion

  //region Directory Icons
  /** Is enabled directories changed. */
  fun isEnabledDirectoriesChanged(isEnabledDirectories: Boolean): Boolean =
    this.isEnabledDirectories != isEnabledDirectories

  /** Toggle directories icons. */
  fun toggleDirectoriesIcons() {
    isEnabledDirectories = !isEnabledDirectories
  }
  //endregion

  //region Monochrome Icons
  /** Is monochrome icons changed. */
  fun isMonochromeIconsChanged(isMonochrome: Boolean): Boolean = isMonochromeIcons != isMonochrome

  /** Toggle monochrome icons. */
  fun toggleMonochromeIcons() {
    isMonochromeIcons = !isMonochromeIcons
  }

  /** Is monochrome color changed. */
  fun isMonochromeColorChanged(monochromeColor: String): Boolean =
    this.monochromeColor?.lowercase() != monochromeColor.lowercase()
  //endregion

  //region Saturated Icons
  /** Is saturation icons changed. */
  fun isSaturatedIconsChanged(isSaturated: Boolean): Boolean = isSaturatedIcons != isSaturated

  /** Toggle saturation icons. */
  fun toggleSaturatedIcons() {
    isSaturatedIcons = !isSaturatedIcons
  }

  /** Is saturation changed. */
  fun isSaturatedColorChanged(saturation: Int): Boolean =
    this.saturation != saturation
  //endregion

  //region Opacity Icons
  /** Is opacity icons changed. */
  fun isOpacityIconsChanged(isOpacity: Boolean): Boolean = isOpacityIcons != isOpacity

  /** Toggle opacity icons. */
  fun toggleOpacityIcons() {
    isOpacityIcons = !isOpacityIcons
  }

  /** Is opacity changed. */
  fun isOpacityColorChanged(opacity: Int): Boolean =
    this.opacity != opacity
  //endregion

  //region UI Icons
  /** Is enabled ui icons changed. */
  fun isEnabledUIIconsChanged(isEnabledUIIcons: Boolean): Boolean = this.isEnabledUIIcons != isEnabledUIIcons

  /** Toggle ui icons. */
  fun toggleUIIcons() {
    isEnabledUIIcons = !isEnabledUIIcons
  }
  //endregion

  //region PSI Icons
  /** Is enabled psi icons changed. */
  fun isEnabledPsiIconsChanged(isEnabledPsiIcons: Boolean): Boolean = this.isEnabledPsiIcons != isEnabledPsiIcons

  /** Toggle psi icons. */
  fun togglePsiIcons() {
    isEnabledPsiIcons = !isEnabledPsiIcons
  }
  //endregion

  //region Hollow Folders
  /** Is use hollow folders changed. */
  fun isUseHollowFoldersChanged(useHollowFolders: Boolean): Boolean = isUseHollowFolders != useHollowFolders

  /** Toggle use hollow folders. */
  fun toggleUseHollowFolders() {
    isUseHollowFolders = !isUseHollowFolders
  }
  //endregion

  //region Hide File Icons
  /** Is hide file icons changed. */
  fun isHideFileIconsChanged(hideFileIcons: Boolean): Boolean = isHideFileIcons != hideFileIcons

  /** Toggle hide file icons. */
  fun toggleHideFileIcons() {
    isHideFileIcons = !isHideFileIcons
  }
  //endregion

  //region Hide Folder Icons
  /** Is hide folder icons changed. */
  fun isHideFolderIconsChanged(hideFolderIcons: Boolean): Boolean = isHideFolderIcons != hideFolderIcons

  /** Toggle hide folder icons. */
  fun toggleHideFolderIcons() {
    isHideFolderIcons = !isHideFolderIcons
  }
  //endregion

  //region Arrows Style
  /** Is arrows style changed? */
  fun isArrowsStyleChanged(arrowsStyle: ArrowsStyles): Boolean = this.arrowsStyle != arrowsStyle
  //endregion

  //region Accent Color
  /** Is accent color changed. */
  fun isAccentColorChanged(accentColor: String): Boolean = this.accentColor?.lowercase() != accentColor.lowercase()

  /** Is accent color enabled changed. */
  fun isAccentColorEnabledChanged(enabled: Boolean): Boolean = this.isAccentColorEnabled != enabled

  /** Get current accent color. */
  fun getCurrentAccentColor(): String {
    if (isAccentColorEnabled) return accentColor ?: accentColorFromTheme
    return accentColorFromTheme
  }

  //endregion

  //region Themed Color
  /** Is themed color changed. */
  fun isThemedColorChanged(themedColor: String): Boolean = this.themedColor?.lowercase() != themedColor.lowercase()

  /** Is themed color enabled changed. */
  fun isThemedColorEnabledChanged(enabled: Boolean): Boolean = this.isThemedColorEnabled != enabled

  /** Get current themed color. */
  fun getCurrentThemedColor(): String {
    if (isThemedColorEnabled) return themedColor ?: themedColorFromTheme
    return themedColorFromTheme
  }
  //endregion

  //region Custom Icon size
  /** Is custom icon size changed? */
  fun isHasCustomIconSizeChanged(isCustomIconSize: Boolean): Boolean = hasCustomIconSize != isCustomIconSize

  /** Toggle big icons. */
  fun toggleHasCustomIconSize() {
    hasCustomIconSize = !hasCustomIconSize
  }
  //endregion

  //region Custom Icon size
  /** Is custom icon size changed? */
  fun isCustomIconSizeChanged(newSize: Int): Boolean = newSize != customIconSize

  //endregion

  //region Custom Line Height

  /** Is custom line height changed? */
  fun isHasCustomLineHeightChanged(newHeight: Boolean): Boolean = hasCustomLineHeight != newHeight

  /** Is custom line height changed? */
  fun hasCustomLineHeightChanged(newSize: Int): Boolean = newSize != customLineHeight

  //endregion

  //region Low Power mode
  /** Is low power mode changed. */
  fun isLowPowerModeChanged(lowPower: Boolean): Boolean = isLowPowerMode != lowPower

  /** Toggle low power mode. */
  fun toggleLowPowerMode() {
    isLowPowerMode = !isLowPowerMode
  }
  //endregion


  companion object {
    /** Default Icon Size. */
    const val DEFAULT_ICON_SIZE: Int = 16

    /** min icon size */
    const val MIN_ICON_SIZE: Int = 12

    /** Max icon size. */
    const val MAX_ICON_SIZE: Int = 24

    /** Default Line Height. */
    const val DEFAULT_LINE_HEIGHT: Int = 20

    /** Min Line Hieght. */
    const val MIN_LINE_HEIGHT: Int = 16

    /** Max Line Height. */
    const val MAX_LINE_HEIGHT: Int = 60

    /** Default Monochrome. */
    const val DEFAULT_MONOCHROME: String = "546E7A" // NON-NLS

    /** Default Saturation. */
    const val DEFAULT_SATURATION: Int = 0

    /** Max Saturation. */
    const val MAX_SATURATION: Int = 20

    /** Default Opacity. */
    const val DEFAULT_OPACITY: Int = 100

    /** Max Opacity. */
    const val MAX_OPACITY: Int = 100

    /** Instance of the Config service. */
    @JvmStatic
      /** Instance of the theme manager. */
    val instance: AtomFileIconsConfig by lazy { service() }

    private val accentColorFromTheme: String
      get() = getAccentFromTheme()

    private val themedColorFromTheme: String
      get() = getThemedFromTheme()


  }
}
