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
package com.mallowigi.icons.associations

import com.intellij.util.xmlb.annotations.Property
import com.intellij.util.xmlb.annotations.XCollection
import com.mallowigi.models.FileInfo

/**
 * Represents a list of [SelectedAssociations]
 *
 */
@Suppress("MemberNameEqualsClassName")
class SelectedAssociations(associations: List<RegexAssociation> = listOf()) : Associations() {
  /**
   * All loaded [Associations]
   */
  @Transient
  private var defaultAssociations: MutableMap<String, RegexAssociation> = mutableMapOf()

  /**
   * My modified [Associations]
   */
  @Property
  @XCollection
  private var ownAssociations: MutableMap<String, RegexAssociation> = mutableMapOf()

  init {
    // Copy from a list of other [Associations] (used when applying form)
    defaultAssociations = associations.associateBy { it.name }.toMutableMap()
  }

  /**
   * Checks if an [Association] is already registered in the defaults
   *
   * @param name
   */
  fun hasDefault(name: String): Boolean = defaultAssociations.containsKey(name)

  /**
   * Get a default [Association] by name
   *
   * @param name
   */
  fun getDefault(name: String): RegexAssociation? = defaultAssociations[name]

  /**
   * Checks if an own [Association] is already registered
   *
   * @param name
   */
  fun hasOwn(name: String): Boolean = ownAssociations.containsKey(name)

  /**
   * Get an own [Association] by name
   *
   * @param name
   */
  fun getOwn(name: String): RegexAssociation? = ownAssociations[name]

  /**
   * Inserts association to [defaultAssociations] map if not exists
   *
   * @param name
   * @param assoc
   */
  fun insertDefault(name: String, assoc: RegexAssociation) {
    if (hasDefault(name)) return

    defaultAssociations[name] = assoc
    defaultAssociations[name]?.enabled = true
  }

  /**
   * Inserts association to [ownAssociations]
   *
   * @param name
   * @param assoc
   */
  fun insertOwn(name: String, assoc: RegexAssociation) {
    ownAssociations[name] = assoc
  }

  /**
   * Gets the list of default [Associations]
   *
   * @return
   */
  fun defaultValues(): List<RegexAssociation> = defaultAssociations.values.toList()

  /**
   * Gets the list of own [Associations]
   *
   * @return
   */
  fun ownValues(): List<RegexAssociation> = ownAssociations.values.toList()

  /**
   * Find matching [Association] with the highest priority
   *
   * @param file a file's [FileInfo]
   * @return the association if found
   */
  override fun findMatchingAssociation(file: FileInfo): Association? = findInOwn(file) ?: findInDefault(file)

  /**
   * Look for [Association] in [defaultAssociations]
   *
   * @param file a file's [FileInfo]
   * @return matching [Association] if found
   */
  private fun findInDefault(file: FileInfo): Association? = defaultValues()
    .filter { association: Association -> association.enabled && association.matches(file) }
    .maxByOrNull { it.priority }

  /**
   * Look for matching association in [ownAssociations]
   *
   * @param file a file's [FileInfo]
   * @return matching association if found
   */
  private fun findInOwn(file: FileInfo): Association? = ownValues()
    .filter { association: Association -> association.enabled && association.matches(file) }
    .maxByOrNull { it.priority }

  /**
   * Get the list of all [Associations]
   *
   * @return the list of [Associations]
   */
  override fun getTheAssociations(): List<RegexAssociation> {
    // to display associations to the form, need to merge both
    val result = mutableMapOf<String, RegexAssociation>()
    result.putAll(defaultAssociations)
    result.putAll(ownAssociations)
    return result.values.toList()
  }

  /**
   * Clear [ownAssociations]
   *
   */
  fun clear() {
    ownAssociations.clear()
  }

  /**
   * Extract [ownAssociations] from [defaultAssociations]
   *
   */
  fun registerOwnAssociations() {
    ownAssociations.putAll(defaultAssociations.filter { it.value.touched })
  }

}
