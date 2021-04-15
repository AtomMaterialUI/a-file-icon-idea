/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2020 Elior "Mallowigi" Boukhobza
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

package com.mallowigi.icons;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ColorUtil;
import com.intellij.util.SVGLoader;
import com.intellij.util.messages.MessageBusConnection;
import com.mallowigi.config.AtomFileIconsConfig;
import com.mallowigi.config.listeners.AtomConfigNotifier;
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
public final class TintedIconsComponent implements DynamicPluginListener, AppLifecycleListener, DumbAware {
  private static final PluginId PLUGIN_ID = PluginId.getId("com.mallowigi");
  private final MessageBusConnection connect;

  public TintedIconsComponent() {
    connect = ApplicationManager.getApplication().getMessageBus().connect();
  }

  @Override
  public void appStarting(@Nullable final Project projectFromCommandLine) {
    initComponent();
  }

  @Override
  public void appClosing() {
    disposeComponent();
  }

  @Override
  public void pluginLoaded(@NotNull final IdeaPluginDescriptor pluginDescriptor) {
    initComponent();
  }

  @Override
  public void pluginUnloaded(@NotNull final IdeaPluginDescriptor pluginDescriptor, final boolean isUpdate) {
    if(!PLUGIN_ID.equals(pluginDescriptor.getPluginId())) return;

    disposeComponent();
  }

  private void initComponent() {
    SVGLoader.setColorPatcherProvider(new TintedColorPatcher(SvgLoaderHacker.collectOtherPatcher()));

    // Listen for changes on the settings
    connect.subscribe(LafManagerListener.TOPIC, source -> {
      SVGLoader.setColorPatcherProvider(new TintedColorPatcher(SvgLoaderHacker.collectOtherPatcher()));

      TintedColorPatcher.refreshThemeColor(getThemedColor());
      TintedColorPatcher.refreshAccentColor(getTintedColor());
    });
    connect.subscribe(AtomConfigNotifier.TOPIC, atomFileIconsConfig -> {
      SVGLoader.setColorPatcherProvider(new TintedColorPatcher(SvgLoaderHacker.collectOtherPatcher()));

      TintedColorPatcher.refreshThemeColor(getThemedColor());
      TintedColorPatcher.refreshAccentColor(getTintedColor());
    });
  }

  private void disposeComponent() {
    connect.disconnect();
  }

  private static ColorUIResource getThemedColor() {
    return new ColorUIResource(ColorUtil.fromHex(AtomFileIconsConfig.getInstance().getCurrentThemedColor()));
  }

  private static ColorUIResource getTintedColor() {
    return new ColorUIResource(ColorUtil.fromHex(AtomFileIconsConfig.getInstance().getCurrentAccentColor()));
  }

  protected static final class TintedColorPatcher implements SVGLoader.SvgElementColorPatcherProvider {
    @NonNls
    private static ColorUIResource themedColor = getThemedColor();
    private static ColorUIResource tintedColor = getTintedColor();
    private final SVGLoader.SvgElementColorPatcherProvider otherPatcherProvider;

    private TintedColorPatcher(SVGLoader.SvgElementColorPatcherProvider otherPatcherProvider) {
      this.otherPatcherProvider = otherPatcherProvider;
      refreshColors();
    }

    private static void refreshThemeColor(final ColorUIResource theme) {
      themedColor = theme;
    }

    private static void refreshAccentColor(final ColorUIResource tint) {
      tintedColor = tint;
    }

    private static void refreshColors() {
      tintedColor = getTintedColor();
      themedColor = getThemedColor();
    }

    @NotNull
    @Override
    public SVGLoader.SvgElementColorPatcher forPath(@Nullable String path) {
      return createPatcher(otherPatcherProvider.forPath(path));
    }

    @NotNull
    private SVGLoader.SvgElementColorPatcher createPatcher(
        final @Nullable SVGLoader.SvgElementColorPatcher otherPatcher
    ) {
      return new SVGLoader.SvgElementColorPatcher() {
        @Override
        public void patchColors(@NonNls final Element svg) {
          if(otherPatcher != null) {
            otherPatcher.patchColors(svg);
          }
          @NonNls final String tint = svg.getAttribute("tint");
          @NonNls final String themed = svg.getAttribute("themed");
          final String hexColor = getColorHex(themedColor);
          final String tintColor = getColorHex(tintedColor);

          if ("true".equals(tint) || "fill".equals(tint)) {
            svg.setAttribute("fill", "#" + tintColor);
          } else if ("stroke".equals(tint)) {
            svg.setAttribute("stroke", "#" + tintColor);
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
