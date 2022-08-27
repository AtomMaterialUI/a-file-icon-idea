/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.PluginId
import com.mallowigi.config.AtomSettingsBundle.message
import com.mallowigi.models.FileInfo
import org.jetbrains.annotations.NonNls
import java.io.Serializable

/** Represents a serialized collection of [Associations][Association]. */
abstract class Associations : Serializable {
  /** Ignored Associations. */
  open val ignoredAssociations: Set<String> = emptySet()

  /**
   * Find the [Association] which matches the file info
   *
   * @param fileInfo the file information
   * @return the [Association] if found
   */
  fun findAssociation(fileInfo: FileInfo): Association? {
    // First check in custom assocs
    val matching: Association = findMatchingAssociation(fileInfo) ?: return null

    when {
      // Specific plugin handling
      IMAGE_TYPES.contains(matching.name)         -> try {
        // If those plugins are installed, let them handle the icon
        val imageIconViewerID = message("plugins.imageIconViewer")
        val plugin = PluginManagerCore.getPlugin(PluginId.getId(imageIconViewerID))

        if (plugin != null) return null
      } catch (e: RuntimeException) {
        LOG.error(e)
      }
      // Other ignores
      ignoredAssociations.contains(matching.name) -> return null
    }
    return matching
  }

  /**
   * Look for a matching [Association]
   *
   * @param file file information
   * @return [Association] if found
   */
  abstract fun findMatchingAssociation(file: FileInfo): Association?

  /**
   * Get the parsed [associations][Association]
   *
   * @return list of [associations][Association]
   */
  abstract fun getTheAssociations(): List<Association>

  /**
   * Find association by name
   *
   * @param assocName
   */
  abstract fun findAssociationByName(assocName: String): Association?

  companion object {
    private const val serialVersionUID: Long = -1L
    private val LOG = Logger.getInstance(Associations::class.java)

    @NonNls
    private val IMAGE_TYPES: Set<String> = setOf(
      "Images",
      "SVG",
      "SVG Dark",
      "Images (PNG)",
      "Images (JPG)",
      "Images (BMP)",
      "Images (GIF)",
      "Icons"
    )

  }

}
