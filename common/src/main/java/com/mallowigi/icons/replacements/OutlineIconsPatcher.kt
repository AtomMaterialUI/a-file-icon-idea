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
package com.mallowigi.icons.replacements

import com.intellij.openapi.util.IconPathPatcher
import com.mallowigi.config.AtomSettingsConfig
import com.mallowigi.icons.services.IconReplacementsFactory

class OutlineIconsPatcher : IconPathPatcher() {
  private val outlineIcons: IconReplacements = IconReplacementsFactory.create("/outline_icons.xml")

  /** Whether the patcher should be enabled or not. */
  var enabled: Boolean = AtomSettingsConfig.instance.isNewIconsEnabeld

  /**
   * Get the plugin context class loader if an icon needs to be patched
   *
   * @param path the icon path
   * @param originalClassLoader the original class loader
   * @return the plugin class loader if the icon needs to be patched, or the
   *     original class loader
   */
  override fun getContextClassLoader(path: String, originalClassLoader: ClassLoader?): ClassLoader? {
    val classLoader = javaClass.classLoader
    if (!CL_CACHE.containsKey(path)) CL_CACHE[path] = originalClassLoader

    val cachedClassLoader = CL_CACHE[path]
    return if (enabled) classLoader else cachedClassLoader
  }

  /**
   * Patch the icon path if there is an icon available
   *
   * @param path the path to patch
   * @param classLoader the classloader of the icon
   * @return the patched path to the plugin icon, or the original path if the
   *     icon patcher is disabled
   */
  override fun patchPath(path: String, classLoader: ClassLoader?): String? {
    if (CACHE.containsKey(path)) return CACHE[path]

    val replacement = outlineIcons.getReplacement(path)
    if (replacement == null || !enabled) return null

    if (javaClass.getResource(replacement) == null) return null

    CACHE[path] = replacement
    return replacement
  }

  companion object {
    private val CACHE: MutableMap<String, String?> = HashMap(100)
    private val CL_CACHE: MutableMap<String, ClassLoader?> = HashMap(100)
  }
}
