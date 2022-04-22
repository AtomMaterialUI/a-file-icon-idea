/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.config.select

import com.intellij.openapi.options.SearchableConfigurable
import com.mallowigi.config.AtomSettingsBundle
import com.mallowigi.config.ConfigurableBase
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import java.util.Objects

/** Configurable for Custom Associations. */
class AtomSelectConfigurable : ConfigurableBase<AtomSelectForm?, AtomSelectConfig?>(), SearchableConfigurable {
  override val config: AtomSelectConfig
    get() = AtomSelectConfig.instance

  /** Configurable display name. */
  @Nls
  override fun getDisplayName(): String = AtomSettingsBundle.message("AtomSelectForm.title")

  /** Configurable ID. */
  override fun getId(): String = ID

  override fun createForm(): AtomSelectForm = AtomSelectForm()

  override fun setFormState(form: AtomSelectForm?, config: AtomSelectConfig?) {
    form?.setFormState(config)
  }

  override fun doApply(form: AtomSelectForm?, config: AtomSelectConfig?) {
    config?.applySettings(form ?: return)
  }

  override fun checkModified(form: AtomSelectForm?, config: AtomSelectConfig?): Boolean =
    checkFormModified(form!!, config)

  companion object {
    /** Configurable ID. */
    @NonNls
    const val ID: String = "AtomSelectConfig"

    private fun checkFormModified(form: AtomSelectForm, config: AtomSelectConfig?): Boolean =
      Objects.requireNonNull(form)!!.isModified(config)
  }
}
