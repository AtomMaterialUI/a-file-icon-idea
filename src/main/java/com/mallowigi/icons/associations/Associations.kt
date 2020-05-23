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
package com.mallowigi.icons.associations

import com.google.common.collect.ImmutableSet
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.xmlb.annotations.Property
import com.intellij.util.xmlb.annotations.XCollection
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.models.FileInfo
import org.jetbrains.annotations.NonNls
import java.io.Serializable
import java.util.List.copyOf

class Associations : Serializable {
  @Property
  @XCollection
  private val customAssociations: List<RegexAssociation>?

  private var associations: List<Association>

  constructor() {
    associations = emptyList()
    customAssociations = emptyList()
  }

  constructor(associations: List<RegexAssociation>) {
    this.associations = emptyList()
    customAssociations = copyOf(associations)
  }

  fun getAssociations(): List<Association> {
    return associations
  }

  fun setAssociations(associations: List<Association>) {
    this.associations = copyOf(associations)
  }

  fun findAssociation(file: FileInfo?): Association? {
    val matching: Association?
    // First check in custom assocs
    matching = if (customAssociations != null) {
      customAssociations.stream()
        .filter { association: RegexAssociation -> association.matches(file!!) }
        .findAny()
        .orElse(null)
    }
    else {
      associations.stream()
        .filter { association: Association -> association.matches(file!!) }
        .findAny()
        .orElse(null)
    }
    // Specific plugin handling
    if (matching != null && IMAGE_TYPES.contains(matching.name)) {
      try {
        // If those plugins are installed, let them handle the icon
        val pluginID = message("plugins.iconViewer")
        val imageIconViewerID = message("plugins.imageIconViewer")
        val plugin = PluginManagerCore.getPlugin(PluginId.getId(pluginID))
        val plugin2 = PluginManagerCore.getPlugin(PluginId.getId(imageIconViewerID))

        if (plugin != null || plugin2 != null) return null
      }
      catch (e: RuntimeException) {
        LOG.error(e)
      }
    }
    return matching
  }

  fun getCustomAssociations(): List<RegexAssociation>? {
    return customAssociations
  }

  companion object {
    private val LOG = Logger.getInstance(Associations::class.java)

    @NonNls
    private val IMAGE_TYPES: Set<String> = ImmutableSet.of("Images", "SVG")
  }
}