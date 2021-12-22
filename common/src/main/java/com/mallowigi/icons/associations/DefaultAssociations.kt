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
import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/**
 * Represents the bundled list of associations parsed from the xml
 *
 * @constructor Create empty [DefaultAssociations]
 */
@Suppress("HardCodedStringLiteral")
@XStreamAlias("associations")
class DefaultAssociations : Associations() {

  /**
   * List of [Association]s
   */
  @Property
  @XCollection
  @XStreamAsAttribute
  var associations: List<Association> = emptyList()

  /**
   * Find the file info's matching association by looping over all associations sorted by priority,
   * taking the first enabled one that matches.
   *
   * @param file file information
   * @return [Association] if found
   */
  override fun findMatchingAssociation(file: FileInfo): Association? =
    associations
      .filter { association: Association -> association.matches(file) }
      .maxByOrNull { it.priority }

  /**
   * Return list of [DefaultAssociations]
   *
   * @return list of [DefaultAssociations]
   */
  override fun getTheAssociations(): List<Association> = associations

}
