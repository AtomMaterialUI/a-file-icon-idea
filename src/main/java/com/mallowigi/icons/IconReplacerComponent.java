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
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.UISettingsListener;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileTypes.FileTypeEvent;
import com.intellij.openapi.fileTypes.FileTypeListener;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.IconPathPatcher;
import com.intellij.ui.GuiUtils;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.xmlb.annotations.Property;
import com.mallowigi.config.AtomFileIconsConfig;
import com.mallowigi.config.ConfigNotifier;
import com.mallowigi.icons.patchers.CheckStyleIconPatcher;
import com.mallowigi.icons.patchers.IconPathPatchers;
import com.mallowigi.icons.patchers.MTIconPatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

import static com.mallowigi.icons.IconManager.applyFilter;

@SuppressWarnings("InstanceVariableMayNotBeInitialized")
public final class IconReplacerComponent implements DynamicPluginListener, AppLifecycleListener {
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
    disposeComponent();
  }

  @Property
  private final IconPathPatchers iconPathPatchers = IconPatchersFactory.create();

  private final Collection<IconPathPatcher> installedPatchers = new HashSet<>(100);
  private final CheckStyleIconPatcher checkStyleIconPatcher = new CheckStyleIconPatcher();

  private MessageBusConnection connect;

  private void initComponent() {
    updateIcons();
    connect = ApplicationManager.getApplication().getMessageBus().connect();
    connect.subscribe(UISettingsListener.TOPIC, uiSettings -> applyFilter());

    connect.subscribe(ConfigNotifier.CONFIG_TOPIC, this::onSettingsChanged);
    connect.subscribe(FileTypeManager.TOPIC, new FileTypeListener() {
      @Override
      public void fileTypesChanged(@NotNull final FileTypeEvent event) {
        updateIcons();
      }
    });

    ApplicationManager.getApplication().runWriteAction(() -> {
      applyFilter();
      LafManager.getInstance().updateUI();
    });
  }

  @SuppressWarnings({"WeakerAccess",
    "FeatureEnvy"})
  void updateIcons() {
    MTIconPatcher.clearCache();
    removePathPatchers();

    final AtomFileIconsConfig atomFileIconsConfig = AtomFileIconsConfig.getInstance();
    if (atomFileIconsConfig.isEnabledUIIcons()) {
      IconLoader.installPathPatcher(checkStyleIconPatcher);
      installPathPatchers();
    }
    if (atomFileIconsConfig.isEnabledPsiIcons()) {
      installPSIPatchers();
    }
    if (atomFileIconsConfig.isEnabledIcons()) {
      installFileIconsPatchers();
    }
  }

  private void installPathPatchers() {
    for (final IconPathPatcher externalPatcher : iconPathPatchers.getIconPatchers()) {
      installPathPatcher(externalPatcher);
    }
  }

  private void installPSIPatchers() {
    for (final IconPathPatcher externalPatcher : iconPathPatchers.getGlyphPatchers()) {
      installPathPatcher(externalPatcher);
    }
  }

  private void installFileIconsPatchers() {
    for (final IconPathPatcher externalPatcher : iconPathPatchers.getFilePatchers()) {
      installPathPatcher(externalPatcher);
    }
  }

  private void removePathPatchers() {
    for (final IconPathPatcher iconPathPatcher : installedPatchers) {
      removePathPatcher(iconPathPatcher);
    }
    installedPatchers.clear();
  }

  private void installPathPatcher(final IconPathPatcher patcher) {
    installedPatchers.add(patcher);
    IconLoader.installPathPatcher(patcher);
  }

  private static void removePathPatcher(final IconPathPatcher patcher) {
    IconLoader.removePathPatcher(patcher);
  }

  private void disposeComponent() {
    MTIconPatcher.clearCache();
    connect.disconnect();
  }

  private void onSettingsChanged(final AtomFileIconsConfig atomFileIconsConfig) {
    updateFileIcons();
    updateIcons();
  }

  private static void updateFileIcons() {
    GuiUtils.invokeLaterIfNeeded(() -> {
      final Application app = ApplicationManager.getApplication();
      app.runWriteAction(() -> FileTypeManagerEx.getInstanceEx().fireFileTypesChanged());
      app.runWriteAction(ActionToolbarImpl::updateAllToolbarsImmediately);

      applyFilter();
    }, ModalityState.NON_MODAL);
  }

}
