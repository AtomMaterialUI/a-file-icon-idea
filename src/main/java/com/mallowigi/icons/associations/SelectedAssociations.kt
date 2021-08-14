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
 * Custom associations
 *
 * @constructor Create empty Custom associations
 */
@Suppress("MemberNameEqualsClassName")
class SelectedAssociations : Associations {
  @Property
  @XCollection
  private val myAssociations: MutableMap<String, RegexAssociation>

  constructor() {
    myAssociations = mutableMapOf()
  }

  constructor(associations: Map<String, RegexAssociation>) {
    myAssociations = copyOf(associations)
  }

  constructor(associations: List<RegexAssociation>) {
    myAssociations = associations.associateBy { it.name }.toMutableMap()
  }

  /**
   * Has the association?
   *
   * @param name
   */
  fun has(name: String): Boolean = myAssociations.containsKey(name)

  /**
   * Get an association
   *
   * @param name
   */
  fun get(name: String): RegexAssociation? = myAssociations[name]

  /**
   * Add association to map if not exists
   *
   * @param name
   * @param assoc
   */
  fun set(name: String, assoc: RegexAssociation) {
    if (has(name)) return

    myAssociations[name] = assoc
  }

  /**
   * get the association values
   *
   * @return
   */
  fun values(): List<RegexAssociation> = myAssociations.values.toList()

  override fun findMatchingAssociation(file: FileInfo?): Association? =
    values().stream()
      .filter { association: Association -> association.matches(file!!) }
      .findAny()
      .orElse(null)


  override fun getTheAssociations(): List<RegexAssociation> = values()


}
