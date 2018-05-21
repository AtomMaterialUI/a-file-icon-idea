package com.mallowigi.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.mallowigi.config.AtomFileIconsConfig;

public final class EnableDirectoryIconsAction extends ToggleAction {

  @Override
  public boolean isSelected(final AnActionEvent e) {
    return AtomFileIconsConfig.getInstance().isEnabledDirectories();
  }

  @Override
  public void setSelected(final AnActionEvent e, final boolean state) {
    AtomFileIconsConfig.getInstance().toggleDirectoriesIcons();
  }

  /**
   * Disable Contrast Mode if Material Theme is disabled
   *
   * @param e
   */
  @Override
  public void update(final AnActionEvent e) {
    e.getPresentation().setEnabled(AtomFileIconsConfig.getInstance().isEnabledDirectories());
  }
}
