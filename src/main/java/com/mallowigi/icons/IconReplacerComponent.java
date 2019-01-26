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
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.IconPathPatcher;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import com.mallowigi.config.AtomFileIconsConfig;
import com.mallowigi.config.ConfigNotifier;
import com.mallowigi.icons.patchers.*;
import com.mallowigi.icons.patchers.glyphs.*;

import java.util.Set;

import static com.mallowigi.icons.IconManager.applyFilter;

public final class IconReplacerComponent implements BaseComponent {
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

  private void updateIcons() {
    MTIconPatcher.clearCache();
    removePathPatchers();

    if (AtomFileIconsConfig.getInstance().isEnabledUIIcons()) {
      installPathPatchers();
    }
    if (AtomFileIconsConfig.getInstance().isEnabledPsiIcons()) {
      installPSIPatchers();
    }
    if (AtomFileIconsConfig.getInstance().isEnabledIcons()) {
      installFileIconsPatchers();
    }
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
    installPathPatcher(new WizardPluginsIconsPatcher());

    installPathPatcher(new PHPIconsPatcher());
    installPathPatcher(new PythonIconsPatcher());
    installPathPatcher(new AppEngineIconsPatcher());
    installPathPatcher(new CythonIconsPatcher());
    installPathPatcher(new MakoIconsPatcher());
    installPathPatcher(new JinjaIconsPatcher());
    installPathPatcher(new FlaskIconsPatcher());
    installPathPatcher(new DjangoIconsPatcher());
    installPathPatcher(new ChameleonIconsPatcher());
    installPathPatcher(new PyQtIconsPatcher());
    installPathPatcher(new Web2PythonIconsPatcher());

    installPathPatcher(new JavascriptIconsPatcher());
    installPathPatcher(new RubyIconsPatcher());

    installPathPatcher(new GolandIconsPatcher());
    installPathPatcher(new DataGripIconsPatcher());
    installPathPatcher(new CLionIconsPatcher());
    installPathPatcher(new AppCodeIconsPatcher());
    installPathPatcher(new RestClientIconsPatcher());

    installPathPatcher(new RiderIconsPatcher());
    installPathPatcher(new ResharperIconsPatcher());
  }

  @SuppressWarnings("OverlyCoupledMethod")
  private void installPSIPatchers() {
    installPathPatcher(new GlyphsPatcher());
    installPathPatcher(new ActionsGlyphsPatcher());
    installPathPatcher(new GeneralGlyphsPatcher());
    installPathPatcher(new GutterGlyphsPatcher());

    installPathPatcher(new JavascriptGlyphsPatcher());
    installPathPatcher(new PHPGlyphsPatcher());
    installPathPatcher(new PythonGlyphsPatcher());
    installPathPatcher(new RubyGlyphsPatcher());
    installPathPatcher(new DataGripGlyphsPatcher());
    installPathPatcher(new AppCodeGlyphsPatcher());
    installPathPatcher(new GolandGlyphsPatcher());
    installPathPatcher(new CLionGlyphsPatcher());
    installPathPatcher(new AopGlyphsPatcher());
    installPathPatcher(new OtherGlyphsPatcher());
  }

  private void installFileIconsPatchers() {
    installPathPatcher(new PHPFileIconsPatcher());
  }

  private void onSettingsChanged(final AtomFileIconsConfig atomFileIconsConfig) {
    updateFileIcons();
    updateIcons();
  }

  private void updateFileIcons() {
    ApplicationManager.getApplication().runWriteAction(() -> {
      final FileTypeManagerEx instanceEx = FileTypeManagerEx.getInstanceEx();
      instanceEx.fireFileTypesChanged();
      applyFilter();
      //      LafManager.getInstance().updateUI();
      ActionToolbarImpl.updateAllToolbarsImmediately();
    });
  }

  private void removePathPatchers() {
    for (final IconPathPatcher iconPathPatcher : CACHE) {
      IconLoader.removePathPatcher(iconPathPatcher);
    }
    CACHE.clear();
  }

  private void installPathPatcher(final IconPathPatcher patcher) {
    CACHE.add(patcher);
    IconLoader.installPathPatcher(patcher);
  }

  @Override
  public void disposeComponent() {
    MTIconPatcher.clearCache();
    connect.disconnect();
  }

  @Override
  public String getComponentName() {
    return "IconReplacerComponent";
  }
}
