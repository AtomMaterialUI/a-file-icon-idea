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
@file:Suppress("HardCodedStringLiteral")

package com.mallowigi.icons.associations

import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.xmlb.annotations.Property
import com.mallowigi.models.FileInfo
import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/** A Regex [Association]. */
@XStreamAlias("regex")
class RegexAssociation internal constructor() : Association() {
  /** The regex pattern. */
  @field:Property
  @XStreamAsAttribute
  var pattern: String = ""

  /** Compiled pattern of the regex pattern. */
  @Transient
  private var compiledPattern: Pattern? = null

  /** Matches by the [pattern]. */
  override var matcher: String
    get() = pattern
    set(matcher) {
      pattern = matcher
    }

  /** Identifies [RegexAssociation] that are empty. */
  override val isEmpty: Boolean
    get() = super.isEmpty || pattern.isEmpty()

  /**
   * Matches against the pattern
   *
   * @param file the file info
   * @return true if the pattern matches
   */
  override fun matches(file: FileInfo): Boolean {
    return try {
      if (compiledPattern == null) compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
      var target = file.name
      if (pattern.contains("/")) {
        target = file.path
      }
      compiledPattern!!.matcher(target).matches()
    } catch (e: PatternSyntaxException) {
      LOG.warn(e)
      false
    }
  }

  /**
   * Apply changes from another [RegexAssociation]
   *
   * @param other the other to apply from
   */
  override fun apply(other: Association) {
    super.apply(other)
    pattern = other.matcher
  }

  override fun toString(): String =
    "RegexAssociation(enabled=$enabled, priority=$priority, iconType=$iconType, name='$name', icon='$icon', pattern='$pattern', iconColor='$iconColor', folderColor='$folderColor', folderIconColor='$folderIconColor')"

  @Suppress("detekt:UnnecessaryParentheses")
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as RegexAssociation

    if (enabled != other.enabled) return false
    if (priority != other.priority) return false
    if (iconType != other.iconType) return false
    if (name != other.name) return false
    if (icon != other.icon) return false
    if (pattern != other.pattern) return false
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
    result = 31 * result + pattern.hashCode()
    result = 31 * result + (iconColor ?: DEFAULT_COLOR).hashCode()
    result = 31 * result + (folderColor ?: DEFAULT_COLOR).hashCode()
    result = 31 * result + (folderIconColor ?: DEFAULT_COLOR).hashCode()
    return result
  }

  override fun isValid(): Boolean {
    if (pattern.isBlank()) return false

    if (!super.isValid()) return false

    return try {
      Pattern.compile(pattern)
      true
    } catch (e: PatternSyntaxException) {
      LOG.warn(e)
      false
    }
  }

  companion object {
    private val LOG = Logger.getInstance(RegexAssociation::class.java)
  }

}
