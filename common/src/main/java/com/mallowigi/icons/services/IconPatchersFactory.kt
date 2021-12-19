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

import com.mallowigi.icons.patchers.ExternalIconsPatcher
import com.mallowigi.icons.patchers.FileIconsPatcher
import com.mallowigi.icons.patchers.GlyphIconsPatcher
import com.mallowigi.icons.patchers.IconPathPatchers
import com.mallowigi.icons.patchers.UIIconsPatcher
import com.thoughtworks.xstream.XStream

/**
 * Load patchers from xml
 *
 * @constructor Create empty Icon patchers factory
 */
object IconPatchersFactory {
  private val ICON_PATCHERS_XML = "/icon_patchers.xml"

  fun create(): IconPathPatchers {
    val xml = IconPatchersFactory::class.java.getResource(ICON_PATCHERS_XML)
    val xStream = XStream()

    xStream.run {
      allowTypesByWildcard(arrayOf("com.mallowigi.icons.patchers.*"))
      alias("iconPathPatchers", IconPathPatchers::class.java)
      alias("iconPatchers", MutableSet::class.java)
      alias("glyphPatchers", MutableSet::class.java)
      alias("filePatchers", MutableSet::class.java)
      alias("filePatcher", FileIconsPatcher::class.java)
      alias("iconPatcher", UIIconsPatcher::class.java)
      alias("glyphPatcher", GlyphIconsPatcher::class.java)
      useAttributeFor(ExternalIconsPatcher::class.java, "append")
      useAttributeFor(ExternalIconsPatcher::class.java, "remove")
      useAttributeFor(ExternalIconsPatcher::class.java, "name")
    }
    return try {
      xStream.fromXML(xml) as IconPathPatchers
    } catch (e: RuntimeException) {
      IconPathPatchers()
    }
  }
}
