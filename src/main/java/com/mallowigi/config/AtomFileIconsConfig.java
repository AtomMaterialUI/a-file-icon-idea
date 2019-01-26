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

package com.mallowigi.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Property;
import com.mallowigi.config.ui.SettingsForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "AtomFileIconsConfig",
    storages = @Storage("a-file-icons.xml")
)
public class AtomFileIconsConfig implements PersistentStateComponent<AtomFileIconsConfig> {
  @Property
  private boolean enabledIcons = true;
  @Property
  private boolean enabledDirectories = true;
  @Property
  private boolean enabledUIIcons = true;
  @Property
  private boolean monochromeIcons = false;
  @Property
  private String monochromeColor = "546E7A";
  @Property
  private boolean enabledPsiIcons = true;

  public AtomFileIconsConfig() {
  }

  /**
   * Get instance of the config from the ServiceManager
   *
   * @return the MTConfig instance
   */
  public static AtomFileIconsConfig getInstance() {
    return ServiceManager.getService(AtomFileIconsConfig.class);
  }

  /**
   * Get the state of MTConfig
   */
  @Nullable
  @Override
  public AtomFileIconsConfig getState() {
    return this;
  }

  /**
   * Load the state from XML
   *
   * @param state the MTConfig instance
   */
  @Override
  public void loadState(@NotNull final AtomFileIconsConfig state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  /**
   * Fire an event to the application bus that the settings have changed
   */
  public void fireChanged() {
    ApplicationManager.getApplication().getMessageBus()
                      .syncPublisher(ConfigNotifier.CONFIG_TOPIC)
                      .configChanged(this);
  }

  void applySettings(final SettingsForm form) {
    setIsEnabledIcons(form.getIsEnabledIcons());
    setEnabledDirectories(form.getIsEnabledDirectories());
    setEnabledUIIcons(form.getIsEnabledUIIcons());
    setMonochromeIcons(form.getIsEnabledMonochromeIcons());
    setMonochromeColor(form.getMonochromeColor());
    setEnabledPsiIcons(form.getIsEnabledPsiIcons());

    fireChanged();
  }

  /**
   * Convenience method to reset settings
   */
  public void resetSettings() {
    enabledIcons = true;
    enabledDirectories = true;
    enabledUIIcons = true;
    monochromeIcons = false;
    monochromeColor = "546E7A";
    enabledPsiIcons = true;
  }

  //region Enabled Icons
  public boolean isEnabledIcons() {
    return enabledIcons;
  }

  private void setIsEnabledIcons(final boolean enabledIcons) {
    this.enabledIcons = enabledIcons;
  }

  public boolean isEnabledIconsChanged(final boolean isEnabledIcons) {
    return isEnabledIcons() != isEnabledIcons;
  }

  public void toggleEnabledIcons() {
    enabledIcons = !enabledIcons;
  }
  //endregion

  //region Enabled directories
  public boolean isEnabledDirectories() {
    return enabledDirectories;
  }

  private void setEnabledDirectories(final boolean enabledDirectories) {
    this.enabledDirectories = enabledDirectories;
  }

  public boolean isEnabledDirectoriesChanged(final boolean isEnabledDirectories) {
    return enabledDirectories != isEnabledDirectories;
  }

  public void toggleDirectoriesIcons() {
    enabledDirectories = !enabledDirectories;
  }
  //endregion

  //region Monochrome icons
  public boolean isMonochromeIcons() {
    return monochromeIcons;
  }

  private void setMonochromeIcons(final boolean monochromeIcons) {
    this.monochromeIcons = monochromeIcons;
  }

  public boolean isMonochromeIconsChanged(final boolean isMonochrome) {
    return monochromeIcons != isMonochrome;
  }

  public void toggleMonochromeIcons() {
    monochromeIcons = !monochromeIcons;
  }
  //endregion

  //region UI Icons
  public boolean isEnabledUIIcons() {
    return enabledUIIcons;
  }

  private void setEnabledUIIcons(final boolean enabledUIIcons) {
    this.enabledUIIcons = enabledUIIcons;
  }

  public boolean isEnabledUIIconsChanged(final boolean isEnabledUIIcons) {
    return enabledUIIcons != isEnabledUIIcons;
  }

  public void toggleUIIcons() {
    enabledUIIcons = !enabledUIIcons;
  }
  //endregion

  //region monochrome color
  public String getMonochromeColor() {
    return monochromeColor;
  }

  private void setMonochromeColor(final String monochromeColor) {
    this.monochromeColor = monochromeColor;
  }

  public boolean isMonochromeColorChanged(final String monochromeColor) {
    return !this.monochromeColor.equals(monochromeColor);
  }
  //endregion

  // region psi icons
  public boolean isEnabledPsiIcons() {
    return enabledPsiIcons;
  }

  private void setEnabledPsiIcons(final boolean enabledPsiIcons) {
    this.enabledPsiIcons = enabledPsiIcons;
  }

  public boolean isEnabledPsiIconsChanged(final boolean isEnabledPsiIcons) {
    return enabledPsiIcons != isEnabledPsiIcons;
  }

  public void togglePsiIcons() {
    enabledPsiIcons = !enabledPsiIcons;
  }

  //endregion
}
