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
package com.mallowigi.icons.services

import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.DefaultAssociations
import com.mallowigi.icons.associations.RegexAssociation
import com.mallowigi.icons.associations.TypeAssociation
import com.thoughtworks.xstream.XStream

/**
 * Factory for building associations from XML
 *
 */
object AssociationsFactory {

  /**
   * Generate the list of [DefaultAssociations] from XML
   *
   * @param associationsFile xml file
   * @return list of [DefaultAssociations]
   */
  fun create(associationsFile: String): DefaultAssociations {
    val associationsXml = AssociationsFactory::class.java.getResource(associationsFile)
    val xStream = XStream()

    xStream.run {
      allowTypesByWildcard(arrayOf("com.mallowigi.icons.associations.*")) // NON-NLS
      alias("associations", DefaultAssociations::class.java)
      alias("regex", RegexAssociation::class.java)
      alias("type", TypeAssociation::class.java)
      useAttributeFor(DefaultAssociations::class.java, "associations")
      useAttributeFor(Association::class.java, "icon")
      useAttributeFor(Association::class.java, "name")
      useAttributeFor(Association::class.java, "priority")
      useAttributeFor(Association::class.java, "iconType")
      useAttributeFor(Association::class.java, "color")
      useAttributeFor(Association::class.java, "folderColor")
      useAttributeFor(Association::class.java, "iconColor")
      useAttributeFor(RegexAssociation::class.java, "pattern")
      useAttributeFor(TypeAssociation::class.java, "type")
    }
    return try {
      xStream.fromXML(associationsXml) as DefaultAssociations
    } catch (e: RuntimeException) {
      e.printStackTrace()
      DefaultAssociations()
    }
  }
}
