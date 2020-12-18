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
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.UISettingsListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.fileTypes.FileTypeEvent;
import com.intellij.openapi.fileTypes.FileTypeListener;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.util.messages.MessageBusConnection;
import com.mallowigi.config.listeners.AssocConfigNotifier;
import com.mallowigi.config.listeners.AtomConfigNotifier;
import com.mallowigi.icons.patchers.AbstractIconPatcher;
import com.mallowigi.icons.services.IconFilterManager;
import com.mallowigi.icons.services.IconPatchersManager;
import com.mallowigi.utils.UiUtilsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class IconReplacerComponent implements DynamicPluginListener, AppLifecycleListener, DumbAware {
  private final MessageBusConnection connect;

  public IconReplacerComponent() {
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
    disposeComponent();
  }

  private void initComponent() {
    if (connect == null) {
      return;
    }
    IconPatchersManager.INSTANCE.init();
    connect.subscribe(UISettingsListener.TOPIC, uiSettings -> IconFilterManager.INSTANCE.applyFilter());

    connect.subscribe(AtomConfigNotifier.TOPIC, IconReplacerComponent::onSettingsChanged);
    connect.subscribe(AssocConfigNotifier.TOPIC, IconReplacerComponent::onSettingsChanged);
    connect.subscribe(FileTypeManager.TOPIC, new FileTypeListener() {
      @Override
      public void fileTypesChanged(@NotNull final FileTypeEvent event) {
        IconPatchersManager.INSTANCE.updateIcons();
      }
    });
    connect.subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
      @Override
      public void projectOpened(@NotNull final Project project) {
        IconPatchersManager.INSTANCE.updateIcons();
      }
    });

    ApplicationManager.getApplication().invokeLater(IconFilterManager.INSTANCE::applyFilter);
  }

  private void disposeComponent() {
    AbstractIconPatcher.clearCache();
    connect.disconnect();
  }

  private static void onSettingsChanged(final PersistentStateComponent config) {
    IconPatchersManager.INSTANCE.updateFileIcons();
    IconPatchersManager.INSTANCE.updateIcons();
    LafManager.getInstance().updateUI();
    UiUtilsKt.refreshOpenedProjects();
  }

}
