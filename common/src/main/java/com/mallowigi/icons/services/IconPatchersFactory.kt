/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.icons.services

import com.intellij.openapi.diagnostic.thisLogger
import com.mallowigi.icons.patchers.*
import com.thoughtworks.xstream.XStream
import org.jetbrains.annotations.NonNls

/** Load patchers from xml. */
object IconPatchersFactory {

  @NonNls
  private const val FILE = "/icon_patchers.xml"

  /**
   * Generate the list of [IconPathPatchers] from XML
   *
   * @return list of [IconPathPatchers]
   */
  fun create(): IconPathPatchers {
    val xml = IconPatchersFactory::class.java.getResource(FILE)
    val xStream = XStream()

    xStream.run {
      allowTypesByWildcard(arrayOf("com.mallowigi.icons.patchers.*")) // NON-NLS
      processAnnotations(IconPathPatchers::class.java)
      processAnnotations(FileIconsPatcher::class.java)
      processAnnotations(UIIconsPatcher::class.java)
      processAnnotations(GlyphIconsPatcher::class.java)
      processAnnotations(ExternalIconsPatcher::class.java)
    }
    return try {
      xStream.fromXML(xml) as IconPathPatchers
    } catch (e: RuntimeException) {
      thisLogger().error(e)
      IconPathPatchers()
    }
  }

}
