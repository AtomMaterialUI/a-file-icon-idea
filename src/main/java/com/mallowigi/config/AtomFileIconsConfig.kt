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

import com.intellij.ide.ui.LafManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
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

/**
 * Atom file icons config
 *
 * @constructor Create empty Atom file icons config
 */
@Suppress("TooManyFunctions")
@State(name = "AtomFileIconsConfig", storages = [Storage("a-file-icons.xml")]) // NON-NLS
class AtomFileIconsConfig : PersistentStateComponent<AtomFileIconsConfig> {
  @Property
  var isEnabledIcons: Boolean = true

  @Property
  var isEnabledDirectories: Boolean = true

  @Property
  var isEnabledUIIcons: Boolean = true

  @Property
  var isMonochromeIcons: Boolean = false

  @NonNls
  @Property
  var monochromeColor: String = "546E7A"
    private set

  @Property
  var isEnabledPsiIcons: Boolean = true

  @Property
  var isHideFileIcons: Boolean = false

  @Property
  var isHideFolderIcons: Boolean = false

  @Property
  var isUseHollowFolders: Boolean = true

  @Property
  var arrowsStyle: ArrowsStyles = ArrowsStyles.MATERIAL

  @Property
  var isAccentColorEnabled: Boolean = false

  @NonNls
  @Property
  var accentColor: String = accentColorFromTheme
    private set

  @Property
  var isThemedColorEnabled: Boolean = false

  @NonNls
  @Property
  var themedColor: String = themedColorFromTheme
    private set

  override fun getState(): AtomFileIconsConfig {
    return this
  }

  override fun loadState(state: AtomFileIconsConfig) {
    XmlSerializerUtil.copyBean(state, this)
  }

  /**
   * Fire event when settings are changed
   *
   */
  fun fireChanged() {
    ApplicationManager.getApplication().messageBus
      .syncPublisher(AtomConfigNotifier.TOPIC)
      .configChanged(this)
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
    fireChanged()
  }

  /**
   * Reset settings
   *
   */
  fun resetSettings() {
    isEnabledIcons = true
    isEnabledDirectories = true
    isEnabledUIIcons = true
    isMonochromeIcons = false
    monochromeColor = "546E7A" // NON-NLS
    isEnabledPsiIcons = true
    isHideFileIcons = false
    isHideFolderIcons = false
    isUseHollowFolders = true
    arrowsStyle = ArrowsStyles.MATERIAL
    isAccentColorEnabled = false
    accentColor = accentColorFromTheme
    isThemedColorEnabled = false
    themedColor = themedColorFromTheme
  }

  //region File Icons
  /**
   * Is enabled icons changed
   *
   * @param isEnabledIcons
   * @return
   */
  fun isEnabledIconsChanged(isEnabledIcons: Boolean): Boolean = this.isEnabledIcons != isEnabledIcons

  /**
   * Toggle enabled icons
   *
   */
  fun toggleEnabledIcons() {
    isEnabledIcons = !isEnabledIcons
  }
  //endregion

  //region Directory Icons
  /**
   * Is enabled directories changed
   *
   * @param isEnabledDirectories
   * @return
   */
  fun isEnabledDirectoriesChanged(isEnabledDirectories: Boolean): Boolean =
    this.isEnabledDirectories != isEnabledDirectories

  /**
   * Toggle directories icons
   *
   */
  fun toggleDirectoriesIcons() {
    isEnabledDirectories = !isEnabledDirectories
  }
  //endregion

  //region Monochrome Icons
  /**
   * Is monochrome icons changed
   *
   * @param isMonochrome
   * @return
   */
  fun isMonochromeIconsChanged(isMonochrome: Boolean): Boolean = isMonochromeIcons != isMonochrome

  /**
   * Toggle monochrome icons
   *
   */
  fun toggleMonochromeIcons() {
    isMonochromeIcons = !isMonochromeIcons
  }

  /**
   * Is monochrome color changed
   *
   * @param monochromeColor
   * @return
   */
  fun isMonochromeColorChanged(monochromeColor: String): Boolean =
    this.monochromeColor.lowercase() != monochromeColor.lowercase()
  //endregion

  //region UI Icons
  /**
   * Is enabled ui icons changed
   *
   * @param isEnabledUIIcons
   * @return
   */
  fun isEnabledUIIconsChanged(isEnabledUIIcons: Boolean): Boolean = this.isEnabledUIIcons != isEnabledUIIcons

  /**
   * Toggle ui icons
   *
   */
  fun toggleUIIcons() {
    isEnabledUIIcons = !isEnabledUIIcons
  }
  //endregion

  //region PSI Icons
  /**
   * Is enabled psi icons changed
   *
   * @param isEnabledPsiIcons
   * @return
   */
  fun isEnabledPsiIconsChanged(isEnabledPsiIcons: Boolean): Boolean = this.isEnabledPsiIcons != isEnabledPsiIcons

  /**
   * Toggle psi icons
   *
   */
  fun togglePsiIcons() {
    isEnabledPsiIcons = !isEnabledPsiIcons
  }
  //endregion

  //region Hollow Folders
  /**
   * Is use hollow folders changed
   *
   * @param useHollowFolders
   * @return
   */
  fun isUseHollowFoldersChanged(useHollowFolders: Boolean): Boolean = isUseHollowFolders != useHollowFolders

  /**
   * Toggle use hollow folders
   *
   */
  fun toggleUseHollowFolders() {
    isUseHollowFolders = !isUseHollowFolders
  }
  //endregion

  //region Hide File Icons
  /**
   * Is hide file icons changed
   *
   * @param hideFileIcons
   * @return
   */
  fun isHideFileIconsChanged(hideFileIcons: Boolean): Boolean = isHideFileIcons != hideFileIcons

  /**
   * Toggle hide file icons
   *
   */
  fun toggleHideFileIcons() {
    isHideFileIcons = !isHideFileIcons
  }
  //endregion

  //region Hide Folder Icons
  /**
   * Is hide folder icons changed
   *
   * @param hideFolderIcons
   * @return
   */
  fun isHideFolderIconsChanged(hideFolderIcons: Boolean): Boolean = isHideFolderIcons != hideFolderIcons

  /**
   * Toggle hide folder icons
   *
   */
  fun toggleHideFolderIcons() {
    isHideFolderIcons = !isHideFolderIcons
  }
  //endregion

  //region Arrows Style
  /**
   * Is arrows style changed
   *
   * @param arrowsStyle
   * @return
   */
  fun isArrowsStyleChanged(arrowsStyle: ArrowsStyles): Boolean = this.arrowsStyle != arrowsStyle
  //endregion

  //region Accent Color
  /**
   * Is accent color changed
   *
   * @param accentColor
   * @return
   */
  fun isAccentColorChanged(accentColor: String): Boolean = this.accentColor.lowercase() != accentColor.lowercase()

  /**
   * Is accent color enabled changed
   *
   * @param enabled
   * @return
   */
  fun isAccentColorEnabledChanged(enabled: Boolean): Boolean = this.isAccentColorEnabled != enabled
  //endregion

  //region Themed Color
  /**
   * Is themed color changed
   *
   * @param themedColor
   * @return
   */
  fun isThemedColorChanged(themedColor: String): Boolean = this.themedColor.lowercase() != themedColor.lowercase()

  /**
   * Is themed color enabled changed
   *
   * @param enabled
   * @return
   */
  fun isThemedColorEnabledChanged(enabled: Boolean): Boolean = this.isThemedColorEnabled != enabled
  //endregion

  /**
   * Get current accent color
   *
   * @return
   */
  fun getCurrentAccentColor(): String {
    if (isAccentColorEnabled) return accentColor
    return accentColorFromTheme
  }

  /**
   * Get current themed color
   *
   * @return
   */
  fun getCurrentThemedColor(): String {
    if (isThemedColorEnabled) return themedColor
    return themedColorFromTheme
  }

  companion object {
    @JvmStatic
    val instance: AtomFileIconsConfig
      get() = ApplicationManager.getApplication().getService(AtomFileIconsConfig::class.java)

    private val accentColorFromTheme: String
      get() = getAccentFromTheme()

    private val themedColorFromTheme: String
      get() = getThemedFromTheme()

    private fun getAccentFromTheme(): String {
      val namedKey = when (LafManager.getInstance().currentLookAndFeel?.name) {
        "Light" -> "Button.select"
        "Darcula" -> "Button.select"
        else -> "Link.activeForeground"
      }

      val namedColor = JBColor.namedColor(
        namedKey,
        UIUtil.getButtonSelectColor() ?: UIUtil.getListSelectionForeground(true)
      )
      return ColorUtil.toHex(namedColor)
    }

    private fun getThemedFromTheme(): String =
      ColorUtil.toHex(JBColor.namedColor("Tree.foreground", UIUtil.getLabelForeground()))
  }
}
