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
  public SettingsForm() {
    initComponents();
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JPanel content;
  private JLabel enableFileIconsIcon;
  private JCheckBox enableFileIconsCheckbox;
  private JLabel enableDirectoryIconsIcon;
  private JCheckBox enableDirectoryIconsCheckbox;
  private JLabel enableUIIconsIcon;
  private JCheckBox enableUIIconsCheckbox;
  // JFormDesigner - End of variables declaration  //GEN-END:variables

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    final ResourceBundle bundle = ResourceBundle.getBundle("config.AtomFileIconsBundle");
    content = new JPanel();
    enableFileIconsIcon = new JLabel();
    enableFileIconsCheckbox = new JCheckBox();
    enableDirectoryIconsIcon = new JLabel();
    enableDirectoryIconsCheckbox = new JCheckBox();
    enableUIIconsIcon = new JLabel();
    enableUIIconsCheckbox = new JCheckBox();

    //======== content ========
    {
      content.setLayout(new MigLayout(
          "hidemode 3",
          // columns
          "[fill]" +
          "[fill]" +
          "[fill]",
          // rows
          "[]" +
          "[]" +
          "[]"));

      //---- enableFileIconsIcon ----
      enableFileIconsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/files/atom@2x.png")));
      content.add(enableFileIconsIcon, "cell 0 0");

      //---- enableFileIconsCheckbox ----
      enableFileIconsCheckbox.setText(bundle.getString("SettingsForm.enableFileIconsCheckbox.text"));
      enableFileIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.enableFileIconsCheckbox.toolTipText"));
      content.add(enableFileIconsCheckbox, "cell 1 0");

      //---- enableDirectoryIconsIcon ----
      enableDirectoryIconsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/nodes/compiledClassesFolder@2x.png")));
      content.add(enableDirectoryIconsIcon, "cell 0 1");

      //---- enableDirectoryIconsCheckbox ----
      enableDirectoryIconsCheckbox.setText(bundle.getString("SettingsForm.enableDirectoryIconsCheckbox.text"));
      enableDirectoryIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.enableDirectoryIconsCheckbox.toolTipText"));
      content.add(enableDirectoryIconsCheckbox, "cell 1 1");

      //---- enableUIIconsIcon ----
      enableUIIconsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/nodes/plugin@2x.png")));
      content.add(enableUIIconsIcon, "cell 0 2");

      //---- enableUIIconsCheckbox ----
      enableUIIconsCheckbox.setText(bundle.getString("SettingsForm.enableUIIconsCheckbox.text"));
      enableUIIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.enableUIIconsCheckbox.toolTipText"));
      content.add(enableUIIconsCheckbox, "cell 1 2");
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void init() {

  }

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

  //region Directory Icons
  public boolean getIsEnabledDirectories() {
    return enableDirectoryIconsCheckbox.isSelected();
  }

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
