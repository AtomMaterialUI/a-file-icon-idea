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

package com.mallowigi.icons;

import com.intellij.ui.ColorUtil;
import com.intellij.util.SVGLoader;
import com.mallowigi.config.AtomFileIconsConfig;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.plaf.ColorUIResource;

public final class TintedColorPatcher implements SVGLoader.SvgElementColorPatcherProvider {
  public static final String STROKE = "stroke";
  public static final String FILL = "fill";
  public static final String WIDTH = "width";
  public static final String HEIGHT = "height";
  public static final String TRUE = "true";
  public static final String BIG = "big";
  public static final String TINT = "tint";
  public static final String THEMED = "themed";
  @NonNls
  private static ColorUIResource themedColor = getThemedColor();
  private static ColorUIResource tintedColor = getTintedColor();
  private final SVGLoader.SvgElementColorPatcherProvider otherPatcherProvider;

  TintedColorPatcher(final SVGLoader.SvgElementColorPatcherProvider otherPatcherProvider) {
    this.otherPatcherProvider = otherPatcherProvider;
    refreshColors();
  }

  static void refreshThemeColor() {
    themedColor = getThemedColor();
  }

  static void refreshAccentColor() {
    tintedColor = getTintedColor();
  }

  private static ColorUIResource getThemedColor() {
    return new ColorUIResource(ColorUtil.fromHex(AtomFileIconsConfig.getInstance().getCurrentThemedColor()));
  }

  private static ColorUIResource getTintedColor() {
    return new ColorUIResource(ColorUtil.fromHex(AtomFileIconsConfig.getInstance().getCurrentAccentColor()));
  }

  private static void refreshColors() {
    refreshThemeColor();
    refreshAccentColor();
  }

  @NotNull
  private static SVGLoader.SvgElementColorPatcher createPatcher(
    final @Nullable SVGLoader.SvgElementColorPatcher otherPatcher
  ) {
    return new SVGLoader.SvgElementColorPatcher() {
      @Override
      public void patchColors(@NonNls final @NotNull Element svg) {
        if (otherPatcher != null) {
          otherPatcher.patchColors(svg);
        }
        patchTints(svg);
        patchSizes(svg);

        final NodeList nodes = svg.getChildNodes();
        final int length = nodes.getLength();
        for (int i = 0; i < length; i++) {
          final Node item = nodes.item(i);
          if (item instanceof Element) {
            patchColors((Element) item);
          }
        }
      }

      private boolean hasBigIcons() {
        return AtomFileIconsConfig.getInstance().getHasBigIcons();
      }

      @Override
      public byte @Nullable [] digest() {
        return null;
      }

      private void patchTints(final @NonNls Element svg) {
        @NonNls final String tint = svg.getAttribute(TINT);
        @NonNls final String themed = svg.getAttribute(THEMED);
        final String hexColor = ColorUtil.toHex(themedColor);
        final String tintColor = ColorUtil.toHex(tintedColor);

        if (TRUE.equals(tint) || FILL.equals(tint)) {
          svg.setAttribute(FILL, "#" + tintColor);
        } else if (STROKE.equals(tint)) {
          svg.setAttribute(STROKE, "#" + tintColor);
        } else if (TRUE.equals(themed) || FILL.equals(themed)) {
          svg.setAttribute(FILL, "#" + hexColor);
        } else if (STROKE.equals(themed)) {
          svg.setAttribute(STROKE, "#" + hexColor);
        }
      }

      private void patchSizes(final @NonNls Element svg) {
        @NonNls final String isBig = svg.getAttribute(BIG);
        final String size = hasBigIcons() ? "20" : "16";

        if (TRUE.equals(isBig)) {
          svg.setAttribute(WIDTH, size);
          svg.setAttribute(HEIGHT, size);
        }
      }
    };
  }

  @NotNull
  @Override
  public SVGLoader.SvgElementColorPatcher forPath(@Nullable final String path) {
    return createPatcher(otherPatcherProvider.forPath(path));
  }

}
