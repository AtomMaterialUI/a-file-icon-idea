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
package com.mallowigi.config

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.Associations
import com.mallowigi.icons.associations.DefaultAssociations
import com.mallowigi.icons.associations.RegexAssociation
import com.mallowigi.icons.services.AssociationsFactory
import com.mallowigi.models.IconType
import com.mallowigi.utils.getValue

/** Service for managing bundled [Associations]. */
@Service(Service.Level.APP)
class BundledAssociations {

  /** All loaded file [Associations]. */
  private var defaultFileAssociations: MutableMap<String, RegexAssociation> = mutableMapOf()

  /** All loaded folder [Associations]. */
  private var defaultFolderAssociations: MutableMap<String, RegexAssociation> = mutableMapOf()

  /** Default psi associations. */
  private var defaultPsiAssociations: MutableMap<String, RegexAssociation> = mutableMapOf()

  init {
    init()
  }

  /**
   * Get a default [Association] by name and [IconType]
   *
   * @param name the name
   * @param iconType the [IconType]
   */
  fun getDefault(name: String, iconType: IconType): RegexAssociation? = getMap(iconType)[name]

  /**
   * Get the list of [RegexAssociation]s
   *
   * @param iconType the [IconType]
   * @return the list
   */
  fun getList(iconType: IconType): List<RegexAssociation> = getMap(iconType).values.toList()

  /**
   * Returns the relevant list according to the [IconType]
   *
   * @param iconType
   * @return
   */
  fun getMap(iconType: IconType): MutableMap<String, RegexAssociation> = when (iconType) {
    IconType.FILE -> defaultFileAssociations
    IconType.FOLDER -> defaultFolderAssociations
    IconType.PSI -> defaultPsiAssociations
  }

  /**
   * Checks if an [Association] is already registered in the defaults
   *
   * @param name assoc name
   * @param iconType the [IconType]
   */
  private fun hasDefault(name: String, iconType: IconType): Boolean = getMap(iconType).containsKey(name)

  /** Load bundled associations. */
  private fun init() {
    folderAssociations.getTheAssociations()
      .filterIsInstance<RegexAssociation>()
      .forEach { insert(it.name, it, IconType.FOLDER) }

    fileAssociations.getTheAssociations()
      .filterIsInstance<RegexAssociation>()
      .forEach { insert(it.name, it, IconType.FILE) }
  }

  /**
   * Insert a new default [RegexAssociation]
   *
   * @param name assoc name
   * @param assoc the [RegexAssociation]
   * @param iconType the [IconType]
   */
  private fun insert(name: String, assoc: RegexAssociation, iconType: IconType) {
    if (hasDefault(name, iconType)) return

    val map = getMap(iconType)

    map[name] = assoc
    map[name]?.enabled = true
  }

  companion object {
    /** Service instance. */
    val instance: BundledAssociations by lazy { service() }

    /** Load folder associations from XML. */
    val folderAssociations: DefaultAssociations =
      AssociationsFactory.create("/iconGenerator/folder_associations.xml")

    /** Load file associations from XML. */
    val fileAssociations: DefaultAssociations =
      AssociationsFactory.create("/iconGenerator/icon_associations.xml")

  }

}
