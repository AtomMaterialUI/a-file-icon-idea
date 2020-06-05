/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2020 Elior "Mallowigi" Boukhobza
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

/*
 * Created by JFormDesigner on Sat May 02 14:11:13 IDT 2020
 */

package com.mallowigi.config.associations.ui;

import com.intellij.openapi.Disposable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.table.TableModelEditor;
import com.mallowigi.config.AtomSettingsBundle;
import com.mallowigi.config.associations.AtomAssocConfig;
import com.mallowigi.config.associations.ui.columns.IconEditableColumnInfo;
import com.mallowigi.config.associations.ui.columns.NameEditableColumnInfo;
import com.mallowigi.config.associations.ui.columns.PatternEditableColumnInfo;
import com.mallowigi.config.associations.ui.internal.AssociationsTableItemEditor;
import com.mallowigi.config.ui.SettingsFormUI;
import com.mallowigi.icons.associations.CustomAssociations;
import com.mallowigi.icons.associations.RegexAssociation;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.util.Collections;
import java.util.ResourceBundle;

@SuppressWarnings({"UndesirableClassUsage",
                    "FieldCanBeLocal",
                    "LocalCanBeFinal",
                    "PublicMethodNotExposedInInterface",
                    "ClassWithTooManyFields",
                    "OverlyLongMethod"})
public final class AssociationsForm extends JPanel implements SettingsFormUI, Disposable {

  private final ColumnInfo[] fileColumns = {
    new NameEditableColumnInfo(this),
    new PatternEditableColumnInfo(this),
    new IconEditableColumnInfo(this)};

  private final ColumnInfo[] folderColumns = {
    new NameEditableColumnInfo(this),
    new PatternEditableColumnInfo(this),
    new IconEditableColumnInfo(this)};

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JTabbedPane tabbedContainer;
  private JPanel customFileIconsNew;
  private JTextPane explanation;
  private JPanel customFolderIconsNew;
  private JPanel defaultFileIcons;
  private JPanel defFileIconsPanel;
  private JScrollPane fileIconsScrollPane;
  private FileAssociationsTable defaultFileIconsTable;
  private JPanel defaultFolderIcons;
  private JPanel defFolderIconsPanel;
  private JScrollPane folderIconsScrollpane;
  private FolderAssociationsTable defaultFolderIconsTable;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private JComponent customFileIconsTable;
  private JComponent customFolderIconsTable;
  private @Nullable TableModelEditor<RegexAssociation> customFileAssociationsEditor;
  private @Nullable TableModelEditor<RegexAssociation> customFolderAssociationsEditor;

  public AssociationsForm() {

  }

  @Override
  public void init() {
    initComponents();
    createCustomTables();
  }

  @Override
  public JComponent getContent() {
    return tabbedContainer;
  }

  @Override
  public void afterStateSet() {

  }

  @Override
  public void dispose() {
    customFileAssociationsEditor = null;
    customFolderAssociationsEditor = null;
  }

  public void setFormState(final AtomAssocConfig config) {
    customFileAssociationsEditor.reset(config.getCustomFileAssociations().getTheAssociations());
    customFolderAssociationsEditor.reset(config.getCustomFolderAssociations().getTheAssociations());
    afterStateSet();
  }

  public boolean isModified(@Nullable final AtomAssocConfig config) {
    if (config == null) {
      return false;
    }

    boolean modified = config.isFileIconsModified(customFileAssociationsEditor.getModel().getItems());
    modified = modified || config.isFolderIconsModified(customFolderAssociationsEditor.getModel().getItems());
    return modified;
  }

  public CustomAssociations getFileAssociations() {
    return new CustomAssociations(Collections.unmodifiableList(customFileAssociationsEditor.getModel().getItems()));
  }

  public CustomAssociations getFolderAssociations() {
    return new CustomAssociations(Collections.unmodifiableList(customFolderAssociationsEditor.getModel().getItems()));
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    ResourceBundle bundle = ResourceBundle.getBundle("config.AtomFileIconsBundle"); //NON-NLS
    tabbedContainer = new JTabbedPane();
    customFileIconsNew = new JPanel();
    explanation = new JTextPane();
    customFolderIconsNew = new JPanel();
    defaultFileIcons = new JPanel();
    defFileIconsPanel = new JPanel();
    fileIconsScrollPane = new JScrollPane();
    defaultFileIconsTable = new FileAssociationsTable();
    defaultFolderIcons = new JPanel();
    defFolderIconsPanel = new JPanel();
    folderIconsScrollpane = new JScrollPane();
    defaultFolderIconsTable = new FolderAssociationsTable();

    //======== this ========
    setLayout(new MigLayout(
      "hidemode 3", //NON-NLS
      // columns
      "[585,grow,fill]", //NON-NLS
      // rows
      "[183,fill]")); //NON-NLS

    //======== tabbedContainer ========
    {

      //======== customFileIconsNew ========
      {
        customFileIconsNew.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[260,grow,fill]" + //NON-NLS
            "[]")); //NON-NLS

        //---- explanation ----
        explanation.setText(bundle.getString("AssociationsForm.explanation.text")); //NON-NLS
        explanation.setForeground(UIManager.getColor("MenuItem.acceleratorForeground")); //NON-NLS
        customFileIconsNew.add(explanation, "cell 0 1"); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.customFileIconsNew.tab.title"), customFileIconsNew); //NON-NLS

      //======== customFolderIconsNew ========
      {
        customFolderIconsNew.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[260,grow,fill]")); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.customFolderIconsNew.title"), customFolderIconsNew); //NON-NLS

      //======== defaultFileIcons ========
      {
        defaultFileIcons.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[grow,fill]")); //NON-NLS

        //======== defFileIconsPanel ========
        {
          defFileIconsPanel.setBorder(null);
          defFileIconsPanel.setLayout(new MigLayout(
            "hidemode 3", //NON-NLS
            // columns
            "0[grow,fill]0", //NON-NLS
            // rows
            "0[grow,fill]0")); //NON-NLS

          //======== fileIconsScrollPane ========
          {
            fileIconsScrollPane.setBorder(new EtchedBorder());
            fileIconsScrollPane.setViewportBorder(null);

            //---- defaultFileIconsTable ----
            defaultFileIconsTable.setBorder(null);
            defaultFileIconsTable.setShowHorizontalLines(false);
            defaultFileIconsTable.setShowVerticalLines(false);
            defaultFileIconsTable.setRowSelectionAllowed(false);
            defaultFileIconsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            defaultFileIconsTable.setFocusable(false);
            defaultFileIconsTable.setRequestFocusEnabled(false);
            defaultFileIconsTable.setRowHeight(22);
            defaultFileIconsTable.setCellSelectionEnabled(true);
            defaultFileIconsTable.setStriped(true);
            fileIconsScrollPane.setViewportView(defaultFileIconsTable);
          }
          defFileIconsPanel.add(fileIconsScrollPane, "cell 0 0"); //NON-NLS
        }
        defaultFileIcons.add(defFileIconsPanel, "cell 0 0"); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.defaultFileIcons.tab.title"), defaultFileIcons); //NON-NLS

      //======== defaultFolderIcons ========
      {
        defaultFolderIcons.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[grow,fill]")); //NON-NLS

        //======== defFolderIconsPanel ========
        {
          defFolderIconsPanel.setBorder(null);
          defFolderIconsPanel.setLayout(new MigLayout(
            "hidemode 3", //NON-NLS
            // columns
            "0[grow,fill]0", //NON-NLS
            // rows
            "0[grow,fill]0")); //NON-NLS

          //======== folderIconsScrollpane ========
          {
            folderIconsScrollpane.setBorder(new EtchedBorder());
            folderIconsScrollpane.setViewportBorder(null);

            //---- defaultFolderIconsTable ----
            defaultFolderIconsTable.setBorder(null);
            defaultFolderIconsTable.setShowHorizontalLines(false);
            defaultFolderIconsTable.setShowVerticalLines(false);
            defaultFolderIconsTable.setRowSelectionAllowed(false);
            defaultFolderIconsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            defaultFolderIconsTable.setFocusable(false);
            defaultFolderIconsTable.setRequestFocusEnabled(false);
            defaultFolderIconsTable.setRowHeight(22);
            defaultFolderIconsTable.setCellSelectionEnabled(true);
            defaultFolderIconsTable.setStriped(true);
            folderIconsScrollpane.setViewportView(defaultFolderIconsTable);
          }
          defFolderIconsPanel.add(folderIconsScrollpane, "cell 0 0"); //NON-NLS
        }
        defaultFolderIcons.add(defFolderIconsPanel, "cell 0 0"); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.defaultFolderIcons.tab.title"), defaultFolderIcons); //NON-NLS
    }
    add(tabbedContainer, "cell 0 0"); //NON-NLS
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  /**
   * Create the custom tables
   */
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
    customFileIconsNew.add(customFileIconsTable, "cell 0 0"); //NON-NLS
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
    customFolderIconsNew.add(customFolderIconsTable, "cell 0 0"); //NON-NLS
  }

}
