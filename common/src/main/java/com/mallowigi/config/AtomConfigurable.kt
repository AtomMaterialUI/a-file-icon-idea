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

import com.intellij.openapi.options.SearchableConfigurable
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.config.ui.SettingsForm
import org.jetbrains.annotations.NonNls
import java.util.Objects

/**
 * Configurable for the Atom Settings
 *
 */
class AtomConfigurable : ConfigurableBase<SettingsForm?, AtomFileIconsConfig?>(),
  SearchableConfigurable {

  override val config: AtomFileIconsConfig
    get() = AtomFileIconsConfig.instance

  /**
   * Display name
   */
  override fun getDisplayName(): String = message("settings.title")

  /**
   * Configurable ID
   */
  override fun getId(): String = ID

  override fun createForm(): SettingsForm = SettingsForm()

  override fun setFormState(form: SettingsForm?, config: AtomFileIconsConfig?) {
    form?.setFormState(config)
  }

  override fun doApply(form: SettingsForm?, config: AtomFileIconsConfig?) {
    (config ?: return).applySettings(form ?: return)
  }

  override fun checkModified(form: SettingsForm?, config: AtomFileIconsConfig?): Boolean =
    checkFormModified(form, config!!)

  companion object {
    /**
     * Configurable ID
     */
    @NonNls
    const val ID: String = "AtomFileIconsConfig"

    private fun checkFormModified(form: SettingsForm?, config: AtomFileIconsConfig): Boolean =
      Objects.requireNonNull(form)!!.isModified(config)
  }

}
