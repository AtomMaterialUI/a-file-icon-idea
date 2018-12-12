/*
 * Created by JFormDesigner on Mon May 21 20:14:40 IDT 2018
 */

package com.mallowigi.config.ui;

import com.intellij.ui.ColorPanel;
import com.mallowigi.config.AtomFileIconsConfig;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * @author Elior Boukhobza
 */
@SuppressWarnings("FieldCanBeLocal")
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
  private ColorPanel monoColorChooser;
  private JLabel enableUIIconsIcon;
  private JCheckBox enableUIIconsCheckbox;
  private JTextPane notice;


  // JFormDesigner - End of variables declaration  //GEN-END:variables

  @SuppressWarnings("Convert2MethodRef")
  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    ResourceBundle bundle = ResourceBundle.getBundle("config.AtomFileIconsBundle");
    content = new JPanel();
    enableFileIconsIcon = new JLabel();
    enableFileIconsCheckbox = new JCheckBox();
    enableDirectoryIconsIcon = new JLabel();
    enableDirectoryIconsCheckbox = new JCheckBox();
    monochromeIcon = new JLabel();
    monochromeCheckbox = new JCheckBox();
    monoColorChooser = new ColorPanel();
    enableUIIconsIcon = new JLabel();
    enableUIIconsCheckbox = new JCheckBox();
    notice = new JTextPane();

    //======== content ========
    {
      content.setLayout(new MigLayout(
          "hidemode 3",
          // columns
          "[fill]" +
              "[::600,fill]" +
              "[fill]",
          // rows
          "[]" +
              "[]" +
              "[]" +
              "[]" +
              "[]"));

      //---- enableFileIconsIcon ----
      enableFileIconsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/nodes/atom@2x.png")));
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

      //---- monochromeIcon ----
      monochromeIcon.setIcon(new ImageIcon(getClass().getResource("/icons/nodes/monochrome@2x.png")));
      content.add(monochromeIcon, "cell 0 2");

      //---- monochromeCheckbox ----
      monochromeCheckbox.setText(bundle.getString("SettingsForm.monochromeCheckbox.text"));
      monochromeCheckbox.setToolTipText(bundle.getString("SettingsForm.monochromeCheckbox.toolTipText"));
      monochromeCheckbox.setIcon(null);
      monochromeCheckbox.addChangeListener(e -> monochromeCheckboxStateChanged(e));
      content.add(monochromeCheckbox, "cell 1 2");

      //---- monoColorChooser ----
      monoColorChooser.setToolTipText(bundle.getString("SettingsForm.monoColorChooser.toolTipText"));
      content.add(monoColorChooser, "cell 1 2,alignx right,growx 0");

      //---- enableUIIconsIcon ----
      enableUIIconsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/nodes/plugin@2x.png")));
      content.add(enableUIIconsIcon, "cell 0 3");

      //---- enableUIIconsCheckbox ----
      enableUIIconsCheckbox.setText(bundle.getString("SettingsForm.enableUIIconsCheckbox.text"));
      enableUIIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.enableUIIconsCheckbox.toolTipText"));
      content.add(enableUIIconsCheckbox, "cell 1 3");

      //---- notice ----
      notice.setText(bundle.getString("SettingsForm.notice.text"));
      notice.setEnabled(false);
      content.add(notice, "tag help,cell 1 4,align left top,grow 0 0,wmax 400");
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  private void monochromeCheckboxStateChanged(final ChangeEvent e) {
    monoColorChooser.setEnabled(monochromeCheckbox.isEnabled());
  }

  @Override
  public void init() {

  }

  public final void setFormState(final AtomFileIconsConfig config) {

    this.setIsEnabledIcons(config.isEnabledIcons());
    this.setIsEnabledDirectories(config.isEnabledDirectories());
    this.setIsEnabledMonochromeIcons(config.isMonochromeIcons());
    this.setIsEnabledUIIcons(config.isEnabledUIIcons());
    this.setMonochromeColor(config.getMonochromeColor());

    this.afterStateSet();
  }

  public final boolean isModified(final AtomFileIconsConfig config) {

    boolean modified = config.isEnabledIconsChanged(this.getIsEnabledIcons());
    modified = modified || config.isEnabledDirectoriesChanged(this.getIsEnabledDirectories());
    modified = modified || config.isMonochromeIconsChanged(this.getIsEnabledMonochromeIcons());
    modified = modified || config.isEnabledUIIconsChanged(this.getIsEnabledUIIcons());
    modified = modified || config.isMonochromeColorChanged(this.getMonochromeColor());

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

  //region mono color
  public Color getMonochromeColor() {
    return monoColorChooser.getSelectedColor();
  }

  private void setMonochromeColor(final Color color) {
    monoColorChooser.setSelectedColor(color);
  }
  //endregion
}
