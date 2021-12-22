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
import java.util.List.copyOf

/**
 * Custom associations are [Associations] defined by the user. They are prioritized over bundled associations.
 *
 */
@Suppress("MemberNameEqualsClassName")
class CustomAssociations : Associations {
  @Property
  @XCollection
  private val customAssociations: List<RegexAssociation>

  constructor() {
    customAssociations = emptyList()
  }

  /**
   * Initializes [Associations] from a list
   */
  constructor(associations: List<RegexAssociation>) {
    customAssociations = copyOf(associations)
  }

  /**
   * Find the file info's matching association by looping over all associations sorted by priority,
   * taking the first enabled one that matches.
   *
   * @param file file information
   * @return [Association] if found
   */
  override fun findMatchingAssociation(file: FileInfo): Association? =
    customAssociations
      .filter { association: Association -> association.enabled && association.matches(file) }
      .maxByOrNull { it.priority }

  /**
   * Return list of [CustomAssociations]
   *
   * @return list of [CustomAssociations]
   */
  override fun getTheAssociations(): List<RegexAssociation> = customAssociations

}
