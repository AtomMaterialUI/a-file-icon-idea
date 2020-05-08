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
package com.mallowigi.config.associations

import com.intellij.openapi.options.SearchableConfigurable
import com.mallowigi.config.ConfigurableBase
import com.mallowigi.config.SettingsBundle
import com.mallowigi.config.ui.AssociationsForm
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

/**
 * Service used to load and save settings from MTConfig
 */
class AssocConfigurable : ConfigurableBase<AssociationsForm?, AtomAssocConfig?>(),
                          SearchableConfigurable {
  @Nls
  override fun getDisplayName() = SettingsBundle.message("AssociationsForm.title")

  override fun getId(): String = ID

  override fun getConfig(): AtomAssocConfig {
    return AtomAssocConfig.instance;
  }

  override fun createForm(): AssociationsForm {
    return AssociationsForm()
  }

  protected override fun setFormState(form: AssociationsForm?, config: AtomAssocConfig?) {
    form?.setFormState(config)
  }

  protected override fun doApply(form: AssociationsForm?, config: AtomAssocConfig?) {
    config?.applySettings(form);
  }

  protected override fun checkModified(form: AssociationsForm?, config: AtomAssocConfig?): Boolean {
    return checkFormModified(form!!, config)
  }

  companion object {
    @NonNls
    private const val ID = "com.mallowigi.config.associations"

    /**
     * Checks whether the form is modified by comparing to the config
     *
     * @param config the config
     * @return true if changed
     */
    private fun checkFormModified(form: AssociationsForm, config: AtomAssocConfig?): Boolean {
      return form.isModified(config);
    }
  }
}