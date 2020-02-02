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

import com.intellij.openapi.options.SearchableConfigurable;
import com.mallowigi.config.ui.SettingsForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Service used to load and save settings from MTConfig
 */
public final class Configurable extends ConfigurableBase<SettingsForm, AtomFileIconsConfig> implements SearchableConfigurable {

  private static final String ID = "com.mallowigi.config";

  @Nls
  @Override
  public String getDisplayName() {
    return SettingsBundle.message("settings.title");
  }

  @NotNull
  @Override
  public String getId() {
    return ID;
  }

  @Override
  protected AtomFileIconsConfig getConfig() {
    return AtomFileIconsConfig.getInstance();
  }

  @Override
  protected SettingsForm createForm() {
    return new SettingsForm();
  }

  @Override
  protected void setFormState(final SettingsForm form, final AtomFileIconsConfig config) {
    Objects.requireNonNull(form).setFormState(config);
  }

  @Override
  protected void doApply(final SettingsForm form, final AtomFileIconsConfig config) {
    config.applySettings(form);
  }

  @Override
  protected boolean checkModified(final SettingsForm form, final AtomFileIconsConfig config) {
    return checkFormModified(form, config);
  }

  /**
   * Checks whether the form is modified by comparing to the config
   *
   * @param config the config
   * @return true if changed
   */
  private static boolean checkFormModified(final SettingsForm form, final AtomFileIconsConfig config) {
    return Objects.requireNonNull(form).isModified(config);
  }
}
