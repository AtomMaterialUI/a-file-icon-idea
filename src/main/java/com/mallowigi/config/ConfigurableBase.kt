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

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.options.BaseConfigurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.util.Computable
import com.intellij.util.ui.UIUtil
import com.mallowigi.config.ui.SettingsFormUI
import javax.swing.JComponent

abstract class ConfigurableBase<FORM : SettingsFormUI?, CONFIG : PersistentStateComponent<*>?> :
  BaseConfigurable() {
  /**
   * Return the created form
   */
  @Volatile
  private var form: FORM? = null

  /**
   * Link a form to a config
   *
   * @param form   the form
   * @param config the config
   */
  protected abstract fun setFormState(form: FORM?, config: CONFIG)

  /**
   * Returns the config object for this setting
   */
  protected abstract val config: CONFIG

  /**
   * Create the Form UI for the settings
   */
  protected abstract fun createForm(): FORM

  /**
   * Called when applying settings
   *
   * @param form   the form
   * @param config the config
   */
  @Throws(ConfigurationException::class)
  protected abstract fun doApply(form: FORM?, config: CONFIG)

  /**
   * Checks whether the user has modified the settings
   *
   * @param form   the form
   * @param config the config
   * @return true if modified
   */
  protected abstract fun checkModified(form: FORM?, config: CONFIG): Boolean

  /**
   * Used to disable the apply button
   *
   * @return true if modified
   */
  override fun isModified(): Boolean {
    isModified = checkModified(form, config)
    return super.isModified()
  }

  /**
   * Creates the component and link it to a config
   *
   * @return the created component
   */
  override fun createComponent(): JComponent? {
    initComponent()
    setFormState(form, config)
    return form!!.content
  }

  /**
   * Apply settings
   */
  @Throws(ConfigurationException::class)
  override fun apply() {
    initComponent()
    doApply(form, config)
  }

  /**
   * Reset settings
   */
  override fun reset() {
    initComponent()
    setFormState(form, config)
  }

  /**
   * Dispose the FORM on dispose
   */
  @Synchronized
  override fun disposeUIResources() {
    dispose()
    form?.dispose()
    form = null
  }

  /**
   * Dispose resources
   */
  private fun dispose() {
    // Do nothing
  }

  /**
   * Creates the component with Swing
   */
  @Synchronized
  private fun initComponent() {
    if (form == null) {
      form = UIUtil.invokeAndWaitIfNeeded(Computable {
        val form = createForm()
        form!!.init()
        form
      })
    }
  }
}