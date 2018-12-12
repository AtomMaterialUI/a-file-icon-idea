/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2018 Chris Magnussen and Elior Boukhobza
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

  public boolean needsRestart(final SettingsForm form) {
    return this.isEnabledUIIconsChanged(form.getIsEnabledUIIcons());
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
    this.setIsEnabledIcons(form.getIsEnabledIcons());
    this.setEnabledDirectories(form.getIsEnabledDirectories());
    this.setEnabledUIIcons(form.getIsEnabledUIIcons());
    this.setMonochromeIcons(form.getIsEnabledMonochromeIcons());

    this.fireChanged();
  }

  /**
   * Convenience method to reset settings
   */
  public void resetSettings() {
    this.enabledIcons = true;
    this.enabledDirectories = true;
    this.enabledUIIcons = true;
    this.monochromeIcons = false;
  }

  //region Enabled Icons
  public boolean isEnabledIcons() {
    return this.enabledIcons;
  }

  private void setIsEnabledIcons(final boolean enabledIcons) {
    this.enabledIcons = enabledIcons;
  }

  public boolean isEnabledIconsChanged(final boolean isEnabledIcons) {
    return this.isEnabledIcons() != isEnabledIcons;
  }
  //endregion

  //region Enabled directories
  public boolean isEnabledDirectories() {
    return this.enabledDirectories;
  }

  private void setEnabledDirectories(final boolean enabledDirectories) {
    this.enabledDirectories = enabledDirectories;
  }

  public boolean isEnabledDirectoriesChanged(final boolean isEnabledDirectories) {
    return this.enabledDirectories != isEnabledDirectories;
  }

  //endregion

  //region Monochrome icons
  public boolean isMonochromeIcons() {
    return this.monochromeIcons;
  }

  private void setMonochromeIcons(final boolean monochromeIcons) {
    this.monochromeIcons = monochromeIcons;
  }

  public boolean isMonochromeIconsChanged(final boolean isMonochrome) {
    return this.monochromeIcons != isMonochrome;
  }

  //endregion

  //region UI Icons
  public boolean isEnabledUIIcons() {
    return this.enabledUIIcons;
  }

  private void setEnabledUIIcons(final boolean enabledUIIcons) {
    this.enabledUIIcons = enabledUIIcons;
  }

  public boolean isEnabledUIIconsChanged(final boolean isEnabledUIIcons) {
    return this.enabledUIIcons != isEnabledUIIcons;
  }
  //endregion

  public void toggleEnabledIcons() {
    this.enabledIcons = !this.enabledIcons;
  }

  public void toggleDirectoriesIcons() {
    this.enabledDirectories = !this.enabledDirectories;
  }

  public void toggleUIIcons() {
    this.enabledUIIcons = !this.enabledUIIcons;
  }

  public void toggleMonochromeIcons() {
    this.monochromeIcons = !this.monochromeIcons;
  }
}
