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

package com.mallowigi.tree;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.util.messages.MessageBusConnection;
import com.mallowigi.config.AtomFileIconsConfig;
import com.mallowigi.config.listeners.AtomConfigNotifier;
import com.mallowigi.tree.arrows.ArrowsStyles;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ArrowIconsComponent implements DynamicPluginListener, AppLifecycleListener {
  private final MessageBusConnection connect;

  public ArrowIconsComponent() {
    connect = ApplicationManager.getApplication().getMessageBus().connect();
  }

  private static void replaceTree() {
    final UIDefaults defaults = UIManager.getLookAndFeelDefaults();
    final ArrowsStyles arrowsStyle = AtomFileIconsConfig.getInstance().getArrowsStyle();
    defaults.put("Tree.collapsedIcon", arrowsStyle.getExpandIcon());
    defaults.put("Tree.expandedIcon", arrowsStyle.getCollapseIcon());
    defaults.put("Tree.collapsedSelectedIcon", arrowsStyle.getSelectedExpandIcon());
    defaults.put("Tree.expandedSelectedIcon", arrowsStyle.getSelectedCollapseIcon());

    SwingUtilities.invokeLater(ActionToolbarImpl::updateAllToolbarsImmediately);
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
    replaceTree();

    connect.subscribe(AtomConfigNotifier.TOPIC, atomFileIconsConfig -> replaceTree());
    connect.subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
      @Override
      public void projectOpened(@NotNull final Project project) {
        replaceTree();
      }
    });

    connect.subscribe(LafManagerListener.TOPIC, source -> replaceTree());
  }

  private void disposeComponent() {
    connect.disconnect();
  }
}
