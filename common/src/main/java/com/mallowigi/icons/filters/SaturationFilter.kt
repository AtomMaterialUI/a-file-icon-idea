package com.mallowigi.icons.filters

import com.intellij.ui.ColorUtil
import com.mallowigi.config.AtomFileIconsConfig
import java.awt.Color
import java.awt.image.RGBImageFilter

object SaturationFilter : RGBImageFilter() {
  @Suppress("Detekt:MagicNumber", "HardCodedStringLiteral")
  override fun filterRGB(x: Int, y: Int, rgba: Int): Int {
    val originalColor = Color(rgba, true)
    val tones = AtomFileIconsConfig.instance.saturation;
    return ColorUtil.toAlpha(ColorUtil.desaturate(originalColor, tones), originalColor.alpha).rgb
  }
}
