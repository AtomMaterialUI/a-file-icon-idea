/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 Elior "Mallowigi" Boukhobza
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
 */
package com.mallowigi.icons.associations

import com.intellij.util.xmlb.annotations.Property
import com.intellij.util.xmlb.annotations.XCollection
import com.mallowigi.config.BundledAssociations
import com.mallowigi.models.FileInfo
import com.mallowigi.models.IconType

/** Represents a list of [SelectedAssociations]. */
@Suppress("MemberNameEqualsClassName")
class SelectedAssociations(
  /** The [IconType] of the [SelectedAssociations]. */
  @Property val iconType: IconType = IconType.FILE,
  associations: List<RegexAssociation> = listOf(),
) : Associations() {
  /** List of associations to ignore by type. */
  override val ignoredAssociations: Set<String>
    get() = when (iconType) {
      IconType.FILE -> FILE_IGNORED_ASSOCIATIONS
      IconType.FOLDER -> FOLDER_IGNORED_ASSOCIATIONS
      else -> emptySet()
    }

  /** All associations, mutable (from the form) */
  @Transient
  private var mutableAssociations: MutableMap<String, RegexAssociation> = mutableMapOf()

  /** My modified [Associations]. */
  @Property
  @XCollection
  private var ownAssociations: MutableMap<String, RegexAssociation> = mutableMapOf()

  init {
    // Copy from a list of other [Associations] (used when applying form)
    mutableAssociations = associations.associateBy { it.name }.toMutableMap()
  }

  /** Reinitializes the [mutableAssociations]. */
  fun initMutableListFromDefaults() {
    mutableAssociations.putAll(BundledAssociations.instance.getMap(iconType))
  }

  /**
   * Checks if an own [Association] is already registered
   *
   * @param name
   */
  private fun hasOwn(name: String): Boolean = ownAssociations.containsKey(name)

  /**
   * Gets the list of own [Associations]
   *
   * @return
   */
  fun ownValues(): List<RegexAssociation> = ownAssociations.values.toList()

  /**
   * Find matching [Association] with the highest priority
   *
   * @param file a file's [FileInfo]
   * @return the association if found
   */
  override fun findMatchingAssociation(file: FileInfo): Association? {
    val inOwn = findInOwn(file)
    val inMutable = findInMutable(file)

    return when {
      inOwn != null && inMutable != null -> maxOf(inOwn, inMutable, compareBy { it.priority })
      else -> inOwn ?: inMutable
    }
  }

  /**
   * Look for matching association in [ownAssociations]
   *
   * @param file a file's [FileInfo]
   * @return matching association if found
   */
  private fun findInOwn(file: FileInfo): Association? = ownValues()
    .filter { it.enabled && it.matches(file) }
    .maxByOrNull { it.priority }

  /**
   * Look for matching association in [mutableAssociations]
   *
   * @param file a file's [FileInfo]
   * @return matching association if found
   */
  private fun findInMutable(file: FileInfo): Association? = mutableAssociations.values.toList()
    .filter { it.enabled && it.matches(file) && !hasOwn(it.name) }
    .maxByOrNull { it.priority }

  /** Look for matching association in [ownAssociations]. */
  private fun findInOwnByName(path: String): Association? = ownValues()
    .filter { it.enabled && it.matchesName(path) }
    .maxByOrNull { it.priority }

  /** Look for matching association in [mutableAssociations]. */
  private fun findInMutableByName(path: String): Association? = mutableAssociations.values.toList()
    .filter { it.enabled && it.matchesName(path) && !hasOwn(it.name) }
    .maxByOrNull { it.priority }

  /**
   * Get the list of all [Associations]
   *
   * @return the list of [Associations]
   */
  override fun getTheAssociations(): List<RegexAssociation> {
    // to display associations to the form, need to merge both
    val result = mutableMapOf<String, RegexAssociation>()
    result.putAll(mutableAssociations)
    result.putAll(ownAssociations)
    return result.values.toList()
  }

  override fun findAssociationByName(assocName: String): Association? {
    val inOwn = findInOwnByName(assocName)
    val inMutable = findInMutableByName(assocName)

    return when {
      inOwn != null && inMutable != null -> maxOf(inOwn, inMutable, compareBy { it.priority })
      else -> inOwn ?: inMutable
    }
  }

  /** Resets default state. */
  fun reset() {
    mutableAssociations.clear()
    ownAssociations.clear()
    initMutableListFromDefaults()
  }

  /** Extract [ownAssociations] from [mutableAssociations]. */
  fun registerOwnAssociations() {
    ownAssociations.putAll(mutableAssociations.filter { it.value.touched })
  }

  companion object {
    private val FILE_IGNORED_ASSOCIATIONS: Set<String> = setOf("PHP", "Kotlin", "Java", "Ruby")
    private val FOLDER_IGNORED_ASSOCIATIONS: Set<String> = emptySet()
  }
}
