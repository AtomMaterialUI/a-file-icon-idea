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
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.util.BackgroundTaskUtil;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.table.TableModelEditor;
import com.mallowigi.config.AtomSettingsBundle;
import com.mallowigi.config.associations.AtomAssocConfig;
import com.mallowigi.config.associations.ui.columns.FileIconEditableColumnInfo;
import com.mallowigi.config.associations.ui.columns.FolderIconEditableColumnInfo;
import com.mallowigi.config.associations.ui.columns.NameEditableColumnInfo;
import com.mallowigi.config.associations.ui.columns.PatternEditableColumnInfo;
import com.mallowigi.config.associations.ui.internal.AssociationsTableDataChangedListener;
import com.mallowigi.config.associations.ui.internal.AssociationsTableItemEditor;
import com.mallowigi.config.ui.SettingsFormUI;
import com.mallowigi.icons.FileIconProvider;
import com.mallowigi.icons.associations.Association;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings({"UndesirableClassUsage",
                    "FieldCanBeLocal",
                    "LocalCanBeFinal",
                    "PublicMethodNotExposedInInterface"
                    ,
                    "ClassWithTooManyFields"})
public class AssociationsForm extends JPanel implements SettingsFormUI, Disposable {
  private JBLoadingPanel myLoadingPanel;
  private static final FileIconEditableColumnInfo FILE_ICON_COLUMN = new FileIconEditableColumnInfo();
  private static final FolderIconEditableColumnInfo FOLDER_ICON_COLUMN = new FolderIconEditableColumnInfo();

  private static final ColumnInfo[] FILE_COLUMNS = {
    new NameEditableColumnInfo(),
    new PatternEditableColumnInfo(),
    FILE_ICON_COLUMN};

  private static final ColumnInfo[] FOLDER_COLUMNS = {
    new NameEditableColumnInfo(),
    new PatternEditableColumnInfo(),
    FOLDER_ICON_COLUMN};

  public AssociationsForm() {
    initComponents();
    createCustomTables();

    myLoadingPanel = new JBLoadingPanel(new BorderLayout(), this, 300 * 2); // don't start loading automatically
    myLoadingPanel.add(tabbedContainer);
    add(myLoadingPanel, "cell 0 0");
    myLoadingPanel.startLoading();

    BackgroundTaskUtil.executeAndTryWait(indicator -> () -> ApplicationManager.getApplication().invokeAndWait(() -> {
      defaultFileAssociationsEditor.reset(FileIconProvider.getAssociations().getAssociations());
      defaultFolderAssociationsEditor.reset(FileIconProvider.getDirAssociations().getAssociations());
      myLoadingPanel.stopLoading();
    }), () -> myLoadingPanel.startLoading(), 300, false);

  }

  @Override
  public void init() {

  }

  @Override
  public JComponent getContent() {
    return myLoadingPanel;
  }

  @Override
  public void afterStateSet() {

  }

  @Override
  public void dispose() {

  }

  public void setFormState(final AtomAssocConfig config) {
    afterStateSet();
  }

  public final boolean isModified(@Nullable final AtomAssocConfig config) {
    if (config == null) {
      return false;
    }

    //    boolean modified = config.isFileIconsModified(customFileAssociations);
    //    modified = modified || config.isFolderIconsModified(customFolderAssociations);
    return false;
  }

  @NotNull
  public List<Association> getFileAssociations() {
    return Collections.unmodifiableList(customFileAssociationsEditor.getModel().getItems());
  }

  @NotNull
  public List<Association> getFolderAssociations() {
    return Collections.unmodifiableList(customFileAssociationsEditor.getModel().getItems());
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    ResourceBundle bundle = ResourceBundle.getBundle("config.AtomFileIconsBundle"); //NON-NLS
    tabbedContainer = new JTabbedPane();
    customFileIconsNew = new JPanel();
    customFolderIconsNew = new JPanel();
    fileIcons = new JPanel();
    folderIcons = new JPanel();

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
          "[260,grow,fill]")); //NON-NLS
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

      //======== fileIcons ========
      {
        fileIcons.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[260,grow,fill]")); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.fileIcons.title"), fileIcons); //NON-NLS

      //======== folderIcons ========
      {
        folderIcons.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[260,grow,fill]")); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.folderIcons.title"), folderIcons); //NON-NLS
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
    createDefaultFileIconsTable();
    createDefaultFolderIconsTable();
  }

  /**
   * Create the custom file icons
   */
  private void createCustomFileIconsTable() {
    final AssociationsTableItemEditor itemEditor = new AssociationsTableItemEditor();
    customFileAssociationsEditor = new TableModelEditor<>(FILE_COLUMNS,
                                                          itemEditor,
                                                          AtomSettingsBundle.message("no.custom.file.associations"))
      .modelListener(new AssociationsTableDataChangedListener());
    customFileIconsTable = customFileAssociationsEditor.createComponent();
    customFileIconsNew.add(customFileIconsTable, "cell 0 0"); //NON-NLS
  }

  /**
   * Create the custom folder icons
   */
  private void createCustomFolderIconsTable() {
    final AssociationsTableItemEditor itemEditor = new AssociationsTableItemEditor();
    customFolderAssociationsEditor = new TableModelEditor<>(FOLDER_COLUMNS,
                                                            itemEditor,
                                                            AtomSettingsBundle.message("no.custom.folder.associations"))
      .modelListener(new AssociationsTableDataChangedListener());
    customFolderIconsTable = customFolderAssociationsEditor.createComponent();
    customFolderIconsNew.add(customFolderIconsTable, "cell 0 0"); //NON-NLS
  }

  /**
   * Create the default file icons
   */
  private void createDefaultFileIconsTable() {
    final AssociationsTableItemEditor itemEditor = new AssociationsTableItemEditor();
    defaultFileAssociationsEditor = new TableModelEditor<>(FILE_COLUMNS,
                                                           itemEditor,
                                                           AtomSettingsBundle.message("no.default.file.associations"));
    defaultFileAssociationsEditor.enabled(false);
    defaultFileIconsTable = defaultFileAssociationsEditor.createComponent();
    fileIcons.add(defaultFileIconsTable, "cell 0 0"); //NON-NLS
  }

  /**
   * Create the default folder icons
   */
  private void createDefaultFolderIconsTable() {
    final AssociationsTableItemEditor itemEditor = new AssociationsTableItemEditor();
    defaultFolderAssociationsEditor = new TableModelEditor<>(FOLDER_COLUMNS,
                                                             itemEditor,
                                                             AtomSettingsBundle.message("no.default.folder.associations"));
    defaultFolderAssociationsEditor.enabled(false);
    defaultFolderIconsTable = defaultFolderAssociationsEditor.createComponent();
    folderIcons.add(defaultFolderIconsTable, "cell 0 0"); //NON-NLS
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JTabbedPane tabbedContainer;
  private JPanel customFileIconsNew;
  private JPanel customFolderIconsNew;
  private JPanel fileIcons;
  private JPanel folderIcons;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private JComponent customFileIconsTable;
  private JComponent customFolderIconsTable;
  private JComponent defaultFileIconsTable;
  private JComponent defaultFolderIconsTable;

  private TableModelEditor<Association> customFileAssociationsEditor;
  private TableModelEditor<Association> customFolderAssociationsEditor;
  private TableModelEditor<Association> defaultFileAssociationsEditor;
  private TableModelEditor<Association> defaultFolderAssociationsEditor;

}
