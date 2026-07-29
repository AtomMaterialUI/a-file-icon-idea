/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Elior "Mallowigi" Boukhobza
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
 */
package com.mallowigi.icons.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.mallowigi.config.select.AtomProjectSelectConfig
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.Associations
import com.mallowigi.models.FileInfo
import com.mallowigi.models.IconType

@Service(Service.Level.APP)
class AssociationResolver {
  private val cache = object : LinkedHashMap<CacheKey, CacheEntry>(MAX_ENTRIES, LOAD_FACTOR, true) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<CacheKey, CacheEntry>?): Boolean = size > MAX_ENTRIES
  }
  private var generation = 0L

  fun findAssociation(
    project: Project,
    iconType: IconType,
    file: FileInfo,
    globalAssociations: Associations,
  ): Association? {
    if (project.isDisposed) return globalAssociations.findAssociation(file)

    // First try to check if the association is already cached
    val key = CacheKey(project.locationHash, iconType, file.path)
    val cacheGeneration = synchronized(cache) {
      cache[key]?.let { return it.association }
      generation
    }

    // Resolve the association and cache it for future use
    val association = resolve(
      project = project,
      iconType = iconType,
      file = file,
      globalAssociations = globalAssociations
    )

    synchronized(cache) {
      if (generation == cacheGeneration) {
        cache[key] = CacheEntry(association)
      }
    }

    return association
  }

  fun invalidate() {
    synchronized(cache) {
      generation++
      cache.clear()
    }
  }

  /**
   * Resolves the association for a given file within a project, considering both project-specific and global associations.
   */
  private fun resolve(
    project: Project,
    iconType: IconType,
    file: FileInfo,
    globalAssociations: Associations,
  ): Association? {
    val projectAssociations = AtomProjectSelectConfig.getInstance(project).getAssociations(iconType)
    return projectAssociations.findAssociation(file) ?: globalAssociations.findAssociation(file)
  }

  /**
   * Represents a unique key used for caching associations by combining project-specific, file-specific,
   * and icon-specific attributes. This key is utilized within the caching mechanism of the
   * [AssociationResolver] to improve performance by avoiding redundant computations.
   *
   * @property projectLocationHash A hash representing the location of the project. This is used to
   * differentiate between files in different projects.
   * @property iconType The type of the icon (e.g., file, folder). This allows the cache to store
   * associations based on icon-specific distinctions.
   * @property path The file path for which the association is computed. It uniquely identifies a
   * resource within the project.
   */
  private data class CacheKey(
    val projectLocationHash: String,
    val iconType: IconType,
    val path: String,
  )

  /**
   * Represents a single cache entry used by the AssociationResolver for storing resolved associations.
   *
   * @property association The association object that is resolved and cached.
   *                        It may be null if no valid association is found for a specific query.
   */
  private data class CacheEntry(val association: Association?)

  companion object {
    private const val MAX_ENTRIES = 4_096
    private const val LOAD_FACTOR = 0.75f

    val instance: AssociationResolver by lazy { service() }
  }
}
