package com.mallowigi.icons

import com.intellij.util.SVGLoader
import java.net.URL
import java.util.Optional

typealias PatcherProvider = SVGLoader.SvgElementColorPatcherProvider

object SvgLoaderHacker {

  private lateinit var collectedPatcherProvider: PatcherProvider

  @JvmStatic
  fun collectOtherPatcher(): PatcherProvider =
    extractPatcher()
      .filter { it is PatcherProvider }
      .filter { it !is TintedIconsComponent.TintedColorPatcher }
      .map {
        val otherPatcher = it as PatcherProvider
        collectedPatcherProvider = otherPatcher
        otherPatcher
      }
      .orElseGet { useFallBackPatcher() }

  private fun extractPatcher() = Optional.ofNullable(
    SVGLoader::class.java.declaredFields
      .firstOrNull { it.name == "ourColorPatcher" }
  )
    .map { ourColorPatcherField ->
      ourColorPatcherField.isAccessible = true
      ourColorPatcherField.get(null)
    }


  private val noOpPatcherProvider =
    object : PatcherProvider {
      override fun forURL(url: URL?): SVGLoader.SvgElementColorPatcher? = null
    }

  private fun useFallBackPatcher(): PatcherProvider =
    if (this::collectedPatcherProvider.isInitialized) collectedPatcherProvider
    else noOpPatcherProvider
}
