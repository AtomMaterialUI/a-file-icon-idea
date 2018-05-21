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

import com.intellij.openapi.options.SearchableConfigurable;
import com.mallowigi.config.ui.SettingsForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Service used to load and save settings from MTConfig
 */
public final class Configurable extends ConfigurableBase<SettingsForm, AtomFileIconsConfig> implements SearchableConfigurable {

  public static final String ID = "com.mallowigi.config";
  public static final String HELP_ID = "AtomFileIconsConfig";

  @Nls
  @Override
  public String getDisplayName() {
    return SettingsBundle.message("settings.title");
  }

  @NotNull
  @Override
  public String getId() {
    return Configurable.ID;
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
  protected void setFormState(final SettingsForm mtForm, final AtomFileIconsConfig atomFileIconsConfig) {
    getForm().setIsEnabledIcons(atomFileIconsConfig.isEnabledIcons());
    getForm().setIsEnabledDirectories(atomFileIconsConfig.isEnabledDirectories());
    getForm().setIsEnabledUIIcons(atomFileIconsConfig.isEnabledUIIcons());

    getForm().afterStateSet();
  }

  @Override
  protected void doApply(final SettingsForm mtForm, final AtomFileIconsConfig atomFileIconsConfig) {
    atomFileIconsConfig.setIsEnabledIcons(mtForm.getIsEnabledIcons());
    atomFileIconsConfig.setEnabledDirectories(mtForm.getIsEnabledDirectories());
    atomFileIconsConfig.setEnabledUIIcons(mtForm.getIsEnabledUIIcons());

    atomFileIconsConfig.fireChanged();
  }

  @Override
  protected boolean checkModified(final SettingsForm mtForm, final AtomFileIconsConfig atomFileIconsConfig) {
    boolean modified = false;
    modified |= atomFileIconsConfig.isEnabledIconsChanged(getForm().getIsEnabledIcons());
    modified |= atomFileIconsConfig.isEnabledDirectoriesChanged(getForm().getIsEnabledDirectories());
    modified |= atomFileIconsConfig.isEnabledUIIconsChanged(getForm().getIsEnabledUIIcons());

    return modified;
  }
}
