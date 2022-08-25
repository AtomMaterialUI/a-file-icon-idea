/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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

/*
 * Created by JFormDesigner on Mon May 21 20:14:40 IDT 2018
 */

package com.mallowigi.config.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ColorPanel;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.components.OnOffButton;
import com.mallowigi.config.AtomFileIconsConfig;
import com.mallowigi.tree.arrows.ArrowsStyles;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.ResourceBundle;

@SuppressWarnings({"FieldCanBeLocal",
  "StringConcatenation",
  "PublicMethodNotExposedInInterface",
  "FeatureEnvy",
  "BooleanMethodNameMustStartWithQuestion",
  "ClassWithTooManyFields",
  "ClassWithTooManyMethods",
  "OverlyLongMethod",
  "unused",
  "DuplicateStringLiteralInspection",
  "ConstantConditions",
  "InstanceVariableMayNotBeInitialized",
  "uncachedAlloc",
  "AnonymousInnerClassMayBeStatic"})
public final class SettingsForm implements SettingsFormUI {

  private SpinnerModel customIconSizeSpinnerModel;
  private SpinnerModel customLineHeightSpinnerModel;

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
  private JLabel enablePSIIconsIcon;
  private JCheckBox enablePSIIconsCheckbox;
  private JLabel hideFileIconsIcon;
  private JCheckBox hideFileIconsCheckbox;
  private JLabel hideFolderIconsIcon;
  private JCheckBox hideFolderIconsCheckbox;
  private JLabel hollowFoldersIcon;
  private JCheckBox hollowFoldersCheckbox;
  private JLabel customIconSizeIcon;
  private JCheckBox customIconSizeCheckbox;
  private JSpinner customIconSizeSpinner;
  private JCheckBox customLineHeightCheckbox;
  private JSpinner customLineHeightSpinner;
  private JLabel arrowsStyleIcon;
  private JLabel arrowsStyleLabel;
  private ComboBox<ArrowsStyles> arrowsStyleComboBox;
  private JLabel accentColorIcon;
  private JCheckBox accentColorCheckbox;
  private ColorPanel accentColorChooser;
  private JCheckBox themedColorCheckbox;
  private ColorPanel themedColorChooser;
  private JLabel lowPowerLabel;
  private OnOffButton lowPowerSwitch;
  // JFormDesigner - End of variables declaration  //GEN-END:variables

  public void setFormState(final AtomFileIconsConfig config) {
    setIsEnabledIcons(config.isEnabledIcons());
    setIsEnabledDirectories(config.isEnabledDirectories());
    setIsEnabledMonochromeIcons(config.isMonochromeIcons());
    setIsEnabledUIIcons(config.isEnabledUIIcons());
    setMonochromeColor(config.getMonochromeColor());
    setIsEnabledPsiIcons(config.isEnabledPsiIcons());
    setIsHiddenFileIcons(config.isHideFileIcons());
    setIsHiddenFolderIcons(config.isHideFolderIcons());
    setIsHollowFoldersEnabled(config.isUseHollowFolders());
    setArrowsStyle(config.getArrowsStyle());
    setAccentColorEnabled(config.isAccentColorEnabled());
    setAccentColor(config.getAccentColor());
    setThemedColorEnabled(config.isThemedColorEnabled());
    setThemedColor(config.getThemedColor());
    setHasCustomIconSize(config.getHasCustomIconSize());
    setCustomIconSize(config.getCustomIconSize());
    setHasCustomLineHeight(config.getHasCustomLineHeight());
    setCustomLineHeight(config.getCustomLineHeight());
    setLowPowerMode(config.isLowPowerMode());

    afterStateSet();
  }

  @Override
  public void init() {
    initComponents();
    setupComponents();
  }

  private void setupComponents() {
    configureSpinners();
  }

  private static int valueInRange(final int value, final int min, final int max) {
    return Integer.min(max, Integer.max(value, min));
  }

  private void configureSpinners() {
    final AtomFileIconsConfig config = AtomFileIconsConfig.getInstance();
    final int customIconSize = valueInRange(config.getCustomIconSize(),
      AtomFileIconsConfig.MIN_ICON_SIZE,
      AtomFileIconsConfig.MAX_ICON_SIZE
    );

    customIconSizeSpinnerModel = new SpinnerNumberModel(customIconSize,
      AtomFileIconsConfig.MIN_ICON_SIZE,
      AtomFileIconsConfig.MAX_ICON_SIZE,
      1
    );
    customIconSizeSpinner.setModel(customIconSizeSpinnerModel);

    final int customLineHeight = valueInRange(config.getCustomLineHeight(),
      AtomFileIconsConfig.MIN_LINE_HEIGHT,
      AtomFileIconsConfig.MAX_LINE_HEIGHT
    );

    customLineHeightSpinnerModel = new SpinnerNumberModel(customLineHeight,
      AtomFileIconsConfig.MIN_LINE_HEIGHT,
      AtomFileIconsConfig.MAX_LINE_HEIGHT,
      1
    );
    customLineHeightSpinner.setModel(customLineHeightSpinnerModel);

  }

  @SuppressWarnings("OverlyComplexMethod")
  public boolean isModified(final AtomFileIconsConfig config) {
    boolean modified = config.isEnabledIconsChanged(getIsEnabledIcons());
    modified = modified || config.isEnabledDirectoriesChanged(getIsEnabledDirectories());
    modified = modified || config.isMonochromeIconsChanged(getIsEnabledMonochromeIcons());
    modified = modified || config.isEnabledUIIconsChanged(getIsEnabledUIIcons());
    modified = modified || config.isMonochromeColorChanged(getMonochromeColor());
    modified = modified || config.isEnabledPsiIconsChanged(getIsEnabledPsiIcons());
    modified = modified || config.isHideFileIconsChanged(getIsHiddenFileIcons());
    modified = modified || config.isHideFolderIconsChanged(getIsHiddenFolderIcons());
    modified = modified || config.isUseHollowFoldersChanged(getIsHollowFoldersEnabled());
    modified = modified || config.isArrowsStyleChanged(getArrowsStyle());
    modified = modified || config.isAccentColorEnabledChanged(getIsAccentColorEnabled());
    modified = modified || config.isAccentColorChanged(getAccentColor());
    modified = modified || config.isThemedColorEnabledChanged(getIsThemedColorEnabled());
    modified = modified || config.isThemedColorChanged(getThemedColor());
    modified = modified || config.hasCustomIconSizeChanged(getHasCustomIconSize());
    modified = modified || config.isCustomIconSizeChanged(getCustomIconSize());
    modified = modified || config.isHasCustomLineHeightChanged(getHasCustomLineHeight());
    modified = modified || config.isCustomLineHeightChanged(getCustomLineHeight());
    modified = modified || config.isLowPowerModeChanged(isLowPowerMode());

    return modified;
  }

  @Override
  public JComponent getContent() {
    return content;
  }

  @Override
  public void afterStateSet() {
    enableUIIconsCheckboxActionPerformed(null);
    enableDirectoryIconsCheckboxActionPerformed(null);
    monochromeCheckboxStateChanged(null);
    accentColorCheckboxActionPerformed(null);
    themedColorCheckboxActionPerformed(null);
    customIconSizeActionPerformed(null);
    customLineHeightActionPerformed(null);
  }

  @Override
  public void dispose() {
    // empty
  }

  @SuppressWarnings({"Convert2MethodRef",
    "LocalCanBeFinal"})
  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    ResourceBundle bundle = ResourceBundle.getBundle("messages.AtomFileIconsBundle"); //NON-NLS
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
    enablePSIIconsIcon = new JLabel();
    enablePSIIconsCheckbox = new JCheckBox();
    hideFileIconsIcon = new JLabel();
    hideFileIconsCheckbox = new JCheckBox();
    hideFolderIconsIcon = new JLabel();
    hideFolderIconsCheckbox = new JCheckBox();
    hollowFoldersIcon = new JLabel();
    hollowFoldersCheckbox = new JCheckBox();
    customIconSizeIcon = new JLabel();
    customIconSizeCheckbox = new JCheckBox();
    customIconSizeSpinner = new JSpinner();
    customLineHeightCheckbox = new JCheckBox();
    customLineHeightSpinner = new JSpinner();
    arrowsStyleIcon = new JLabel();
    arrowsStyleLabel = new JLabel();
    arrowsStyleComboBox = new ComboBox<>();
    accentColorIcon = new JLabel();
    accentColorCheckbox = new JCheckBox();
    accentColorChooser = new ColorPanel();
    themedColorCheckbox = new JCheckBox();
    themedColorChooser = new ColorPanel();
    lowPowerLabel = new JLabel();
    lowPowerSwitch = new OnOffButton();

    //======== content ========
    {
      content.setLayout(new MigLayout(
        "hidemode 3", //NON-NLS
        // columns
        "[fill]" + //NON-NLS
          "[::600,fill]" + //NON-NLS
          "[fill]", //NON-NLS
        // rows
        "[]" + //NON-NLS
          "[]" + //NON-NLS
          "[]" + //NON-NLS
          "[]" + //NON-NLS
          "[]" + //NON-NLS
          "[]" + //NON-NLS
          "[]" + //NON-NLS
          "[]" + //NON-NLS
          "[]" + //NON-NLS
          "[]" + //NON-NLS
          "[50]" + //NON-NLS
          "[]" + //NON-NLS
          "[]" + //NON-NLS
          "[]" + //NON-NLS
          "[]")); //NON-NLS

      //---- enableFileIconsIcon ----
      enableFileIconsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/settings/atom@2x.png"))); //NON-NLS
      content.add(enableFileIconsIcon, "cell 0 0"); //NON-NLS

      //---- enableFileIconsCheckbox ----
      enableFileIconsCheckbox.setText(bundle.getString("SettingsForm.enableFileIconsCheckbox.text")); //NON-NLS
      enableFileIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.enableFileIconsCheckbox.toolTipText")); //NON-NLS
      content.add(enableFileIconsCheckbox, "cell 1 0"); //NON-NLS

      //---- enableDirectoryIconsIcon ----
      enableDirectoryIconsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/settings/compiledClassesFolder@2x.png"))); //NON-NLS
      content.add(enableDirectoryIconsIcon, "cell 0 1"); //NON-NLS

      //---- enableDirectoryIconsCheckbox ----
      enableDirectoryIconsCheckbox.setText(bundle.getString("SettingsForm.enableDirectoryIconsCheckbox.text")); //NON-NLS
      enableDirectoryIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.enableDirectoryIconsCheckbox.toolTipText")); //NON-NLS
      enableDirectoryIconsCheckbox.addActionListener(e -> enableDirectoryIconsCheckboxActionPerformed(e));
      content.add(enableDirectoryIconsCheckbox, "cell 1 1"); //NON-NLS

      //---- monochromeIcon ----
      monochromeIcon.setIcon(new ImageIcon(getClass().getResource("/icons/settings/monochrome@2x.png"))); //NON-NLS
      content.add(monochromeIcon, "cell 0 2"); //NON-NLS

      //---- monochromeCheckbox ----
      monochromeCheckbox.setText(bundle.getString("SettingsForm.monochromeCheckbox.text")); //NON-NLS
      monochromeCheckbox.setToolTipText(bundle.getString("SettingsForm.monochromeCheckbox.toolTipText")); //NON-NLS
      monochromeCheckbox.setIcon(null);
      monochromeCheckbox.addChangeListener(e -> monochromeCheckboxStateChanged(e));
      content.add(monochromeCheckbox, "cell 1 2"); //NON-NLS

      //---- monoColorChooser ----
      monoColorChooser.setToolTipText(bundle.getString("SettingsForm.monoColorChooser.toolTipText")); //NON-NLS
      content.add(monoColorChooser, "cell 2 2,alignx right,growx 0"); //NON-NLS

      //---- enableUIIconsIcon ----
      enableUIIconsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/settings/plugin@2x.png"))); //NON-NLS
      content.add(enableUIIconsIcon, "cell 0 3"); //NON-NLS

      //---- enableUIIconsCheckbox ----
      enableUIIconsCheckbox.setText(bundle.getString("SettingsForm.enableUIIconsCheckbox.text")); //NON-NLS
      enableUIIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.enableUIIconsCheckbox.toolTipText")); //NON-NLS
      enableUIIconsCheckbox.addActionListener(e -> enableUIIconsCheckboxActionPerformed(e));
      content.add(enableUIIconsCheckbox, "cell 1 3"); //NON-NLS

      //---- enablePSIIconsIcon ----
      enablePSIIconsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/settings/psiIcons@2x.png"))); //NON-NLS
      content.add(enablePSIIconsIcon, "cell 0 4"); //NON-NLS

      //---- enablePSIIconsCheckbox ----
      enablePSIIconsCheckbox.setText(bundle.getString("SettingsForm.enablePSIIconsCheckbox.text")); //NON-NLS
      enablePSIIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.enablePSIIconsCheckbox.toolTipText")); //NON-NLS
      content.add(enablePSIIconsCheckbox, "cell 1 4"); //NON-NLS

      //---- hideFileIconsIcon ----
      hideFileIconsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/settings/hideFileAction@2x.png"))); //NON-NLS
      content.add(hideFileIconsIcon, "cell 0 5"); //NON-NLS

      //---- hideFileIconsCheckbox ----
      hideFileIconsCheckbox.setText(bundle.getString("SettingsForm.hideFileIconsCheckbox.text")); //NON-NLS
      hideFileIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.hideFileIconsCheckbox.toolTipText")); //NON-NLS
      content.add(hideFileIconsCheckbox, "cell 1 5"); //NON-NLS

      //---- hideFolderIconsIcon ----
      hideFolderIconsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/settings/hideFolderAction@2x.png"))); //NON-NLS
      content.add(hideFolderIconsIcon, "cell 0 6"); //NON-NLS

      //---- hideFolderIconsCheckbox ----
      hideFolderIconsCheckbox.setText(bundle.getString("SettingsForm.hideFolderIconsCheckbox.text")); //NON-NLS
      hideFolderIconsCheckbox.setToolTipText(bundle.getString("SettingsForm.hideFolderIconsCheckbox.toolTipText")); //NON-NLS
      content.add(hideFolderIconsCheckbox, "cell 1 6"); //NON-NLS

      //---- hollowFoldersIcon ----
      hollowFoldersIcon.setIcon(new ImageIcon(getClass().getResource("/icons/settings/folderOpen@2x.png"))); //NON-NLS
      content.add(hollowFoldersIcon, "cell 0 7"); //NON-NLS

      //---- hollowFoldersCheckbox ----
      hollowFoldersCheckbox.setText(bundle.getString("SettingsForm.hollowFoldersCheckbox.text")); //NON-NLS
      hollowFoldersCheckbox.setToolTipText(bundle.getString("SettingsForm.hollowFoldersCheckbox.toolTipText")); //NON-NLS
      content.add(hollowFoldersCheckbox, "cell 1 7"); //NON-NLS

      //---- customIconSizeIcon ----
      customIconSizeIcon.setIcon(new ImageIcon(getClass().getResource("/icons/settings/plus@2x.png"))); //NON-NLS
      content.add(customIconSizeIcon, "cell 0 8"); //NON-NLS

      //---- customIconSizeCheckbox ----
      customIconSizeCheckbox.setText(bundle.getString("SettingsForm.customIconSizeCheckbox.text")); //NON-NLS
      customIconSizeCheckbox.setToolTipText(bundle.getString("SettingsForm.customIconSizeCheckbox.toolTipText")); //NON-NLS
      customIconSizeCheckbox.addActionListener(e -> customIconSizeActionPerformed(e));
      content.add(customIconSizeCheckbox, "cell 1 8"); //NON-NLS
      content.add(customIconSizeSpinner, "cell 2 8"); //NON-NLS

      //---- customLineHeightCheckbox ----
      customLineHeightCheckbox.setText(bundle.getString("SettingsForm.customLineHeightCheckbox.text")); //NON-NLS
      customLineHeightCheckbox.setToolTipText(bundle.getString("SettingsForm.customLineHeightCheckbox.toolTipText")); //NON-NLS
      customLineHeightCheckbox.addActionListener(e -> customLineHeightActionPerformed(e));
      content.add(customLineHeightCheckbox, "cell 1 9"); //NON-NLS
      content.add(customLineHeightSpinner, "cell 2 9"); //NON-NLS

      //---- arrowsStyleIcon ----
      arrowsStyleIcon.setIcon(new ImageIcon(getClass().getResource("/icons/settings/arrowRight@2x.png"))); //NON-NLS
      content.add(arrowsStyleIcon, "cell 0 10"); //NON-NLS

      //---- arrowsStyleLabel ----
      arrowsStyleLabel.setText(bundle.getString("SettingsForm.arrowsStyleLabel.text")); //NON-NLS
      arrowsStyleLabel.setToolTipText(bundle.getString("SettingsForm.arrowsStyleLabel.toolTipText")); //NON-NLS
      content.add(arrowsStyleLabel, "pad 0,cell 1 10,aligny center,grow 100 0"); //NON-NLS

      //---- arrowsStyleComboBox ----
      arrowsStyleComboBox.setToolTipText(bundle.getString("SettingsForm.arrowsStyleComboBox.toolTipText")); //NON-NLS
      content.add(arrowsStyleComboBox, "cell 2 10,align right center,grow 0 0,width 120:120:120"); //NON-NLS

      //---- accentColorIcon ----
      accentColorIcon.setIcon(new ImageIcon(getClass().getResource("/icons/settings/accentColor@2x.png"))); //NON-NLS
      content.add(accentColorIcon, "cell 0 11"); //NON-NLS

      //---- accentColorCheckbox ----
      accentColorCheckbox.setText(bundle.getString("SettingsForm.accentColorCheckbox.text")); //NON-NLS
      accentColorCheckbox.setToolTipText(bundle.getString("SettingsForm.accentColorCheckbox.toolTipText")); //NON-NLS
      accentColorCheckbox.addActionListener(e -> accentColorCheckboxActionPerformed(e));
      content.add(accentColorCheckbox, "cell 1 11"); //NON-NLS

      //---- accentColorChooser ----
      accentColorChooser.setToolTipText(bundle.getString("SettingsForm.accentColorChooser.toolTipText")); //NON-NLS
      content.add(accentColorChooser, "cell 2 11,alignx right,growx 0"); //NON-NLS

      //---- themedColorCheckbox ----
      themedColorCheckbox.setText(bundle.getString("SettingsForm.themedColorCheckbox.text")); //NON-NLS
      themedColorCheckbox.setToolTipText(bundle.getString("SettingsForm.themedColorCheckbox.toolTipText")); //NON-NLS
      themedColorCheckbox.addActionListener(e -> themedColorCheckboxActionPerformed(e));
      content.add(themedColorCheckbox, "cell 1 12"); //NON-NLS

      //---- themedColorChooser ----
      themedColorChooser.setToolTipText(bundle.getString("SettingsForm.themedColorChooser.toolTipText")); //NON-NLS
      content.add(themedColorChooser, "cell 2 12,alignx right,growx 0"); //NON-NLS

      //---- lowPowerLabel ----
      lowPowerLabel.setText(bundle.getString("SettingsForm.lowPowerSwitch.text")); //NON-NLS
      content.add(lowPowerLabel, "cell 1 13"); //NON-NLS

      //---- lowPowerSwitch ----
      lowPowerSwitch.setText(bundle.getString("SettingsForm.lowPowerSwitch.text")); //NON-NLS
      lowPowerSwitch.setToolTipText(bundle.getString("SettingsForm.lowPowerSwitch.toolTipText")); //NON-NLS
      content.add(lowPowerSwitch, "cell 1 13,alignx right,growx 0,hmin 32"); //NON-NLS
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents

    // Arrows
    initializeComboboxes();
  }

  //region Events
  private void enableUIIconsCheckboxActionPerformed(final ActionEvent e) {
    //    enablePSIIconsCheckbox.setEnabled(enableUIIconsCheckbox.isSelected());
  }

  private void enableDirectoryIconsCheckboxActionPerformed(final ActionEvent e) {
    hollowFoldersCheckbox.setEnabled(enableDirectoryIconsCheckbox.isSelected());
  }

  private void monochromeCheckboxStateChanged(final ChangeEvent e) {
    monoColorChooser.setEnabled(monochromeCheckbox.isSelected());
  }

  private void themedColorCheckboxActionPerformed(final ActionEvent e) {
    themedColorChooser.setEnabled(themedColorCheckbox.isSelected());
  }

  private void customIconSizeActionPerformed(final ActionEvent e) {
    customIconSizeSpinner.setEnabled(customIconSizeCheckbox.isSelected());
  }

  private void customLineHeightActionPerformed(final ActionEvent e) {
    customLineHeightSpinner.setEnabled(customLineHeightCheckbox.isSelected());
  }
  //endregion

  //region File Icons
  public boolean getIsEnabledIcons() {
    return enableFileIconsCheckbox.isSelected();
  }

  private void setIsEnabledIcons(final boolean enabledIcons) {
    enableFileIconsCheckbox.setSelected(enabledIcons);
  }
  //endregion

  //region Directory Icons
  public boolean getIsEnabledDirectories() {
    return enableDirectoryIconsCheckbox.isSelected();
  }

  private void setIsEnabledDirectories(final boolean enabledDirectories) {
    enableDirectoryIconsCheckbox.setSelected(enabledDirectories);
  }
  //endregion

  //region Monochrome Icons
  public boolean getIsEnabledMonochromeIcons() {
    return monochromeCheckbox.isSelected();
  }

  private void setIsEnabledMonochromeIcons(final boolean isEnabledMonochromeIcons) {
    monochromeCheckbox.setSelected(isEnabledMonochromeIcons);
  }
  //endregion

  //region UI Icons
  public boolean getIsEnabledUIIcons() {
    return enableUIIconsCheckbox.isSelected();
  }

  private void setIsEnabledUIIcons(final boolean enabledUIIcons) {
    enableUIIconsCheckbox.setSelected(enabledUIIcons);
  }
  //endregion

  //region mono color
  public String getMonochromeColor() {
    return ColorUtil.toHex(Objects.requireNonNull(monoColorChooser.getSelectedColor()));
  }

  private void setMonochromeColor(final String color) {
    monoColorChooser.setSelectedColor(ColorUtil.fromHex(color));
  }
  //endregion

  //region psi Icons
  public boolean getIsEnabledPsiIcons() {
    return enablePSIIconsCheckbox.isSelected();
  }

  private void setIsEnabledPsiIcons(final boolean enabledPsiIcons) {
    enablePSIIconsCheckbox.setSelected(enabledPsiIcons);
  }
  //endregion

  //region hidden file icons
  public boolean getIsHiddenFileIcons() {
    return hideFileIconsCheckbox.isSelected();
  }

  @SuppressWarnings("NegativelyNamedBooleanVariable")
  private void setIsHiddenFileIcons(final boolean isHiddenFileIcons) {
    hideFileIconsCheckbox.setSelected(isHiddenFileIcons);
  }
  //endregion

  //region hidden folder icons
  public boolean getIsHiddenFolderIcons() {
    return hideFolderIconsCheckbox.isSelected();
  }

  @SuppressWarnings("NegativelyNamedBooleanVariable")
  private void setIsHiddenFolderIcons(final boolean isHiddenFolderIcons) {
    hideFolderIconsCheckbox.setSelected(isHiddenFolderIcons);
  }
  //endregion

  //region hollow folders
  public boolean getIsHollowFoldersEnabled() {
    return hollowFoldersCheckbox.isSelected();
  }

  private void setIsHollowFoldersEnabled(final boolean isHollowFoldersEnabled) {
    hollowFoldersCheckbox.setSelected(isHollowFoldersEnabled);
  }
  //endregion

  //region arrows styles
  public ArrowsStyles getArrowsStyle() {
    return (ArrowsStyles) arrowsStyleComboBox.getSelectedItem();
  }

  private void setArrowsStyle(final ArrowsStyles arrowsStyle) {
    arrowsStyleComboBox.setSelectedItem(arrowsStyle);
  }
  //endregion

  //region accent color
  public String getAccentColor() {
    return ColorUtil.toHex(Objects.requireNonNull(accentColorChooser.getSelectedColor()));
  }

  private void setAccentColor(final String color) {
    accentColorChooser.setSelectedColor(ColorUtil.fromHex(color));
  }

  public boolean getIsAccentColorEnabled() {
    return accentColorCheckbox.isSelected();
  }

  private void setAccentColorEnabled(final boolean enabled) {
    accentColorCheckbox.setSelected(enabled);
  }
  //endregion

  //region themed color
  public String getThemedColor() {
    return ColorUtil.toHex(Objects.requireNonNull(themedColorChooser.getSelectedColor()));
  }

  private void setThemedColor(final String color) {
    themedColorChooser.setSelectedColor(ColorUtil.fromHex(color));
  }

  public boolean getIsThemedColorEnabled() {
    return themedColorCheckbox.isSelected();
  }

  private void setThemedColorEnabled(final boolean enabled) {
    themedColorCheckbox.setSelected(enabled);
  }
  //endregion

  //region big icons
  public boolean getHasCustomIconSize() {
    return customLineHeightCheckbox.isSelected();
  }

  private void setHasCustomIconSize(final boolean hasCustomIconSize) {
    customLineHeightCheckbox.setSelected(hasCustomIconSize);
  }
  //endregion

  //region custom icon size
  public Integer getCustomIconSize() {
    return (Integer) customIconSizeSpinnerModel.getValue();
  }

  private void setCustomIconSize(final Integer newSize) {
    customIconSizeSpinnerModel.setValue(newSize);
  }
  //endregion

  //region custom line height
  public boolean getHasCustomLineHeight() {
    return customLineHeightCheckbox.isSelected();
  }

  private void setHasCustomLineHeight(final boolean hasCustomLineHeight) {
    customLineHeightCheckbox.setSelected(hasCustomLineHeight);
  }

  public Integer getCustomLineHeight() {
    return (Integer) customLineHeightSpinnerModel.getValue();
  }

  private void setCustomLineHeight(final Integer newSize) {
    customLineHeightSpinnerModel.setValue(newSize);
  }
  //endregion

  //region Low power mode
  public boolean isLowPowerMode() {
    return lowPowerSwitch.isSelected();
  }

  private void setLowPowerMode(final boolean lowPowerMode) {
    lowPowerSwitch.setSelected(lowPowerMode);
  }
  //endregion

  private void initializeComboboxes() {
    arrowsStyleComboBox.setModel(new DefaultComboBoxModel<>(ArrowsStyles.values()));
    arrowsStyleComboBox.setRenderer(new SimpleListCellRenderer<>() {
      @Override
      public void customize(final @NotNull JList list,
                            final ArrowsStyles value,
                            final int index,
                            final boolean selected,
                            final boolean hasFocus) {
        final Icon baseIcon;
        if (value == null) {
          return;
        }
        baseIcon = value.getIcon();
        setIcon(baseIcon);
      }
    });
  }

  private void accentColorCheckboxActionPerformed(final ActionEvent e) {
    accentColorChooser.setEnabled(accentColorCheckbox.isSelected());
  }

}
