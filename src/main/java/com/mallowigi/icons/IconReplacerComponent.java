/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Chris Magnussen and Elior Boukhobza
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

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.UISettingsListener;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.IconPathPatcher;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import com.mallowigi.config.AtomFileIconsConfig;
import com.mallowigi.config.ConfigNotifier;
import com.mallowigi.icons.patchers.*;

import java.util.Set;

import static com.mallowigi.icons.IconManager.applyFilter;

public final class IconReplacerComponent implements ApplicationComponent {

  private MessageBusConnection connect;
  private final Set<IconPathPatcher> CACHE = ContainerUtil.newHashSet();

  @Override
  public void initComponent() {
    updateIcons();

    // Listen for changes on the settings
    connect = ApplicationManager.getApplication().getMessageBus().connect();
    connect.subscribe(UISettingsListener.TOPIC, uiSettings -> applyFilter());
    connect.subscribe(ConfigNotifier.CONFIG_TOPIC, this::onSettingsChanged);

    ApplicationManager.getApplication().runWriteAction(() -> {
      IconManager.applyFilter();
      LafManager.getInstance().updateUI();
    });
  }

  private void installPathPatcher(IconPathPatcher patcher) {
    CACHE.add(patcher);
    IconLoader.installPathPatcher(patcher);
  }

  private void removePathPatcher(IconPathPatcher patcher) {
    CACHE.remove(patcher);
    IconLoader.removePathPatcher(patcher);
  }

  private void installPathPatchers() {
    installPathPatcher(new AllIconsPatcher());
    installPathPatcher(new ImagesIconsPatcher());
    installPathPatcher(new VCSIconsPatcher());
    installPathPatcher(new GradleIconsPatcher());
    installPathPatcher(new TasksIconsPatcher());
    installPathPatcher(new MavenIconsPatcher());
    installPathPatcher(new TerminalIconsPatcher());
    installPathPatcher(new BuildToolsIconsPatcher());
    installPathPatcher(new RemoteServersIconsPatcher());
    installPathPatcher(new DatabaseToolsIconsPatcher());

    installPathPatcher(new PHPIconsPatcher());
    installPathPatcher(new PythonIconsPatcher());
    installPathPatcher(new CythonIconsPatcher());
    installPathPatcher(new MakoIconsPatcher());
    installPathPatcher(new JinjaIconsPatcher());
    installPathPatcher(new FlaskIconsPatcher());
    installPathPatcher(new DjangoIconsPatcher());
    installPathPatcher(new ChameleonIconsPatcher());

    installPathPatcher(new RubyIconsPatcher());

    installPathPatcher(new GolandIconsPatcher());
    installPathPatcher(new DataGripIconsPatcher());
    installPathPatcher(new CLionIconsPatcher());
    installPathPatcher(new AppCodeIconsPatcher());
    installPathPatcher(new RestClientIconsPatcher());

    installPathPatcher(new RiderIconsPatcher());
    installPathPatcher(new ResharperIconsPatcher());
  }

  private void removePathPatchers() {
    for (IconPathPatcher iconPathPatcher : CACHE) {
      IconLoader.removePathPatcher(iconPathPatcher);
    }
    CACHE.clear();
  }

  private void onSettingsChanged(AtomFileIconsConfig atomFileIconsConfig) {
    updateFileIcons();
    updateIcons();
  }

  private void updateFileIcons() {
    ApplicationManager.getApplication().runWriteAction(() -> {
      FileTypeManagerEx instanceEx = FileTypeManagerEx.getInstanceEx();
      instanceEx.fireFileTypesChanged();
      applyFilter();
      LafManager.getInstance().updateUI();
      ActionToolbarImpl.updateAllToolbarsImmediately();
    });
  }

  private void updateIcons() {
    if (AtomFileIconsConfig.getInstance().isEnabledUIIcons()) {
      installPathPatchers();
    } else {
      removePathPatchers();
    }
  }

  @Override
  public void disposeComponent() {
    connect.disconnect();

    MTIconPatcher.clearCache();
  }

  @Override
  public String getComponentName() {
    return "IconReplacerComponent";
  }
}
