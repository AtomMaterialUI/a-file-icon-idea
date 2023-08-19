/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Elior "Mallowigi" Boukhobza
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
 */

package com.mallowigi.config.select

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Property
import com.mallowigi.config.listeners.AtomSelectNotifier
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.SelectedAssociations
import com.mallowigi.models.IconType
import com.mallowigi.utils.getValue
import java.util.*

/** Configuration for [SelectedAssociations]. */
@State(
  name = "Atom Icon Selections Config",
  storages = [Storage("atom-icon-associations.xml")],
  category = SettingsCategory.UI
)
class AtomSelectConfig : PersistentStateComponent<AtomSelectConfig> {

  private var firstRun: Boolean = true

  /** List of user file [Association]s. */
  @Property
  var selectedFileAssociations: SelectedAssociations = SelectedAssociations(IconType.FILE)

  /** List of user folder [Association]s. */
  @Property
  var selectedFolderAssociations: SelectedAssociations = SelectedAssociations(IconType.FOLDER)

  /** List of user folder open [Association]s. */
  @Property
  var selectedFolderOpenAssociations: SelectedAssociations = SelectedAssociations(IconType.FOLDER)

  init {
    init()
  }

  fun apply(fileAssociations: SelectedAssociations,
            folderAssociations: SelectedAssociations,
            folderOpenAssociations: SelectedAssociations) {
    selectedFileAssociations = fileAssociations
    selectedFolderAssociations = folderAssociations
    selectedFolderOpenAssociations = folderOpenAssociations

    selectedFileAssociations.registerOwnAssociations()
    selectedFolderAssociations.registerOwnAssociations()
    selectedFolderOpenAssociations.registerOwnAssociations()

    fireChanged()
  }

  /** Find association by name. */
  fun findFileAssociationByName(name: String): Association? = selectedFileAssociations.findAssociationByName(name)

  /** Find folder association by name. */
  fun findFolderAssociationByName(name: String): Association? = selectedFolderAssociations.findAssociationByName(name)

  /** Find folder open association by name. */
  fun findFolderOpenAssociationByName(name: String): Association? = selectedFolderOpenAssociations.findAssociationByName(name)

  /**
   * Is file icons modified
   *
   * @param fileAssociations new file associations to compare to
   * @return true if they differ
   */
  fun isFileIconsModified(fileAssociations: List<Association>): Boolean {
    val touched = fileAssociations.filter { it.touched }

    return !Objects.deepEquals(this.selectedFileAssociations.ownValues(), touched)
  }

  /**
   * Is folder icons modified
   *
   * @param folderAssociations new folder associations to compare to
   * @return true if they differ
   */
  fun isFolderIconsModified(folderAssociations: List<Association>): Boolean {
    val touched = folderAssociations.filter { it.touched }

    return !Objects.deepEquals(this.selectedFolderAssociations.ownValues(), touched)
  }

  fun isFolderOpenIconsModified(folderOpenAssociations: List<Association>): Boolean {
    val touched = folderOpenAssociations.filter { it.touched }

    return !Objects.deepEquals(this.selectedFolderOpenAssociations.ownValues(), touched)
  }

  /** Resets the associations. */
  fun reset() {
    selectedFolderAssociations.reset()
    selectedFileAssociations.reset()
    selectedFolderOpenAssociations.reset()
  }

  /** The config state. */
  override fun getState(): AtomSelectConfig = this

  /** Load state from XML. */
  override fun loadState(state: AtomSelectConfig) {
    val changed = state != this
    XmlSerializerUtil.copyBean(state, this)

    if (changed && !firstRun) {
      ApplicationManager.getApplication().invokeAndWait { fireChanged() }
    }
    firstRun = false
    init()
  }

  private fun fireChanged() {
    ApplicationManager.getApplication().messageBus
      .syncPublisher(AtomSelectNotifier.TOPIC)
      .configChanged(this)
  }

  private fun init() {
    selectedFolderAssociations.initMutableListFromDefaults()
    selectedFileAssociations.initMutableListFromDefaults()
    selectedFolderOpenAssociations.initMutableListFromDefaults()
  }

  companion object {
    /** Instance of the [AtomSelectConfig]. */
    @JvmStatic
    val instance: AtomSelectConfig by lazy { service() }
  }
}
