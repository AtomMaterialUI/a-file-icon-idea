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

package com.mallowigi.config.select

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Property
import com.mallowigi.config.listeners.AtomSelectNotifier
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.RegexAssociation
import com.mallowigi.icons.associations.SelectedAssociations
import com.mallowigi.icons.providers.DefaultFileIconProvider
import com.mallowigi.icons.providers.DefaultFolderIconProvider
import java.util.Objects

/**
 * Configuration for [SelectedAssociations]
 *
 */
@State(
  name = "Atom Icon Selections Config",
  storages = [Storage("atom-icon-associations.xml")],
  category = SettingsCategory.UI
)
class AtomSelectConfig : PersistentStateComponent<AtomSelectConfig> {
  @Property
  var selectedFileAssociations: SelectedAssociations = SelectedAssociations()

  @Property
  var selectedFolderAssociations: SelectedAssociations = SelectedAssociations()

  override fun getState(): AtomSelectConfig = this

  override fun loadState(state: AtomSelectConfig) {
    XmlSerializerUtil.copyBean(state, this)
    init() // reload defaults
  }

  /**
   * Apply settings
   *
   * @param form
   */
  fun applySettings(form: AtomSelectForm) {
    selectedFileAssociations = form.fileAssociations
    selectedFolderAssociations = form.folderAssociations

    selectedFileAssociations.registerOwnAssociations()
    selectedFolderAssociations.registerOwnAssociations()

    fireChanged()
  }

  private fun fireChanged() {
    ApplicationManager.getApplication().messageBus
      .syncPublisher(AtomSelectNotifier.TOPIC)
      .configChanged(this)
  }

  /**
   * Is file icons modified
   *
   * @param fileAssociations new file associations to compare to
   * @return true if they differ
   */
  fun isFileIconsModified(fileAssociations: List<Association>): Boolean =
    !Objects.deepEquals(this.selectedFileAssociations, fileAssociations)

  /**
   * Is folder icons modified
   *
   * @param folderAssociations new folder associations to compare to
   * @return true if they differ
   */
  fun isFolderIconsModified(folderAssociations: List<Association>): Boolean =
    !Objects.deepEquals(this.selectedFolderAssociations, folderAssociations)

  /**
   * Resets the associations
   *
   */
  fun reset() {
    selectedFolderAssociations.clear()
    selectedFileAssociations.clear()
  }

  private fun init() {
    val folderAssociations = DefaultFolderIconProvider.associations.getTheAssociations()
    folderAssociations
      .filterIsInstance<RegexAssociation>()
      .forEach { selectedFolderAssociations.insertDefault(it.name, it) }

    val fileAssociations = DefaultFileIconProvider.associations.getTheAssociations()
    fileAssociations
      .filterIsInstance<RegexAssociation>()
      .forEach { selectedFileAssociations.insertDefault(it.name, it) }
  }

  companion object {
    @JvmStatic
    val instance: AtomSelectConfig
      get() = ApplicationManager.getApplication().getService(AtomSelectConfig::class.java)
  }
}
