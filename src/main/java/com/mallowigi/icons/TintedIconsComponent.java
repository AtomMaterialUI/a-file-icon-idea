/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Chris Magnussen and Elior Boukhobza
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

import com.intellij.ide.AppLifecycleListener;
import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ColorUtil;
import com.intellij.util.SVGLoader;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.StartupUiUtil;
import com.mallowigi.config.ConfigNotifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.net.URL;

/**
 * Apply a tint to the icons. This is used either for accented icons and themed icons.
 */
@SuppressWarnings("InstanceVariableMayNotBeInitialized")
public final class TintedIconsComponent implements DynamicPluginListener {
  private static final ColorUIResource LIGHT_COLOR = new ColorUIResource(0x546E7A);
  private static final ColorUIResource DARK_COLOR = new ColorUIResource(0xB0BEC5);
  private TintedColorPatcher colorPatcher;
  private MessageBusConnection connect;

  @Override
  public void pluginLoaded(@NotNull final IdeaPluginDescriptor pluginDescriptor) {
    initComponent();
  }

  @Override
  public void pluginUnloaded(@NotNull final IdeaPluginDescriptor pluginDescriptor, final boolean isUpdate) {
    disposeComponent();
  }

  private void initComponent() {
    colorPatcher = new TintedColorPatcher();
    SVGLoader.setColorPatcherProvider(colorPatcher);

    // Listen for changes on the settings
    connect = ApplicationManager.getApplication().getMessageBus().connect();
    connect.subscribe(LafManagerListener.TOPIC, source -> {
      SVGLoader.setColorPatcherProvider(null);
      SVGLoader.setColorPatcherProvider(colorPatcher);

      TintedColorPatcher.refreshThemeColor(getTintedColor());
    });
    connect.subscribe(ConfigNotifier.CONFIG_TOPIC, atomFileIconsConfig -> {
      SVGLoader.setColorPatcherProvider(null);
      SVGLoader.setColorPatcherProvider(colorPatcher);

      TintedColorPatcher.refreshThemeColor(getTintedColor());
    });
  }

  private void disposeComponent() {
    connect.disconnect();
  }

  private static ColorUIResource getTintedColor() {
    return StartupUiUtil.isUnderDarcula() ? DARK_COLOR : LIGHT_COLOR;
  }

  @SuppressWarnings({"OverlyComplexAnonymousInnerClass",
    "IfStatementWithTooManyBranches"})
  private static final class TintedColorPatcher implements SVGLoader.SvgElementColorPatcherProvider {
    @NonNls
    private static ColorUIResource themedColor = getTintedColor();

    private TintedColorPatcher() {
      refreshColors();
    }

    private static void refreshThemeColor(final ColorUIResource theme) {
      themedColor = theme;
    }

    private static void refreshColors() {
      themedColor = getTintedColor();
    }

    @NotNull
    @Override
    public SVGLoader.SvgElementColorPatcher forURL(@Nullable final URL url) {
      return new SVGLoader.SvgElementColorPatcher() {
        @Override
        public void patchColors(@NonNls final Element svg) {
          @NonNls final String tint = svg.getAttribute("tint");
          @NonNls final String themed = svg.getAttribute("themed");
          final String hexColor = getColorHex(themedColor);

          if ("true".equals(tint) || "fill".equals(tint)) {
            svg.setAttribute("fill", "#" + hexColor);
          } else if ("stroke".equals(tint)) {
            svg.setAttribute("stroke", "#" + hexColor);
          } else if ("true".equals(themed) || "fill".equals(themed)) {
            svg.setAttribute("fill", "#" + hexColor);
          } else if ("stroke".equals(themed)) {
            svg.setAttribute("stroke", "#" + hexColor);
          }

          final NodeList nodes = svg.getChildNodes();
          final int length = nodes.getLength();
          for (int i = 0; i < length; i++) {
            final Node item = nodes.item(i);
            if (item instanceof Element) {
              patchColors((Element) item);
            }
          }
        }

        @Nullable
        @Override
        public byte[] digest() {
          return null;
        }
      };
    }

    private static String getColorHex(final Color color) {
      return ColorUtil.toHex(color);
    }

  }
}
