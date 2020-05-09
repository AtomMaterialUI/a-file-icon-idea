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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.serialization.Property
import com.intellij.util.xmlb.XmlSerializerUtil
import com.mallowigi.config.associations.ui.AssociationsForm
import com.mallowigi.config.listeners.AssocConfigNotifier
import com.mallowigi.icons.associations.RegexAssociation
import java.util.*
import kotlin.collections.ArrayList

@State(
  name = "Atom Icon Associations Config",
  storages = [Storage("atom-icon-associations.xml")] // NON-NLS:
)
class AtomAssocConfig : PersistentStateComponent<AtomAssocConfig> {

  @Property
  var customFileAssociations: List<RegexAssociation> = ArrayList<RegexAssociation>(100);

  @Property
  var customFolderAssociations: List<RegexAssociation> = ArrayList<RegexAssociation>(100)

  override fun getState(): AtomAssocConfig? = this

  override fun loadState(state: AtomAssocConfig) {
    XmlSerializerUtil.copyBean(state, this)
  }

  fun applySettings(form: AssociationsForm?) {
    customFileAssociations = form?.getFileAssociations() ?: listOf();
    customFolderAssociations = form?.getFolderAssociations() ?: listOf();

    fireChanged()
  }

  private fun fireChanged() {
    ApplicationManager.getApplication().messageBus
      .syncPublisher(AssocConfigNotifier.TOPIC)
      .assocChanged(this)
  }

  fun isFileIconsModified(customFileAssociations: List<RegexAssociation>): Boolean {
    return !Objects.deepEquals(this.customFileAssociations, customFileAssociations);
  }

  fun isFolderIconsModified(customFolderAssociations: List<RegexAssociation>): Boolean {
    return !Objects.deepEquals(this.customFolderAssociations, customFolderAssociations);
  }

  companion object {
    @JvmStatic
    val instance: AtomAssocConfig
      get() = ServiceManager.getService(AtomAssocConfig::class.java)
  }
}