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

package com.mallowigi.config.associations

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Property
import com.mallowigi.config.associations.ui.AssociationsForm
import com.mallowigi.config.listeners.AssocConfigNotifier
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.CustomAssociations
import java.util.Objects

/**
 * Atom assoc config
 *
 * @constructor Create empty Atom assoc config
 */
@State(
  name = "Atom Icon Associations Config",
  storages = [Storage("atom-icon-associations.xml")] // NON-NLS:
)
class AtomAssocConfig : PersistentStateComponent<AtomAssocConfig> {

  @Property
  var customFileAssociations: CustomAssociations = CustomAssociations()

  @Property
  var customFolderAssociations: CustomAssociations = CustomAssociations()

  override fun getState(): AtomAssocConfig = this

  override fun loadState(state: AtomAssocConfig) {
    XmlSerializerUtil.copyBean(state, this)
  }

  /**
   * Apply custom file and folder associations
   *
   * @param form
   */
  fun applySettings(form: AssociationsForm) {
    customFileAssociations = form.fileAssociations
    customFolderAssociations = form.folderAssociations

    fireChanged()
  }

  private fun fireChanged() {
    ApplicationManager.getApplication().messageBus
      .syncPublisher(AssocConfigNotifier.TOPIC)
      .assocChanged(this)
  }

  /**
   * Is file icons modified
   *
   * @param customFileAssociations new file associations to compare to
   * @return true if they differ
   */
  fun isFileIconsModified(customFileAssociations: List<Association>): Boolean =
    !Objects.deepEquals(this.customFileAssociations, customFileAssociations)

  /**
   * Is folder icons modified
   *
   * @param customFolderAssociations new folder associations to compare to
   * @return true if they differ
   */
  fun isFolderIconsModified(customFolderAssociations: List<Association>): Boolean =
    !Objects.deepEquals(this.customFolderAssociations, customFolderAssociations)

  companion object {
    @JvmStatic
    val instance: AtomAssocConfig
      get() = ServiceManager.getService(AtomAssocConfig::class.java)
  }
}
