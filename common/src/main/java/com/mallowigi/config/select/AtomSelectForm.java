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

package com.mallowigi.config.select;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.util.ui.ColumnInfo;
import com.mallowigi.config.AtomSettingsBundle;
import com.mallowigi.config.associations.ui.columns.*;
import com.mallowigi.config.associations.ui.internal.AssociationsTableItemEditor;
import com.mallowigi.config.associations.ui.internal.AssociationsTableModelEditor;
import com.mallowigi.config.ui.SettingsFormUI;
import com.mallowigi.icons.associations.SelectedAssociations;
import com.mallowigi.models.IconType;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

@SuppressWarnings("ALL")
public final class AtomSelectForm extends JPanel implements SettingsFormUI, Disposable {

  private final transient ColumnInfo[] fileColumns = {
    new EnabledColumnInfo(),
    new NameEditableColumnInfo(this, true),
    new PatternEditableColumnInfo(this, true),
    new FileIconEditableColumnInfo(this, true),
    new PriorityColumnInfo(this, true),
    new IconColorEditableColumnInfo(this, true),
    new FolderColorEditableColumnInfo(this, true),
    new FolderIconColorEditableColumnInfo(this, true),
    new TouchedColumnInfo(),
  };

  private final transient ColumnInfo[] folderColumns = {
    new EnabledColumnInfo(),
    new NameEditableColumnInfo(this, true),
    new PatternEditableColumnInfo(this, true),
    new FolderIconEditableColumnInfo(this, true),
    new PriorityColumnInfo(this, true),
    new IconColorEditableColumnInfo(this, true),
    new FolderColorEditableColumnInfo(this, true),
    new FolderIconColorEditableColumnInfo(this, true),
    new TouchedColumnInfo(),
  };
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JLabel explanation;
  private JLabel customExplanation2;
  private LinkLabel link;
  private JTabbedPane tabbedPane;
  private JPanel fileAssociationsPanel;
  private SearchTextField fileSearch;
  private JPanel folderAssociationsPanel;
  private SearchTextField folderSearch;
  private JTextPane explanation2;
  private JButton resetButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private JComponent fileIconsTable;
  private JComponent folderIconsTable;
  private @Nullable AssociationsTableModelEditor fileAssociationsEditor;
  private @Nullable AssociationsTableModelEditor folderAssociationsEditor;

  @Override
  public void init() {
    initComponents();
    createTables();
    fileSearch.getTextEditor().getEmptyText().setText(AtomSettingsBundle.message("fileSearch.placeholder"));
    folderSearch.getTextEditor().getEmptyText().setText(AtomSettingsBundle.message("fileSearch.placeholder"));
  }

  @Override
  public JComponent getContent() {
    return this;
  }

  @Override
  public void afterStateSet() {
    // add after state set
  }

  @Override
  public void dispose() {
    fileAssociationsEditor = null;
    folderAssociationsEditor = null;
  }

  public void setFormState(final AtomSelectConfig config) {
    ApplicationManager.getApplication().invokeLater(() -> {
      if (fileAssociationsEditor != null) {
        fileAssociationsEditor.reset(config.getSelectedFileAssociations().getTheAssociations());
      }
      if (folderAssociationsEditor != null) {
        folderAssociationsEditor.reset(config.getSelectedFolderAssociations().getTheAssociations());
      }
      afterStateSet();
    });
  }

  public boolean isModified(final AtomSelectConfig config) {
    boolean modified = false;
    if (fileAssociationsEditor != null) {
      modified = config.isFileIconsModified(fileAssociationsEditor.getModel().getItems());
    }
    if (folderAssociationsEditor != null) {
      modified = modified || config.isFolderIconsModified(folderAssociationsEditor.getModel().getItems());
    }
    return modified;
  }

  public SelectedAssociations getFileAssociations() {
    assert fileAssociationsEditor != null;
    return new SelectedAssociations(IconType.FILE, fileAssociationsEditor.getModel().getAllItems());
  }

  public SelectedAssociations getFolderAssociations() {
    assert folderAssociationsEditor != null;
    return new SelectedAssociations(IconType.FOLDER, folderAssociationsEditor.getModel().getAllItems());
  }

  private static void linkMouseClicked(final MouseEvent e) {
    BrowserUtil.browse(AtomSettingsBundle.message("SelectForm.link.text"));
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    final ResourceBundle bundle = ResourceBundle.getBundle("messages.AtomFileIconsBundle");
    explanation = new JLabel();
    customExplanation2 = new JLabel();
    link = new LinkLabel();
    tabbedPane = new JTabbedPane();
    fileAssociationsPanel = new JPanel();
    fileSearch = new SearchTextField();
    folderAssociationsPanel = new JPanel();
    folderSearch = new SearchTextField();
    explanation2 = new JTextPane();
    resetButton = new JButton();

    //======== this ========
    setBorder(new TitledBorder(null, "Associations Editor", TitledBorder.CENTER, TitledBorder.TOP));
    setLayout(new MigLayout(
      "hidemode 3,wrap",
      // columns
      "[grow,fill]",
      // rows
      "[]" +
        "[]" +
        "[shrink 0,fill]" +
        "[top]0" +
        "[]"));

    //---- explanation ----
    explanation.setText(bundle.getString("SelectForm.explanation.text"));
    explanation.setFont(explanation.getFont().deriveFont(explanation.getFont().getSize() - 1f));
    explanation.setForeground(UIManager.getColor("inactiveCaptionText"));
    add(explanation, "cell 0 0");

    //---- customExplanation2 ----
    customExplanation2.setText(bundle.getString("SelectForm.customExplanation2.text"));
    customExplanation2.setFont(customExplanation2.getFont().deriveFont(customExplanation2.getFont().getSize() - 1f));
    customExplanation2.setForeground(UIManager.getColor("inactiveCaptionText"));
    add(customExplanation2, "cell 0 1,alignx left,growx 0");

    //---- link ----
    link.setText(bundle.getString("SelectForm.link.text"));
    link.setFont(link.getFont().deriveFont(link.getFont().getSize() - 1f));
    link.setLabelFor(explanation);
    link.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(final MouseEvent e) {
        AtomSelectForm.linkMouseClicked(e);
      }
    });
    add(link, "cell 0 1");

    //======== tabbedPane ========
    {
      tabbedPane.setMinimumSize(null);

      //======== fileAssociationsPanel ========
      {
        fileAssociationsPanel.setMinimumSize(null);
        fileAssociationsPanel.setPreferredSize(null);
        fileAssociationsPanel.setLayout(new MigLayout(
          "aligny top",
          // columns
          "0[grow,fill]0",
          // rows
          "rel[shrink 0,fill]rel"));
        fileAssociationsPanel.add(fileSearch, "cell 0 0");
      }
      tabbedPane.addTab(bundle.getString("SelectForm.fileAssociationsPanel.tab.title"), fileAssociationsPanel);

      //======== folderAssociationsPanel ========
      {
        folderAssociationsPanel.setLayout(new MigLayout(
          "aligny top",
          // columns
          "0[grow,fill]0",
          // rows
          "rel[shrink 0,fill]rel"));
        folderAssociationsPanel.add(folderSearch, "cell 0 0");
      }
      tabbedPane.addTab(bundle.getString("SelectForm.folderAssociationsPanel.tab.title"), folderAssociationsPanel);
    }
    add(tabbedPane, "cell 0 2");

    //---- explanation2 ----
    explanation2.setText(bundle.getString("SelectForm.explanation2.text"));
    explanation2.setForeground(UIManager.getColor("inactiveCaptionText"));
    explanation2.setFont(explanation2.getFont().deriveFont(explanation2.getFont().getSize() - 1f));
    add(explanation2, "cell 0 3");

    //---- resetButton ----
    resetButton.setText(bundle.getString("SelectForm.resetButton.text"));
    add(resetButton, "cell 0 4,alignx right,growx 0");
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
    resetButton.addActionListener(this::resetButtonActionPerformed);
  }

  private void resetButtonActionPerformed(final ActionEvent e) {
    final AtomSelectConfig config = AtomSelectConfig.getInstance();

    config.reset();
    ApplicationManager.getApplication().invokeLater(() -> {
      if (fileAssociationsEditor != null) {
        fileAssociationsEditor.reset(config.getSelectedFileAssociations().getTheAssociations());
      }
      if (folderAssociationsEditor != null) {
        folderAssociationsEditor.reset(config.getSelectedFolderAssociations().getTheAssociations());
      }
      afterStateSet();
    });
  }

  private void createTables() {
    createFileIconsTable();
    createFolderIconsTable();
  }

  /**
   * Create the file icons
   */
  private void createFileIconsTable() {
    final AssociationsTableItemEditor itemEditor = new AssociationsTableItemEditor();
    fileAssociationsEditor = new AssociationsTableModelEditor(
      fileColumns,
      itemEditor,
      AtomSettingsBundle.message("no.file.associations"),
      fileSearch,
      IconType.FILE
    );
    ApplicationManager.getApplication().invokeLater(() -> {
      fileIconsTable = fileAssociationsEditor.createComponent();
      fileAssociationsPanel.add(fileIconsTable, "cell 0 1"); //NON-NLS
    });

  }

  /**
   * Create the folder icons
   */
  private void createFolderIconsTable() {
    final AssociationsTableItemEditor itemEditor = new AssociationsTableItemEditor();
    folderAssociationsEditor = new AssociationsTableModelEditor(folderColumns,
      itemEditor,
      AtomSettingsBundle.message("no.folder.associations"),
      folderSearch,
      IconType.FOLDER);
    ApplicationManager.getApplication().invokeLater(() -> {
      folderIconsTable = folderAssociationsEditor.createComponent();
      folderAssociationsPanel.add(folderIconsTable, "cell 0 1"); //NON-NLS
    });
  }
}
