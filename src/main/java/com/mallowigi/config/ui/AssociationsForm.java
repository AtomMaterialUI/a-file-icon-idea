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

package com.mallowigi.config.ui;

import com.intellij.util.ui.table.IconTableCellRenderer;
import com.mallowigi.config.associations.AtomAssocConfig;
import com.mallowigi.icons.FileIconProvider;
import com.mallowigi.icons.associations.RegexAssociation;
import icons.MTIcons;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Elior Boukhobza
 */
@SuppressWarnings({"UndesirableClassUsage",
                    "PackageVisibleField",
                    "WeakerAccess",
                    "FieldCanBeLocal",
                    "LocalCanBeFinal",
                    "ClassWithTooManyFields",
                    "OverlyLongMethod"
                  })
public class AssociationsForm extends JPanel implements SettingsFormUI {

  public AssociationsForm() {
    fileAssociations = FileIconProvider.getAssociations().getAssociations();
    folderAssociations = FileIconProvider.getDirAssociations().getAssociations();
    customFileAssociations = AtomAssocConfig.Companion.getInstance().getCustomFileAssociations();
    customFolderAssociations = AtomAssocConfig.Companion.getInstance().getCustomFolderAssociations();

    initComponents();
  }

  @Override
  public void init() {

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

  }

  public void setFormState(final AtomAssocConfig config) {
    afterStateSet();
  }

  public final boolean isModified(@Nullable final AtomAssocConfig config) {
    if (config == null) {
      return false;
    }

    boolean modified = config.isFileIconsModified(customFileAssociations);
    modified = modified || config.isFolderIconsModified(customFolderAssociations);
    return modified;
  }

  @NotNull
  public List<RegexAssociation> getFileAssociations() {
    return Collections.unmodifiableList(customFileAssociations);
  }

  @NotNull
  public List<RegexAssociation> getFolderAssociations() {
    return Collections.unmodifiableList(customFolderAssociations);
  }

  @SuppressWarnings("SerializableNonStaticInnerClassWithoutSerialVersionUID")
  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    ResourceBundle bundle = ResourceBundle.getBundle("config.AtomFileIconsBundle"); //NON-NLS
    tabbedContainer = new JTabbedPane();
    customFileIcons = new JPanel();
    customFileIconsScrollpane = new JScrollPane();
    customFileIconsTable = new JTable();
    fileIcons = new JPanel();
    fileIconsScrollpane = new JScrollPane();
    fileIconsTable = new JTable();
    customFolderIcons = new JPanel();
    customFolderIconsScrollpane = new JScrollPane();
    customFolderIconsTable = new JTable();
    folderIcons = new JPanel();
    folderIconsScrollpane = new JScrollPane();
    folderIconsTable = new JTable();

    //======== this ========
    setLayout(new MigLayout(
      "hidemode 3", //NON-NLS
      // columns
      "[585,grow,fill]", //NON-NLS
      // rows
      "[]")); //NON-NLS

    //======== tabbedContainer ========
    {

      //======== customFileIcons ========
      {
        customFileIcons.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[]0")); //NON-NLS

        //======== customFileIconsScrollpane ========
        {

          //---- customFileIconsTable ----
          customFileIconsTable.setModel(new DefaultTableModel(
            new Object[][]{
              {"",
                null,
                ""},
              //NON-NLS
              {null,
                null,
                null},
            },
            new String[]{
              "Name",
              "Pattern",
              "Icon"
              //NON-NLS
            }
          ) {
            final Class<?>[] columnTypes = new Class<?>[]{
              String.class,
              Object.class,
              Object.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
              return columnTypes[columnIndex];
            }
          });
          customFileIconsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
          customFileIconsScrollpane.setViewportView(customFileIconsTable);
        }
        customFileIcons.add(customFileIconsScrollpane, "cell 0 0,growx"); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.customFileIcons.tab.title"), customFileIcons); //NON-NLS

      //======== fileIcons ========
      {
        fileIcons.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[]0")); //NON-NLS

        //======== fileIconsScrollpane ========
        {

          //---- fileIconsTable ----
          fileIconsTable.setModel(new DefaultTableModel(
            new Object[][]{
              {"",
                null,
                ""},
              //NON-NLS
              {null,
                null,
                null},
            },
            new String[]{
              "Name",
              "Pattern",
              "Icon"
              //NON-NLS
            }
          ) {
            final Class<?>[] columnTypes = new Class<?>[]{
              String.class,
              Object.class,
              Object.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
              return columnTypes[columnIndex];
            }
          });
          fileIconsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
          fileIconsScrollpane.setViewportView(fileIconsTable);
        }
        fileIcons.add(fileIconsScrollpane, "cell 0 0,growx"); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.fileIcons.tab.title"), fileIcons); //NON-NLS

      //======== customFolderIcons ========
      {
        customFolderIcons.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[]0")); //NON-NLS

        //======== customFolderIconsScrollpane ========
        {

          //---- customFolderIconsTable ----
          customFolderIconsTable.setModel(new DefaultTableModel(
            new Object[][]{
              {"",
                null,
                ""},
              //NON-NLS
              {null,
                null,
                null},
            },
            new String[]{
              "Name",
              "Pattern",
              "Icon"
              //NON-NLS
            }
          ) {
            final Class<?>[] columnTypes = new Class<?>[]{
              String.class,
              Object.class,
              Object.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
              return columnTypes[columnIndex];
            }
          });
          customFolderIconsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
          customFolderIconsScrollpane.setViewportView(customFolderIconsTable);
        }
        customFolderIcons.add(customFolderIconsScrollpane, "cell 0 0,growx"); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.customFolderIcons.tab.title"), customFolderIcons); //NON-NLS

      //======== folderIcons ========
      {
        folderIcons.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[]0")); //NON-NLS

        //======== folderIconsScrollpane ========
        {

          //---- folderIconsTable ----
          folderIconsTable.setModel(new DefaultTableModel(
            new Object[][]{
              {"",
                null,
                ""},
              //NON-NLS
              {null,
                null,
                null},
            },
            new String[]{
              "Name",
              "Pattern",
              "Icon"
              //NON-NLS
            }
          ) {
            final Class<?>[] columnTypes = new Class<?>[]{
              String.class,
              Object.class,
              Object.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
              return columnTypes[columnIndex];
            }
          });
          folderIconsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
          folderIconsScrollpane.setViewportView(folderIconsTable);
        }
        folderIcons.add(folderIconsScrollpane, "cell 0 0,growx"); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.folderIcons.tab.title"), folderIcons); //NON-NLS
    }
    add(tabbedContainer, "cell 0 0"); //NON-NLS

    //---- bindings ----
    bindingGroup = new BindingGroup();
    {
      JTableBinding binding = SwingBindings.createJTableBinding(UpdateStrategy.READ,
                                                                fileAssociations, fileIconsTable);
      binding.addColumnBinding(BeanProperty.create("name")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.fileIconsTable.columnName_3")) //NON-NLS
        .setColumnClass(String.class)
        .setEditable(false);
      binding.addColumnBinding(BeanProperty.create("icon")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.fileIconsTable.columnName.1")) //NON-NLS
        .setColumnClass(String.class)
        .setEditable(false);
      binding.addColumnBinding(ELProperty.create("${matcher}")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.fileIconsTable.columnName_2")) //NON-NLS
        .setColumnClass(String.class)
        .setEditable(false);
      bindingGroup.addBinding(binding);
    }
    {
      JTableBinding binding = SwingBindings.createJTableBinding(UpdateStrategy.READ,
                                                                folderAssociations, folderIconsTable);
      binding.addColumnBinding(BeanProperty.create("icon")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.folderIconsTable.columnName.1")) //NON-NLS
        .setColumnClass(String.class);
      binding.addColumnBinding(BeanProperty.create("matcher")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.folderIconsTable.columnName_2")) //NON-NLS
        .setColumnClass(String.class);
      binding.addColumnBinding(BeanProperty.create("name")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.folderIconsTable.columnName_3")) //NON-NLS
        .setColumnClass(String.class);
      bindingGroup.addBinding(binding);
    }
    {
      JTableBinding binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
                                                                customFileAssociations, customFileIconsTable);
      binding.addColumnBinding(BeanProperty.create("icon")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.customFileIconsTable.columnName.1")) //NON-NLS
        .setColumnClass(String.class);
      binding.addColumnBinding(BeanProperty.create("matcher")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.customFileIconsTable.columnName_2")) //NON-NLS
        .setColumnClass(String.class);
      binding.addColumnBinding(BeanProperty.create("name")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.customFileIconsTable.columnName_3")) //NON-NLS
        .setColumnClass(String.class);
      bindingGroup.addBinding(binding);
    }
    {
      JTableBinding binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
                                                                customFolderAssociations, customFolderIconsTable);
      binding.addColumnBinding(BeanProperty.create("icon")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.customFolderIconsTable.columnName.1")) //NON-NLS
        .setColumnClass(String.class);
      binding.addColumnBinding(BeanProperty.create("matcher")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.customFolderIconsTable.columnName_2")) //NON-NLS
        .setColumnClass(String.class);
      binding.addColumnBinding(BeanProperty.create("name")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.customFolderIconsTable.columnName_3")) //NON-NLS
        .setColumnClass(String.class);
      bindingGroup.addBinding(binding);
    }
    bindingGroup.bind();
    // JFormDesigner - End of component initialization  //GEN-END:initComponents

    fileIconsTable.getColumnModel().getColumn(1).setCellRenderer(new IconTableCellRenderer<String>() {
      @NotNull
      @Override
      protected Icon getIcon(@NotNull final String value, final JTable table, final int row) {
        return MTIcons.getFileIcon(value);
      }
    });
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JTabbedPane tabbedContainer;
  private JPanel customFileIcons;
  private JScrollPane customFileIconsScrollpane;
  private JTable customFileIconsTable;
  private JPanel fileIcons;
  private JScrollPane fileIconsScrollpane;
  private JTable fileIconsTable;
  private JPanel customFolderIcons;
  private JScrollPane customFolderIconsScrollpane;
  private JTable customFolderIconsTable;
  private JPanel folderIcons;
  private JScrollPane folderIconsScrollpane;
  private JTable folderIconsTable;
  private final List<com.mallowigi.icons.associations.Association> fileAssociations;
  private final List<com.mallowigi.icons.associations.Association> folderAssociations;
  private final List<com.mallowigi.icons.associations.RegexAssociation> customFileAssociations;
  private final List<com.mallowigi.icons.associations.RegexAssociation> customFolderAssociations;
  private BindingGroup bindingGroup;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
}
