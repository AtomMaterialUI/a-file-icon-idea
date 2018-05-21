package com.mallowigi.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.mallowigi.config.AtomFileIconsConfig;

public final class EnableUIIconsAction extends ToggleAction {

  public static final AtomFileIconsConfig CONFIG = AtomFileIconsConfig.getInstance();

  @Override
  public boolean isSelected(final AnActionEvent e) {
    return CONFIG.isEnabledUIIcons();
  }

  @Override
  public void setSelected(final AnActionEvent e, final boolean state) {
    CONFIG.toggleUIIcons();
    CONFIG.fireChanged();
  }
}
