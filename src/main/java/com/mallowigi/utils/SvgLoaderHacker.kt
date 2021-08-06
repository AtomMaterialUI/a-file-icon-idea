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

package com.mallowigi.utils

import com.intellij.util.SVGLoader
import com.mallowigi.icons.TintedColorPatcher
import java.net.URL
import java.util.Optional

typealias PatcherProvider = SVGLoader.SvgElementColorPatcherProvider

/**
 * Svg loader hacker
 *
 * @constructor Create empty Svg loader hacker
 */
object SvgLoaderHacker {

  private lateinit var collectedPatcherProvider: PatcherProvider

  private val noOpPatcherProvider =
    object : PatcherProvider {
      override fun forURL(url: URL?): SVGLoader.SvgElementColorPatcher? = null
    }

  @JvmStatic
  fun collectOtherPatcher(): PatcherProvider =
    extractPatcher()
      .filter { it is PatcherProvider }
      .filter { it !is TintedColorPatcher }
      .map {
        val otherPatcher = it as PatcherProvider
        collectedPatcherProvider = otherPatcher
        otherPatcher
      }
      .orElseGet { useFallBackPatcher() }

  private fun extractPatcher() = Optional.ofNullable(
    SVGLoader::class.java.declaredFields.firstOrNull { it.name == "ourColorPatcher" }
  )
    .map { ourColorPatcherField ->
      ourColorPatcherField.isAccessible = true
      ourColorPatcherField.get(null)
    }


  private fun useFallBackPatcher(): PatcherProvider =
    if (this::collectedPatcherProvider.isInitialized) collectedPatcherProvider else noOpPatcherProvider
}
