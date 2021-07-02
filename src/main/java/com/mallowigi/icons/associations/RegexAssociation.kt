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
package com.mallowigi.icons.associations

import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.xmlb.annotations.Property
import com.mallowigi.models.FileInfo
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Regex association
 *
 * @property pattern
 * @constructor
 *
 * @param name
 * @param icon
 * @param enabled
 */
class RegexAssociation @JvmOverloads constructor(
  name: String = "",
  icon: String = "",
  enabled: Boolean = true,
  @field:Property var pattern: String = ""
) : Association(name, icon, enabled) {

  override var matcher: String
    get() = pattern
    set(matcher) {
      pattern = matcher
    }

  override val isEmpty: Boolean
    get() = super.isEmpty || pattern.isEmpty()

  @Transient
  private var compiledPattern: Pattern? = null

  override fun matches(file: FileInfo): Boolean {
    return try {
      if (compiledPattern == null) compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
      compiledPattern!!.matcher(file.name).matches()
    } catch (e: PatternSyntaxException) {
      LOG.warn(e)
      false
    }
  }


  override fun apply(other: Association) {
    super.apply(other)
    pattern = other.matcher
  }


  companion object {
    private val LOG = Logger.getInstance(RegexAssociation::class.java)
  }

}
