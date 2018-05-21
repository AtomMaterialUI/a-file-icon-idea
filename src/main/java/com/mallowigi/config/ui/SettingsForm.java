/*
 * Created by JFormDesigner on Mon May 21 20:14:40 IDT 2018
 */

package com.mallowigi.config.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * @author Elior Boukhobza
 */
public class SettingsForm implements SettingsFormUI {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JPanel content;
  private JCheckBox enableFileIconsCheckbox;
  private JCheckBox enableDirectoryIconsCheckbox;
  private JCheckBox enableUIIconsCheckbox;

  public SettingsForm() {
    initComponents();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    final ResourceBundle bundle = ResourceBundle.getBundle("config.Bundle");
    content = new JPanel();
    enableFileIconsCheckbox = new JCheckBox();
    enableDirectoryIconsCheckbox = new JCheckBox();
    enableUIIconsCheckbox = new JCheckBox();

    //======== content ========
    {
      content.setLayout(new MigLayout(
          "hidemode 3",
          // columns
          "[fill]",
          // rows
          "[]" +
          "[]" +
          "[]"));

      //---- enableFileIconsCheckbox ----
      enableFileIconsCheckbox.setText(bundle.getString("SettingsForm.enableFileIconsCheckbox.text"));
      content.add(enableFileIconsCheckbox, "cell 0 0");

      //---- enableDirectoryIconsCheckbox ----
      enableDirectoryIconsCheckbox.setText(bundle.getString("SettingsForm.enableDirectoryIconsCheckbox.text"));
      content.add(enableDirectoryIconsCheckbox, "cell 0 1");

      //---- enableUIIconsCheckbox ----
      enableUIIconsCheckbox.setText(bundle.getString("SettingsForm.enableUIIconsCheckbox.text"));
      content.add(enableUIIconsCheckbox, "cell 0 2");
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void init() {

  }
  // JFormDesigner - End of variables declaration  //GEN-END:variables

  @Override
  public JComponent getContent() {
    return content;
  }

  @Override
  public void afterStateSet() {

  }

  @Override
  public void dispose() {

  }

  //region File Icons
  public boolean getIsEnabledIcons() {
    return enableFileIconsCheckbox.isSelected();
  }

  public void setIsEnabledIcons(final boolean enabledIcons) {
    enableFileIconsCheckbox.setSelected(enabledIcons);
  }
  //endregion

  public boolean getIsEnabledDirectories() {
    return enableDirectoryIconsCheckbox.isSelected();
  }

  //region Directory Icons
  public void setIsEnabledDirectories(final boolean enabledDirectories) {
    enableDirectoryIconsCheckbox.setSelected(enabledDirectories);
  }
  //endregion

  //region UI Icons
  public boolean getIsEnabledUIIcons() {
    return enableUIIconsCheckbox.isSelected();
  }

  public void setIsEnabledUIIcons(final boolean enabledUIIcons) {
    enableUIIconsCheckbox.setSelected(enabledUIIcons);
  }
  //endregion
}
