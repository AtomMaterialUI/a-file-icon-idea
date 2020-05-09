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

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.util.Function;
import com.intellij.util.PathUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.LocalPathCellEditor;
import com.intellij.util.ui.table.IconTableCellRenderer;
import com.intellij.util.ui.table.TableModelEditor;
import com.mallowigi.config.associations.AtomAssocConfig;
import com.mallowigi.config.ui.SettingsFormUI;
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
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings({"UndesirableClassUsage",
                    "PackageVisibleField",
                    "WeakerAccess",
                    "FieldCanBeLocal",
                    "LocalCanBeFinal",
                    "ClassWithTooManyFields",
                    "OverlyLongMethod",
                    "FeatureEnvy",
                    "PublicMethodNotExposedInInterface",
                    "ZeroLengthArrayAllocation",
                    "UnqualifiedInnerClassAccess"})
public class AssociationsForm extends JPanel implements SettingsFormUI {
  private static final FileChooserDescriptor APP_FILE_CHOOSER_DESCRIPTOR =
    FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor();

  private static final TableModelEditor.EditableColumnInfo<RegexAssociation, String> PATH_COLUMN_INFO =
    new TableModelEditor.EditableColumnInfo<RegexAssociation, String>("Icon") {
      @Override
      public String valueOf(RegexAssociation item) {
        return PathUtil.toSystemDependentName(item.getIcon());
      }

      @Override
      public void setValue(RegexAssociation item, String value) {
        item.setIcon(value);
      }

      @Nullable
      @Override
      public TableCellEditor getEditor(RegexAssociation item) {
        return new LocalPathCellEditor().fileChooserDescriptor(APP_FILE_CHOOSER_DESCRIPTOR).normalizePath(true);
      }

      @NotNull
      @Override
      public TableCellRenderer getRenderer(RegexAssociation item) {
        return new IconTableCellRenderer<String>() {
          @NotNull
          @Override
          protected Icon getIcon(@NotNull final String value, final JTable table, final int row) {
            return MTIcons.getFileIcon(value);
          }
        };
      }
    };

  private static final ColumnInfo[] COLUMNS = {
    new TableModelEditor.EditableColumnInfo<RegexAssociation, String>("Name") {
      @Override
      public String valueOf(RegexAssociation item) {
        return item.getName();
      }

      @Override
      public void setValue(RegexAssociation item, String value) {
        item.setName(value);
      }
    },
    new TableModelEditor.EditableColumnInfo<RegexAssociation, String>("Pattern") {
      @Override
      public String valueOf(RegexAssociation item) {
        return item.getMatcher();
      }

      @Override
      public void setValue(RegexAssociation item, String value) {
        item.setPattern(value);
      }
    },
    PATH_COLUMN_INFO};

  public AssociationsForm() {
    fileAssociations = FileIconProvider.getAssociations().getAssociations();
    folderAssociations = FileIconProvider.getDirAssociations().getAssociations();

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

    //    boolean modified = config.isFileIconsModified(customFileAssociations);
    //    modified = modified || config.isFolderIconsModified(customFolderAssociations);
    return config.isFileIconsModified(customFileAssociationsEditor.getModel().getItems());
  }

  @NotNull
  public List<RegexAssociation> getFileAssociations() {
    return Collections.unmodifiableList(customFileAssociationsEditor.getModel().getItems());
  }

  @NotNull
  public List<RegexAssociation> getFolderAssociations() {
    return Collections.unmodifiableList(customFileAssociationsEditor.getModel().getItems());
  }

  @SuppressWarnings("SerializableNonStaticInnerClassWithoutSerialVersionUID")
  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    ResourceBundle bundle = ResourceBundle.getBundle("config.AtomFileIconsBundle"); //NON-NLS
    tabbedContainer = new JTabbedPane();
    customFileIconsNew = new JPanel();
    fileIcons = new JPanel();
    scrollPane1 = new JScrollPane();
    fileIconsTable = new FileAssociationsTable();
    folderIcons = new JPanel();
    scrollPane2 = new JScrollPane();
    folderIconsTable = new FolderAssociationsTable();

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

      //======== fileIcons ========
      {
        fileIcons.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[300,fill]")); //NON-NLS

        //======== scrollPane1 ========
        {

          //---- fileIconsTable ----
          fileIconsTable.setModel(new DefaultTableModel(
            new Object[][]{
            },
            new String[]{
              "Name",
              "Pattern",
              "Icon"
              //NON-NLS
            }
          ) {
            Class<?>[] columnTypes = new Class<?>[]{
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
          fileIconsTable.setBorder(null);
          fileIconsTable.setFillsViewportHeight(false);
          fileIconsTable.setStriped(true);
          scrollPane1.setViewportView(fileIconsTable);
        }
        fileIcons.add(scrollPane1, "cell 0 0"); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.fileIcons.tab.title"), fileIcons); //NON-NLS

      //======== folderIcons ========
      {
        folderIcons.setLayout(new MigLayout(
          "hidemode 3", //NON-NLS
          // columns
          "[grow,fill]", //NON-NLS
          // rows
          "[300.,fill]")); //NON-NLS

        //======== scrollPane2 ========
        {

          //---- folderIconsTable ----
          folderIconsTable.setModel(new DefaultTableModel(
            new Object[][]{
            },
            new String[]{
              "Name",
              "Pattern",
              "Icon"
              //NON-NLS
            }
          ) {
            Class<?>[] columnTypes = new Class<?>[]{
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
          folderIconsTable.setBorder(null);
          folderIconsTable.setFillsViewportHeight(false);
          scrollPane2.setViewportView(folderIconsTable);
        }
        folderIcons.add(scrollPane2, "cell 0 0"); //NON-NLS
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.folderIcons.tab.title"), folderIcons); //NON-NLS
    }
    add(tabbedContainer, "cell 0 0"); //NON-NLS

    //---- bindings ----
    bindingGroup = new BindingGroup();
    {
      JTableBinding binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_ONCE,
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
      JTableBinding binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_ONCE,
                                                                folderAssociations, folderIconsTable);
      binding.addColumnBinding(BeanProperty.create("name")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.folderIconsTable.columnName_3")) //NON-NLS
        .setColumnClass(String.class)
        .setEditable(false);
      binding.addColumnBinding(BeanProperty.create("icon")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.folderIconsTable.columnName.1")) //NON-NLS
        .setColumnClass(String.class)
        .setEditable(false);
      binding.addColumnBinding(ELProperty.create("${matcher}")) //NON-NLS
        .setColumnName(bundle.getString("AssociationsForm.folderIconsTable.columnName_2")) //NON-NLS
        .setColumnClass(String.class)
        .setEditable(false);
      bindingGroup.addBinding(binding);
    }
    bindingGroup.bind();
    // JFormDesigner - End of component initialization  //GEN-END:initComponents

    createUIComponents();
    addIconCellRenderers();

  }

  private void addIconCellRenderers() {
    fileIconsTable.getColumnModel().getColumn(1).setCellRenderer(new IconTableCellRenderer<String>() {
      @NotNull
      @Override
      protected Icon getIcon(@NotNull final String value, final JTable table, final int row) {
        return MTIcons.getFileIcon(value);
      }
    });
    folderIconsTable.getColumnModel().getColumn(1).setCellRenderer(new IconTableCellRenderer<String>() {
      @NotNull
      @Override
      protected Icon getIcon(@NotNull final String value, final JTable table, final int row) {
        return MTIcons.getFolderIcon(value);
      }
    });
  }

  private void createUIComponents() {
    final TableModelEditor.DialogItemEditor<RegexAssociation> itemEditor = new TableModelEditor.DialogItemEditor<RegexAssociation>() {

      @Override
      public @NotNull Class getItemClass() {
        return RegexAssociation.class;
      }

      @Override
      public RegexAssociation clone(@NotNull final RegexAssociation item, final boolean forInPlaceEditing) {
        return new RegexAssociation(forInPlaceEditing ? item.getName() : "",
                                    forInPlaceEditing ? item.getMatcher() : "",
                                    forInPlaceEditing ? item.getIcon() : "");
      }

      @Override
      public void edit(@NotNull final RegexAssociation item,
                       @NotNull final Function<RegexAssociation, RegexAssociation> mutator,
                       final boolean isAdd) {
        RegexAssociation settings = clone(item, true);
        mutator.fun(item).apply(settings);
      }

      @Override
      public void applyEdited(@NotNull final RegexAssociation oldItem, @NotNull final RegexAssociation newItem) {
        oldItem.apply(newItem);
      }

      @Override
      public boolean isEditable(@NotNull final RegexAssociation item) {
        return !item.isEmpty();
      }

    };
    customFileAssociationsEditor = new TableModelEditor<>(COLUMNS, itemEditor, "No custom file associations")
      .modelListener(new TableModelEditor.DataChangedListener<RegexAssociation>() {
        @Override
        public void tableChanged(@NotNull TableModelEvent event) {
          update();
        }

        @Override
        public void dataChanged(@NotNull final ColumnInfo<RegexAssociation, ?> columnInfo, final int rowIndex) {
          update();
        }

        private void update() {

        }
      });
    customFileIconsTable = customFileAssociationsEditor.createComponent();
    customFileIconsNew.add(customFileIconsTable, "cell 0 0");
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JTabbedPane tabbedContainer;
  private JPanel customFileIconsNew;
  private JPanel fileIcons;
  private JScrollPane scrollPane1;
  private FileAssociationsTable fileIconsTable;
  private JPanel folderIcons;
  private JScrollPane scrollPane2;
  private FolderAssociationsTable folderIconsTable;
  private List<com.mallowigi.icons.associations.Association> fileAssociations;
  private List<com.mallowigi.icons.associations.Association> folderAssociations;
  private BindingGroup bindingGroup;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private @NotNull JComponent customFileIconsTable;

  private TableModelEditor<RegexAssociation> customFileAssociationsEditor;

}
