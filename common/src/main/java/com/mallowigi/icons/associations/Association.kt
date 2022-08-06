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

import com.intellij.serialization.PropertyMapping
import com.intellij.util.xmlb.annotations.Property
import com.mallowigi.models.FileInfo
import com.mallowigi.models.IconType
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import java.io.Serializable

/**
 * Represents an Association
 *
 * @property enabled whether the association is used
 * @property touched whether the association is touched by the user
 * @property iconType the [IconType] of icon (file/folder)
 * @property name the name of the association
 * @property icon the icon path
 * @property priority association priority. Lowest priorities are used last.
 * @property matcher How the association will be matched against (regex, type)
 * @property isEmpty whether the association has empty fields
 * @property iconColor the color of the icon
 * @property folderIconColor the color of the folder icon
 * @property folderColor the color of the folder
 */
abstract class Association @PropertyMapping() internal constructor() : Serializable, Comparable<Association> {
  @field:Property
  var enabled: Boolean = true

  @field:Property
  @XStreamAsAttribute
  var touched: Boolean = false

  @field:Property
  @XStreamAsAttribute
  var iconType: IconType = IconType.FILE

  @field:Property
  @XStreamAsAttribute
  var name: String = ""

  @field:Property
  @XStreamAsAttribute
  var icon: String = ""

  @field:Property
  @XStreamAsAttribute
  var priority: Int = 100

  @field:Property
  @XStreamAsAttribute
  var iconColor: String? = "808080"

  @field:Property
  @XStreamAsAttribute
  var folderColor: String? = "808080"

  @field:Property
  @XStreamAsAttribute
  var folderIconColor: String? = "808080"

  abstract var matcher: String

  open val isEmpty: Boolean
    get() = name.isEmpty() || icon.isEmpty()

  /**
   * Check whether the file matches the association
   *
   * @param file file information
   * @return true if matches
   */
  abstract fun matches(file: FileInfo): Boolean

  /**
   * Apply changes to the association
   *
   * @param other the other assoc to apply from
   */
  open fun apply(other: Association) {
    iconType = other.iconType
    name = other.name
    icon = other.icon
    enabled = other.enabled
    priority = other.priority
    touched = other.touched
    iconColor = other.iconColor
    folderColor = other.folderColor
    folderIconColor = other.folderIconColor
  }

  override fun toString(): String = "$name: $matcher ($priority)"

  companion object {
    private const val serialVersionUID: Long = -1L
  }

  override fun compareTo(other: Association): Int {
    return Comparator
      .comparingInt(Association::priority)
      .compare(this, other)
  }

  /** Check if matches icon name. */
  fun matchesName(assocName: String): Boolean = name == assocName
}
