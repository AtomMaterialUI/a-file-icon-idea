/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package com.mallowigi.icons.patchers

import com.intellij.openapi.util.IconPathPatcher
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.tree.arrows.ArrowsStyles
import org.jetbrains.annotations.NonNls
import java.net.URL

/** Base class for [IconPathPatcher]s. */
abstract class AbstractIconPatcher : IconPathPatcher() {

  /** The string to append to the final path. */
  protected abstract val pathToAppend: @NonNls String

  /** The string to remove from the original path. */
  protected abstract val pathToRemove: @NonNls String

  /** Whether the patcher should be enabled or not. */
  var enabled: Boolean = false

  /** Singleton instance. */
  var instance: AtomFileIconsConfig? = AtomFileIconsConfig.instance
    get() {
      if (field == null) field = AtomFileIconsConfig.instance
      return field
    }
    private set

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
    if (instance == null) return null

    val patchedPath = getPatchedPath(path)
    return if (!enabled) null else patchedPath
  }

  /** Check whether a png version of a resource exists. */
  private fun getPNG(path: String): URL? {
    val replacement = SVG.replace(getReplacement(path), ".png") // NON-NLS
    return javaClass.getResource("/$replacement")
  }

  /**
   * Returns the patched path by taking the original path and appending the
   * path to append, and converting to svg
   *
   * @param path
   * @return
   */
  @Suppress("kotlin:S1871", "HardCodedStringLiteral")
  private fun getPatchedPath(path: String): String? = when {
    !enabled -> null
    path.contains("expui/gutter") -> getArrowReplacement(path)
    CACHE.containsKey(path) -> CACHE[path]
    // First try the svg version of the resource
    getSVG(path) != null -> {
      CACHE[path] = getReplacement(path)
      CACHE[path]
    }
    // Then try the png version
    getPNG(path) != null -> {
      CACHE[path] = getReplacement(path)
      CACHE[path]
    }

    else -> null
  }

  private fun getArrowReplacement(path: String): String? {
    val arrowsStyle = instance?.arrowsStyle
    if (arrowsStyle === ArrowsStyles.NONE) return null

    return arrowsStyle?.getIconForPath(path)
  }

  /**
   * Replace the path by using the pathToAppend and pathToRemove
   *
   * @param path
   * @return
   */
  private fun getReplacement(path: String): String {
    val finalPath: String = when {
      path.contains(".gif") -> GIF.replace(path, ".svg")
      else -> path.replace(".png", ".svg")
    }
    return (pathToAppend + finalPath.replace(pathToRemove, "")).replace("//", "/") // don't ask
  }

  /** Check whether a svg version of a resource exists. */
  private fun getSVG(path: String): URL? {
    val svgFile = PNG.replace(getReplacement(path), ".svg") // NON-NLS
    return javaClass.getResource("/$svgFile")
  }

  companion object {
    private val CACHE: MutableMap<String, String> = HashMap(100)
    private val CL_CACHE: MutableMap<String, ClassLoader?> = HashMap(100)
    private val PNG = ".png".toRegex(RegexOption.LITERAL)
    private val SVG = ".svg".toRegex(RegexOption.LITERAL)
    private val GIF = ".gif".toRegex(RegexOption.LITERAL)

    /** Clear all caches. */
    @JvmStatic
    fun clearCache() {
      CACHE.clear()
      CL_CACHE.clear()
    }
  }

}
