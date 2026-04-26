/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2024 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.icons.associations

import com.intellij.util.xmlb.annotations.Property
import com.intellij.util.xmlb.annotations.XCollection
import com.mallowigi.config.AtomSettingsConfig
import com.mallowigi.config.BundledAssociations
import com.mallowigi.models.FileInfo
import com.mallowigi.models.IconType
import com.mallowigi.utils.isPluginEnabled
import org.jetbrains.annotations.NonNls

/** Represents a list of [SelectedAssociations]. */
@Suppress("MemberNameEqualsClassName")
class SelectedAssociations(
  /** The [IconType] of the [SelectedAssociations]. */
  @Property
  val iconType: IconType = IconType.FILE,
  associations: List<Association> = listOf(),
) : Associations() {
  /** List of associations to ignore by type. */
  override val ignoredAssociations: Set<String>
    get() = when (iconType) {
      IconType.FILE -> FILE_IGNORED_ASSOCIATIONS.filter { it.second() }.map { it.first }.toSet()
      else          -> emptySet()
    }

  /** All associations, mutable (from the form). */
  @Transient
  private var mutableAssociations: MutableMap<String, Association> = mutableMapOf()

  @Transient
  private var ownedAssociations: MutableMap<String, Association> = mutableMapOf()

  /** My modified [Associations]. Legacy persisted field for backward compatibility. */
  @Property
  @XCollection
  @Deprecated("Use associationsList instead")
  var ownAssociations: MutableMap<String, RegexAssociation> = mutableMapOf()

  @Property
  @XCollection(
    elementTypes = [
      RegexAssociation::class,
      PsiAssociation::class,
      TypeAssociation::class
    ]
  )
  var associationsList: MutableList<Association> = mutableListOf()

  init {
    // Copy from a list of other [Associations] (used when applying form)
    mutableAssociations = associations.associateBy { it.name }.toMutableMap()
    mutableAssociations.values.forEach { it.iconType = iconType }
  }

  /** Reinitializes the [mutableAssociations]. */
  fun initMutableListFromDefaults() {
    mutableAssociations.putAll(BundledAssociations.instance.getMap(iconType))
  }

  /**
   * Adds a new [RegexAssociation] to the collection of associations. If an association with the same name already exists, the new
   * association's name is appended with "(1)" to ensure uniqueness before adding it to the collection.
   *
   * @param association the [Association] to be added
   */
  fun addAssociation(association: Association) {
    if (hasOwn(association.name)) {
      association.name = "${association.name} (1)"
      // ownAssociations[association.name] = association
      ownedAssociations[association.name] = association
    } else {
      // ownAssociations[association.name] = association
      ownedAssociations[association.name] = association
    }
  }

  /** Checks if an own [Association] is already registered. */
  private fun hasOwn(name: String): Boolean {
    return ownedAssociations.containsKey(name) || ownAssociations.containsKey(name)
  }

  /** Gets the list of own [Associations]. */
  fun ownValues(): List<Association> = ownedAssociations.values.toList()

  /**
   * Find matching [Association] with the highest priority.
   *
   * @param file a file's [FileInfo]
   * @return the association if found
   */
  override fun findMatchingAssociation(file: FileInfo): Association? {
    val inOwn = findInOwn(file)
    val inMutable = findInMutable(file)

    return when {
      inOwn != null && inMutable != null -> maxOf(inOwn, inMutable, compareBy { it.priority })
      else                               -> inOwn ?: inMutable
    }
  }

  /**
   * Look for matching association in [ownedAssociations].
   *
   * @param file a file's [FileInfo]
   * @return matching association if found
   */
  private fun findInOwn(file: FileInfo): Association? = ownValues()
    .filter { it.enabled && it.matches(file) && IconPackManager.instance.hasIconPack(it.iconPack) }
    .maxByOrNull { it.priority }

  /**
   * Look for matching association in [mutableAssociations].
   *
   * @param file a file's [FileInfo]
   * @return matching association if found
   */
  private fun findInMutable(file: FileInfo): Association? = mutableAssociations.values.asSequence()
    .filter { it.enabled && it.matches(file) && IconPackManager.instance.hasIconPack(it.iconPack) && !hasOwn(it.name) }
    .maxByOrNull { it.priority }

  /** Look for matching association in [ownedAssociations]. */
  private fun findInOwnByName(path: String): Association? = ownValues()
    .filter { it.enabled && it.matchesName(path) && IconPackManager.instance.hasIconPack(it.iconPack) }
    .maxByOrNull { it.priority }

  /** Look for matching association in [mutableAssociations]. */
  private fun findInMutableByName(path: String): Association? = mutableAssociations.values.asSequence()
    .filter { it.enabled && it.matchesName(path) && IconPackManager.instance.hasIconPack(it.iconPack) && !hasOwn(it.name) }
    .maxByOrNull { it.priority }

  /** Get the list of all [Associations]. */
  override fun getTheAssociations(): List<Association> {
    // to display associations to the form, need to merge both
    val result = mutableMapOf<String, Association>()
    result.putAll(mutableAssociations)
    result.putAll(ownedAssociations)
    return result.values.toList()
  }

  override fun findAssociationByName(assocName: String): Association? {
    val inOwn = findInOwnByName(assocName)
    val inMutable = findInMutableByName(assocName)

    return when {
      inOwn != null && inMutable != null -> maxOf(inOwn, inMutable, compareBy { it.priority })
      else                               -> inOwn ?: inMutable
    }
  }

  /** Resets default state. */
  fun reset() {
    mutableAssociations.clear()
    ownAssociations.clear()
    ownedAssociations.clear()
    initMutableListFromDefaults()
  }

  /** Extract [ownedAssociations] from [mutableAssociations]. */
  fun registerOwnAssociations() {
    ownedAssociations.putAll(mutableAssociations.filter { it.value.touched })
    associationsList.clear()
    associationsList.addAll(ownedAssociations.values)
  }

  fun updateOwnAssociations() {
    // Migrate legacy ownAssociations map into associationsList if needed
    if (associationsList.isEmpty() && ownAssociations.isNotEmpty()) {
      associationsList.addAll(ownAssociations.values)
    }
    ownedAssociations.clear()
    ownedAssociations.putAll(associationsList.associateBy { it.name })
  }

  companion object {
    @NonNls
    private val FILE_IGNORED_ASSOCIATIONS: Set<Pair<String, () -> Boolean>> = setOf(
      Pair("PHP") { isPluginEnabled("com.jetbrains.php") },
      Pair("Kotlin") { true },
      Pair("Java") { true },
      Pair("Ruby") { AtomSettingsConfig.instance.isUseRubyIcons },
    )
  }
}
