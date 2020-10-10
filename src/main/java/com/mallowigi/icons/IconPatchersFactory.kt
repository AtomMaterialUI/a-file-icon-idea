/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2020 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.icons

import com.mallowigi.icons.IconPatchersFactory
import com.mallowigi.icons.patchers.*
import com.thoughtworks.xstream.XStream
import org.jetbrains.annotations.NonNls

enum class IconPatchersFactory {
  PATCH_PATCH;

  companion object {
    @NonNls
    private val ICON_PATCHERS_XML = "/icon_patchers.xml"
    
    fun create(): IconPathPatchers {
      val xml = IconPatchersFactory::class.java.getResource(ICON_PATCHERS_XML)
      @NonNls val xStream = XStream()
      XStream.setupDefaultSecurity(xStream)
      xStream.allowTypesByWildcard(arrayOf("com.mallowigi.icons.patchers.*"))
      xStream.alias("iconPathPatchers", IconPathPatchers::class.java)
      xStream.alias("iconPatchers", MutableSet::class.java)
      xStream.alias("glyphPatchers", MutableSet::class.java)
      xStream.alias("filePatchers", MutableSet::class.java)
      xStream.alias("filePatcher", FileIconsPatcher::class.java)
      xStream.alias("iconPatcher", UIIconsPatcher::class.java)
      xStream.alias("glyphPatcher", GlyphIconsPatcher::class.java)
      xStream.useAttributeFor(ExternalIconsPatcher::class.java, "append")
      xStream.useAttributeFor(ExternalIconsPatcher::class.java, "remove")
      xStream.useAttributeFor(ExternalIconsPatcher::class.java, "name")
      return try {
        xStream.fromXML(xml) as IconPathPatchers
      } catch (e: RuntimeException) {
        IconPathPatchers()
      }
    }
  }
}