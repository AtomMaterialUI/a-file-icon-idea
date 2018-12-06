/*
 * Created by JFormDesigner on Mon May 21 20:14:40 IDT 2018
 */

package com.mallowigi.config.ui;

import com.mallowigi.config.AtomFileIconsConfig;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * @author Elior Boukhobza
 */
public class SettingsForm implements SettingsFormUI {
  public SettingsForm() {
    this.initComponents();
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JPanel content;
  private JLabel enableFileIconsIcon;
  private JCheckBox enableFileIconsCheckbox;
  private JLabel enableDirectoryIconsIcon;
  private JCheckBox enableDirectoryIconsCheckbox;
  private JLabel monochromeIcon;
  private JCheckBox monochromeCheckbox;
  private JLabel enableUIIconsIcon;
  private JCheckBox enableUIIconsCheckbox;
  private JTextPane notice;
  // JFormDesigner - End of variables declaration  //GEN-END:variables

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    final ResourceBundle bundle = ResourceBundle.getBundle("config.AtomFileIconsBundle");
    this.content = new JPanel();
    this.enableFileIconsIcon = new JLabel();
    this.enableFileIconsCheckbox = new JCheckBox();
    this.enableDirectoryIconsIcon = new JLabel();
    this.enableDirectoryIconsCheckbox = new JCheckBox();
    this.monochromeIcon = new JLabel();
    this.monochromeCheckbox = new JCheckBox();
    this.enableUIIconsIcon = new JLabel();
    this.enableUIIconsCheckbox = new JCheckBox();
    this.notice = new JTextPane();

    //======== content ========
    {
      this.content.setLayout(new MigLayout(
          "hidemode 3",
          // columns
          "[fill]" +
          "[fill]" +
          "[fill]",
          // rows
          "[]" +
          "[]" +
          "[]" +
          "[]" +
          "[]"));

      //---- enableFileIconsIcon ----
      this.enableFileIconsIcon.setIcon(new ImageIcon(this.getClass().getResource("/icons/nodes/atom@2x.png")));
      this.content.add(this.enableFileIconsIcon, "cell 0 0");

      //---- enableFileIconsCheckbox ----
      this.enableFileIconsCheckbox.setText(bundle.getString("SettingsForm.enableFileIconsCheckbox.text"));
      this.enableFileIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.enableFileIconsCheckbox.toolTipText"));
      this.content.add(this.enableFileIconsCheckbox, "cell 1 0");

      //---- enableDirectoryIconsIcon ----
      this.enableDirectoryIconsIcon.setIcon(new ImageIcon(this.getClass().getResource("/icons/nodes/compiledClassesFolder@2x.png")));
      this.content.add(this.enableDirectoryIconsIcon, "cell 0 1");

      //---- enableDirectoryIconsCheckbox ----
      this.enableDirectoryIconsCheckbox.setText(bundle.getString("SettingsForm.enableDirectoryIconsCheckbox.text"));
      this.enableDirectoryIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.enableDirectoryIconsCheckbox.toolTipText"));
      this.content.add(this.enableDirectoryIconsCheckbox, "cell 1 1");

      //---- monochromeIcon ----
      this.monochromeIcon.setIcon(new ImageIcon(this.getClass().getResource("/icons/nodes/monochrome@2x.png")));
      this.content.add(this.monochromeIcon, "cell 0 2");

      //---- monochromeCheckbox ----
      this.monochromeCheckbox.setText(bundle.getString("SettingsForm.monochromeCheckbox.text"));
      this.monochromeCheckbox.setToolTipText(bundle.getString("SettingsForm.monochromeCheckbox.toolTipText"));
      this.monochromeCheckbox.setIcon(null);
      this.content.add(this.monochromeCheckbox, "cell 1 2");

      //---- enableUIIconsIcon ----
      this.enableUIIconsIcon.setIcon(new ImageIcon(this.getClass().getResource("/icons/nodes/plugin@2x.png")));
      this.content.add(this.enableUIIconsIcon, "cell 0 3");

      //---- enableUIIconsCheckbox ----
      this.enableUIIconsCheckbox.setText(bundle.getString("SettingsForm.enableUIIconsCheckbox.text"));
      this.enableUIIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.enableUIIconsCheckbox.toolTipText"));
      this.content.add(this.enableUIIconsCheckbox, "cell 1 3");

      //---- notice ----
      this.notice.setText(bundle.getString("SettingsForm.notice.text"));
      this.content.add(this.notice, "cell 1 4");
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void init() {

  }

  public final void setFormState(final AtomFileIconsConfig config) {

    this.setIsEnabledIcons(config.isEnabledIcons());
    this.setIsEnabledDirectories(config.isEnabledDirectories());
    this.setIsEnabledMonochromeIcons(config.isMonochromeIcons());
    this.setIsEnabledUIIcons(config.isEnabledUIIcons());

    this.afterStateSet();
  }

  public final boolean isModified(final AtomFileIconsConfig config) {

    boolean modified = config.isEnabledIconsChanged(this.getIsEnabledIcons());
    modified = modified || config.isEnabledDirectoriesChanged(this.getIsEnabledDirectories());
    modified = modified || config.isMonochromeIconsChanged(this.getIsEnabledMonochromeIcons());
    modified = modified || config.isEnabledUIIconsChanged(this.getIsEnabledUIIcons());

    return modified;
  }

  @Override
  public JComponent getContent() {
    return this.content;
  }

  @Override
  public void afterStateSet() {

  }

  @Override
  public void dispose() {

  }

  //region File Icons
  public boolean getIsEnabledIcons() {
    return this.enableFileIconsCheckbox.isSelected();
  }

  private void setIsEnabledIcons(final boolean enabledIcons) {
    this.enableFileIconsCheckbox.setSelected(enabledIcons);
  }
  //endregion

  //region Directory Icons
  public boolean getIsEnabledDirectories() {
    return this.enableDirectoryIconsCheckbox.isSelected();
  }

  private void setIsEnabledDirectories(final boolean enabledDirectories) {
    this.enableDirectoryIconsCheckbox.setSelected(enabledDirectories);
  }
  //endregion

  //region Monochrome Icons
  public boolean getIsEnabledMonochromeIcons() {
    return this.monochromeCheckbox.isSelected();
  }

  private void setIsEnabledMonochromeIcons(final boolean isEnabledMonochromeIcons) {
    this.monochromeCheckbox.setSelected(isEnabledMonochromeIcons);
  }
  //endregion

  //region UI Icons
  public boolean getIsEnabledUIIcons() {
    return this.enableUIIconsCheckbox.isSelected();
  }

  private void setIsEnabledUIIcons(final boolean enabledUIIcons) {
    this.enableUIIconsCheckbox.setSelected(enabledUIIcons);
  }
  //endregion
}
