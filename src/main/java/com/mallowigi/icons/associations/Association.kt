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

import com.intellij.util.xmlb.annotations.Property
import com.mallowigi.models.FileInfo
import org.jetbrains.annotations.NonNls
import java.io.Serializable

/**
 * Association
 *
 * @property name
 * @property icon
 * @property enabled
 * @constructor Create empty Association
 */
abstract class Association internal constructor(
  @field:Property @field:NonNls var name: String,
  @field:Property var icon: String,
  var enabled: Boolean = true,
) : Serializable {

  abstract var matcher: String

  open val isEmpty: Boolean
    get() = name.isEmpty() || icon.isEmpty()

  /**
   * Check whether the file matches the association
   *
   * @param file
   * @return
   */
  abstract fun matches(file: FileInfo): Boolean

  /**
   * Apply the association
   *
   * @param other
   */
  open fun apply(other: Association) {
    name = other.name
    icon = other.icon
    enabled = other.enabled
  }

  override fun toString(): String = "$name $matcher"

  companion object {
    private const val serialVersionUID: Long = -1L
  }
}
