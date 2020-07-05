/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2020 Elior "Mallowigi" Boukhobza
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package com.mallowigi.config

import com.intellij.ide.ui.LafManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.ui.ColorUtil
import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Property
import com.mallowigi.config.listeners.AtomConfigNotifier
import com.mallowigi.config.ui.SettingsForm
import com.mallowigi.tree.arrows.ArrowsStyles
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.NotNull

@State(name = "AtomFileIconsConfig", storages = [Storage("a-file-icons.xml")]) // NON-NLS
class AtomFileIconsConfig : PersistentStateComponent<AtomFileIconsConfig> {
  @Property
  var isEnabledIcons = true
    private set

  @Property
  var isEnabledDirectories = true
    private set

  @Property
  var isEnabledUIIcons = true
    private set

  @Property
  var isMonochromeIcons = false
    private set

  @NonNls
  @Property
  var monochromeColor = "546E7A"
    private set

  @Property
  var isEnabledPsiIcons = true
    private set

  @Property
  var isHideFileIcons = false
    private set

  @Property
  var isUseHollowFolders = true
    private set

  @Property
  var arrowsStyle = ArrowsStyles.MATERIAL
    private set

  @Property
  var isAccentColorEnabled = false
    private set

  @NonNls
  @Property
  var accentColor = accentColorFromTheme
    private set

  @Property
  var isThemedColorEnabled = false
    private set

  @NonNls
  @Property
  var themedColor = themedColorFromTheme
    private set

  override fun getState(): AtomFileIconsConfig {
    return this
  }

  override fun loadState(state: AtomFileIconsConfig) {
    XmlSerializerUtil.copyBean(state, this)
  }

  fun fireChanged() {
    ApplicationManager.getApplication().messageBus
        .syncPublisher(AtomConfigNotifier.TOPIC)
        .configChanged(this)
  }

  fun applySettings(form: SettingsForm) {
    isEnabledIcons = form.isEnabledIcons
    isEnabledDirectories = form.isEnabledDirectories
    isEnabledUIIcons = form.isEnabledUIIcons
    isMonochromeIcons = form.isEnabledMonochromeIcons
    monochromeColor = form.monochromeColor
    isEnabledPsiIcons = form.isEnabledPsiIcons
    isHideFileIcons = form.isHiddenFileIcons
    isUseHollowFolders = form.isHollowFoldersEnabled
    arrowsStyle = form.arrowsStyle
    isAccentColorEnabled = form.isAccentColorEnabled
    accentColor = form.accentColor
    isThemedColorEnabled = form.isThemedColorEnabled
    themedColor = form.themedColor
    fireChanged()
  }

  fun resetSettings() {
    isEnabledIcons = true
    isEnabledDirectories = true
    isEnabledUIIcons = true
    isMonochromeIcons = false
    monochromeColor = "546E7A" // NON-NLS
    isEnabledPsiIcons = true
    isHideFileIcons = false
    isUseHollowFolders = true
    arrowsStyle = ArrowsStyles.MATERIAL
    isAccentColorEnabled = false
    accentColor = accentColorFromTheme
    isThemedColorEnabled = false
    themedColor = themedColorFromTheme
  }

  //region File Icons
  fun isEnabledIconsChanged(isEnabledIcons: Boolean): Boolean {
    return this.isEnabledIcons != isEnabledIcons
  }

  fun toggleEnabledIcons() {
    isEnabledIcons = !isEnabledIcons
  }
  //endregion

  //region Directory Icons
  fun isEnabledDirectoriesChanged(isEnabledDirectories: Boolean): Boolean {
    return this.isEnabledDirectories != isEnabledDirectories
  }

  fun toggleDirectoriesIcons() {
    isEnabledDirectories = !isEnabledDirectories
  }
  //endregion

  //region Monochrome Icons
  fun isMonochromeIconsChanged(isMonochrome: Boolean): Boolean {
    return isMonochromeIcons != isMonochrome
  }

  fun toggleMonochromeIcons() {
    isMonochromeIcons = !isMonochromeIcons
  }

  fun isMonochromeColorChanged(monochromeColor: String): Boolean {
    return this.monochromeColor.toLowerCase() != monochromeColor.toLowerCase()
  }
  //endregion

  //region UI Icons
  fun isEnabledUIIconsChanged(isEnabledUIIcons: Boolean): Boolean {
    return this.isEnabledUIIcons != isEnabledUIIcons
  }

  fun toggleUIIcons() {
    isEnabledUIIcons = !isEnabledUIIcons
  }
  //endregion

  //region PSI Icons
  fun isEnabledPsiIconsChanged(isEnabledPsiIcons: Boolean): Boolean {
    return this.isEnabledPsiIcons != isEnabledPsiIcons
  }

  fun togglePsiIcons() {
    isEnabledPsiIcons = !isEnabledPsiIcons
  }
  //endregion

  //region Hollow Folders
  fun isUseHollowFoldersChanged(useHollowFolders: Boolean): Boolean {
    return isUseHollowFolders != useHollowFolders
  }

  fun toggleUseHollowFolders() {
    isUseHollowFolders = !isUseHollowFolders
  }
  //endregion

  //region Hide File Icons
  fun isHideFileIconsChanged(hideFileIcons: Boolean): Boolean {
    return isHideFileIcons != hideFileIcons
  }

  fun toggleHideFileIcons() {
    isHideFileIcons = !isHideFileIcons
  }
  //endregion

  //region Arrows Style
  fun isArrowsStyleChanged(arrowsStyle: ArrowsStyles): Boolean {
    return this.arrowsStyle != arrowsStyle
  }
  //endregion

  //region Accent Color
  fun isAccentColorChanged(accentColor: String): Boolean {
    return this.accentColor.toLowerCase() != accentColor.toLowerCase()
  }

  fun isAccentColorEnabledChanged(enabled: Boolean): Boolean {
    return this.isAccentColorEnabled != enabled
  }
  //endregion

  //region Themed Color
  fun isThemedColorChanged(themedColor: String): Boolean {
    return this.themedColor.toLowerCase() != themedColor.toLowerCase()
  }

  fun isThemedColorEnabledChanged(enabled: Boolean): Boolean {
    return this.isThemedColorEnabled != enabled
  }
  //endregion

  fun getCurrentAccentColor(): String {
    if (isAccentColorEnabled) {
      return accentColor
    }
    return accentColorFromTheme
  }

  fun getCurrentThemedColor(): String {
    if (isThemedColorEnabled) {
      return themedColor
    }
    return themedColorFromTheme
  }

  companion object {
    private val accentColorFromTheme: String
      get() = getAccentFromTheme()

    private val themedColorFromTheme: String
      get() = getThemedFromTheme()

    private fun getAccentFromTheme(): @NotNull String {
      val namedKey = when (LafManager.getInstance().currentLookAndFeel?.name) {
        "Light" -> "Button.select"
        "Darcula" -> "Button.select"
        else -> "Link.activeForeground"
      }

      val namedColor = JBColor.namedColor(namedKey, UIUtil.getButtonSelectColor())
      return ColorUtil.toHex(namedColor)
    }

    private fun getThemedFromTheme(): @NotNull String {
      return ColorUtil.toHex(JBColor.namedColor("Tree.foreground", UIUtil.getLabelForeground()))
    }

    @JvmStatic
    val instance: AtomFileIconsConfig
      get() = ServiceManager.getService(AtomFileIconsConfig::class.java)
  }
}
