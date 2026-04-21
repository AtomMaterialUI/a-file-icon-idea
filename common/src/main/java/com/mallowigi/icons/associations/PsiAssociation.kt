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
 *
 */

package com.mallowigi.icons.associations

import com.intellij.util.xmlb.annotations.Property
import com.mallowigi.models.FileInfo
import com.mallowigi.models.IconType
import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** A PSI [Association]. */
@XStreamAlias("psi")
class PsiAssociation internal constructor() : Association() {

  /** The path to the PSI icon. */
  @field:Property
  @XStreamAsAttribute
  var path: String = ""

  init {
    iconType = IconType.PSI
  }

  /** The matcher is the path. */
  override var matcher: String
    get() = path
    set(matcher) {
      path = matcher
    }

  /** Identifies [PsiAssociation] that are empty. */
  override val isEmpty: Boolean
    get() = super.isEmpty || path.isEmpty()

  /** Matches by the [path]. */
  override fun matches(file: FileInfo): Boolean = file.path.endsWith(path)

  /**
   * Apply changes from another [PsiAssociation].
   *
   * @param other the other assoc to apply from
   */
  override fun apply(other: Association) {
    super.apply(other)
    path = other.matcher
  }

  override fun toString(): String =
    "PsiAssociation(enabled=$enabled, priority=$priority, iconType=$iconType, name='$name', icon='$icon', path='$path')"

  @Suppress("detekt:UnnecessaryParentheses")
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as PsiAssociation

    if (enabled != other.enabled) return false
    if (priority != other.priority) return false
    if (iconType != other.iconType) return false
    if (name != other.name) return false
    if (icon != other.icon) return false
    if (path != other.path) return false
    if ((iconColor ?: DEFAULT_COLOR) != (other.iconColor ?: DEFAULT_COLOR)) return false
    if ((folderColor ?: DEFAULT_COLOR) != (other.folderColor ?: DEFAULT_COLOR)) return false
    if ((folderIconColor ?: DEFAULT_COLOR) != (other.folderIconColor ?: DEFAULT_COLOR)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = enabled.hashCode()
    result = 31 * result + priority
    result = 31 * result + iconType.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + icon.hashCode()
    result = 31 * result + path.hashCode()
    result = 31 * result + (iconColor ?: DEFAULT_COLOR).hashCode()
    result = 31 * result + (folderColor ?: DEFAULT_COLOR).hashCode()
    result = 31 * result + (folderIconColor ?: DEFAULT_COLOR).hashCode()
    return result
  }

  override fun isValid(): Boolean {
    if (path.isBlank()) return false
    return super.isValid()
  }

}
