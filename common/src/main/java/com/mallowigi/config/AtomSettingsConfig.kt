/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2024 Elior "Mallowigi" Boukhobza
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
 */
package com.mallowigi.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import com.mallowigi.config.listeners.AtomConfigNotifier
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
class AtomSettingsConfig : BaseState(), PersistentStateComponent<AtomSettingsConfig> {
  private var firstRun: Boolean = true

  /** Whether the file icons are enabled. */
  var isEnabledIcons: Boolean by property(true)

  /** Whether the folder icons are enabled. */
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

  /** Whether Ruby Psi Icons should be used. */
  var isUseRubyIcons: Boolean by property(true)

  /** Whether Rails Icons should be used. */
  var isUseRailsIcons: Boolean by property(true)

  /** Whether Nest Icons should be used. */
  var isUseNestIcons: Boolean by property(true)

  /** Whether Angular Icons should be used. */
  var isUseAngularIcons: Boolean by property(false)

  /** Is use angular2icons. */
  var isUseAngular2Icons: Boolean by property(true)

  /** Whether Redux Icons should be used. */
  var isUseReduxIcons: Boolean by property(true)

  /** Whether NgRx Icons should be used. */
  var isUseNgRxIcons: Boolean by property(true)

  /** Is use next icons. */
  var isUseNextIcons: Boolean by property(true)

  /** Whether Recoil Icons should be used. */
  var isUseRecoilIcons: Boolean by property(true)

  /** Whether Test Icons should be used. */
  var isUseTestsIcons: Boolean by property(true)

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
  override fun getState(): AtomSettingsConfig = this

  /** Load config state from XML. */
  override fun loadState(state: AtomSettingsConfig) {
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

  /** Apply settings. */
  fun apply() {
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
    isUseRubyIcons = true
    isUseRailsIcons = true
    isUseNestIcons = true
    isUseAngularIcons = false
    isUseAngular2Icons = true
    isUseReduxIcons = true
    isUseNgRxIcons = true
    isUseNextIcons = true
    isUseRecoilIcons = true
    isUseTestsIcons = true
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

  /** Toggle enabled icons. */
  fun toggleEnabledIcons() {
    isEnabledIcons = !isEnabledIcons
  }
  //endregion

  //region Directory Icons

  /** Toggle directories icons. */
  fun toggleDirectoriesIcons() {
    isEnabledDirectories = !isEnabledDirectories
  }
  //endregion

  //region Monochrome Icons

  /** Toggle monochrome icons. */
  fun toggleMonochromeIcons() {
    isMonochromeIcons = !isMonochromeIcons
  }

  //endregion

  //region Saturated Icons

  /** Toggle saturation icons. */
  fun toggleSaturatedIcons() {
    isSaturatedIcons = !isSaturatedIcons
  }

  //endregion

  //region UI Icons

  /** Toggle ui icons. */
  fun toggleUIIcons() {
    isEnabledUIIcons = !isEnabledUIIcons
  }
  //endregion

  //region PSI Icons

  /** Toggle psi icons. */
  fun togglePsiIcons() {
    isEnabledPsiIcons = !isEnabledPsiIcons
  }
  //endregion

  //region Hollow Folders

  /** Toggle use hollow folders. */
  fun toggleUseHollowFolders() {
    isUseHollowFolders = !isUseHollowFolders
  }
  //endregion

  //region Hide File Icons

  /** Toggle hide file icons. */
  fun toggleHideFileIcons() {
    isHideFileIcons = !isHideFileIcons
  }
  //endregion

  //region Hide Folder Icons

  /** Toggle hide folder icons. */
  fun toggleHideFolderIcons() {
    isHideFolderIcons = !isHideFolderIcons
  }
  //endregion

  //region Accent Color

  /** Get current accent color. */
  fun getCurrentAccentColor(): String {
    if (isAccentColorEnabled) return accentColor ?: accentColorFromTheme
    return accentColorFromTheme
  }

  //endregion

  //region Themed Color

  /** Get current themed color. */
  fun getCurrentThemedColor(): String {
    if (isThemedColorEnabled) return themedColor ?: themedColorFromTheme
    return themedColorFromTheme
  }
  //endregion

  //region Custom Icon size

  /** Toggle custom icon size. */
  fun toggleHasCustomIconSize() {
    hasCustomIconSize = !hasCustomIconSize
  }
  //endregion

  //region Custom Line Height

  /** Toggle custom line height. */
  fun toggleHasCustomLineHeight() {
    hasCustomLineHeight = !hasCustomLineHeight
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

    /** Min Line Height. */
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
    val instance: AtomSettingsConfig by lazy { service() }

    private val accentColorFromTheme: String
      get() = getAccentFromTheme()

    private val themedColorFromTheme: String
      get() = getThemedFromTheme()

  }
}
