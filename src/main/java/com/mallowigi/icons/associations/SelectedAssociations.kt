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
import java.util.Map.copyOf

/**
 * Represents a list of [SelectedAssociations]
 *
 */
@Suppress("MemberNameEqualsClassName")
class SelectedAssociations : Associations {
  @Property
  @XCollection
  private val myAssociations: MutableMap<String, RegexAssociation>

  constructor() {
    myAssociations = mutableMapOf()
  }

  /**
   * Copy from other [Associations] as a map
   */
  constructor(associations: Map<String, RegexAssociation>) {
    myAssociations = copyOf(associations)
  }

  /**
   * Copy from a list of other [Associations]
   */
  constructor(associations: List<RegexAssociation>) {
    myAssociations = associations.associateBy { it.name }.toMutableMap()
  }

  /**
   * Checks if an [Association] is already registered
   *
   * @param name
   */
  fun has(name: String): Boolean = myAssociations.containsKey(name)

  /**
   * Get an [Association] by name
   *
   * @param name
   */
  fun get(name: String): RegexAssociation? = myAssociations[name]

  /**
   * Inserts association to map if not exists
   *
   * @param name
   * @param assoc
   */
  fun insert(name: String, assoc: RegexAssociation) {
    if (has(name)) return

    myAssociations[name] = assoc
    myAssociations[name]?.enabled = true
  }

  /**
   * get the [Associations]
   *
   * @return
   */
  fun values(): List<RegexAssociation> = myAssociations.values.toList()

  /**
   * Find matching association with the highest priority
   *
   * @param file
   * @return the association if found
   */
  override fun findMatchingAssociation(file: FileInfo): Association? =
    values()
      .filter { association: Association -> association.enabled && association.matches(file) }
      .maxByOrNull { it.priority }

  /**
   * Get the list of [SelectedAssociations]
   *
   * @return the list of [SelectedAssociations]
   */
  override fun getTheAssociations(): List<RegexAssociation> = values()

  /**
   * Clear the list
   *
   */
  fun clear() {
    myAssociations.clear()
  }

}
