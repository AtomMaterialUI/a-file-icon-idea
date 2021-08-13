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

/*
 * Created by JFormDesigner on Fri Aug 13 16:21:48 IDT 2021
 */

package com.mallowigi.config.select;

import com.intellij.openapi.Disposable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.table.TableModelEditor;
import com.mallowigi.config.AtomSettingsBundle;
import com.mallowigi.config.associations.ui.columns.EnabledColumnInfo;
import com.mallowigi.config.associations.ui.columns.IconEditableColumnInfo;
import com.mallowigi.config.associations.ui.columns.NameEditableColumnInfo;
import com.mallowigi.config.associations.ui.columns.PatternEditableColumnInfo;
import com.mallowigi.config.associations.ui.internal.AssociationsTableItemEditor;
import com.mallowigi.config.ui.SettingsFormUI;
import com.mallowigi.icons.associations.RegexAssociation;
import com.mallowigi.icons.associations.SelectedAssociations;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * @author Elior Boukhobza
 */
@SuppressWarnings({"FieldCanBeLocal",
  "DuplicateStringLiteralInspection",
  "StringConcatenation",
  "UndesirableClassUsage",
  "InstanceVariableMayNotBeInitialized",
  "TransientFieldNotInitialized"})
public final class AtomSelectForm extends JPanel implements SettingsFormUI, Disposable {

  private final transient ColumnInfo[] fileColumns = {
    new NameEditableColumnInfo(this, false),
    new PatternEditableColumnInfo(this, true),
    new IconEditableColumnInfo(this, true),
    new EnabledColumnInfo()
  };

  private final transient ColumnInfo[] folderColumns = {
    new NameEditableColumnInfo(this, true),
    new PatternEditableColumnInfo(this, true),
    new IconEditableColumnInfo(this, true),
    new EnabledColumnInfo()
  };
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JLabel explanation;
  private JTabbedPane tabbedPane;
  private JPanel fileAssociationsPanel;
  private JPanel folderAssociationsPanel;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private JComponent customFileIconsTable;
  private JComponent customFolderIconsTable;
  private @Nullable TableModelEditor<RegexAssociation> customFileAssociationsEditor;
  private @Nullable TableModelEditor<RegexAssociation> customFolderAssociationsEditor;

  @Override
  public void init() {
    initComponents();
    createCustomTables();
  }

  @Override
  public JComponent getContent() {
    return this;
  }

  @Override
  public void afterStateSet() {
    //    customFileAssociationsEditor = null;
    //    customFolderAssociationsEditor = null;
  }

  @Override
  public void dispose() {
    //empty
  }

  public void setFormState(final AtomSelectConfig config) {
    if (customFileAssociationsEditor != null) {
      customFileAssociationsEditor.reset(config.getSelectedFileAssociations().values());
    }
    if (customFolderAssociationsEditor != null) {
      customFolderAssociationsEditor.reset(config.getSelectedFolderAssociations().values());
    }
    afterStateSet();
  }

  @SuppressWarnings({"SimplifiableIfStatement",
    "DuplicatedCode"})
  public boolean isModified(@Nullable final AtomSelectConfig config) {
    if (config == null) {
      return false;
    }

    boolean modified = false;
    if (customFileAssociationsEditor != null) {
      modified = config.isFileIconsModified(customFileAssociationsEditor.getModel().getItems());
    }
    if (customFolderAssociationsEditor != null) {
      modified = modified || config.isFolderIconsModified(customFolderAssociationsEditor.getModel().getItems());
    }
    return modified;
  }

  public SelectedAssociations getFileAssociations() {
    assert customFileAssociationsEditor != null;
    return new SelectedAssociations(customFileAssociationsEditor.getModel().getItems());
  }

  public SelectedAssociations getFolderAssociations() {
    assert customFolderAssociationsEditor != null;
    return new SelectedAssociations(customFolderAssociationsEditor.getModel().getItems());
  }

  @SuppressWarnings("ConfusingFloatingPointLiteral")
  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    explanation = new JLabel();
    tabbedPane = new JTabbedPane();
    fileAssociationsPanel = new JPanel();
    folderAssociationsPanel = new JPanel();

    //======== this ========
    setBorder(new TitledBorder(null, "Associations Editor", TitledBorder.CENTER, TitledBorder.TOP));
    setLayout(new MigLayout(
      "hidemode 3",
      // columns
      "[369,grow,fill]",
      // rows
      "[]" +
        "[270,grow,fill]" +
        "[]"));

    //---- explanation ----
    explanation.setText("Use the following tables to disable unwanted associations");
    explanation.setFont(explanation.getFont().deriveFont(explanation.getFont().getSize() - 1f));
    explanation.setForeground(UIManager.getColor("inactiveCaptionText"));
    add(explanation, "cell 0 0");

    //======== tabbedPane ========
    {

      //======== fileAssociationsPanel ========
      {
        fileAssociationsPanel.setLayout(new MigLayout(
          "hidemode 3",
          // columns
          "0[grow,fill]0",
          // rows
          "0[grow,fill]0"));
      }
      tabbedPane.addTab("File Associations", fileAssociationsPanel);

      //======== folderAssociationsPanel ========
      {
        folderAssociationsPanel.setLayout(new MigLayout(
          "hidemode 3",
          // columns
          "[grow,fill]",
          // rows
          "[grow]"));
      }
      tabbedPane.addTab("Folder Associations", folderAssociationsPanel);
    }
    add(tabbedPane, "cell 0 1");
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  private void createCustomTables() {
    createCustomFileIconsTable();
    createCustomFolderIconsTable();
  }

  /**
   * Create the custom file icons
   */
  private void createCustomFileIconsTable() {
    final AssociationsTableItemEditor itemEditor = new AssociationsTableItemEditor();
    customFileAssociationsEditor = new TableModelEditor<>(fileColumns,
      itemEditor,
      AtomSettingsBundle.message("no.custom.file.associations"));
    customFileIconsTable = customFileAssociationsEditor.createComponent();
    fileAssociationsPanel.add(customFileIconsTable, "cell 0 0"); //NON-NLS

  }

  /**
   * Create the custom folder icons
   */
  private void createCustomFolderIconsTable() {
    final AssociationsTableItemEditor itemEditor = new AssociationsTableItemEditor();
    customFolderAssociationsEditor = new TableModelEditor<>(folderColumns,
      itemEditor,
      AtomSettingsBundle.message("no.custom.folder.associations"));
    customFolderIconsTable = customFolderAssociationsEditor.createComponent();
    folderAssociationsPanel.add(customFolderIconsTable, "cell 0 0"); //NON-NLS
  }
}
