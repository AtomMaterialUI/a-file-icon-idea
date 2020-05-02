/*
 * Created by JFormDesigner on Sat May 02 14:11:13 IDT 2020
 */

package com.mallowigi.config.ui;

import com.mallowigi.config.AtomFileIconsConfig;
import com.mallowigi.icons.FileIconProvider;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Elior Boukhobza
 */
@SuppressWarnings({"UndesirableClassUsage",
                    "PackageVisibleField",
                    "WeakerAccess",
                    "FieldCanBeLocal"})
public class AssociationsForm extends JPanel implements SettingsFormUI {

  public AssociationsForm() {
    fileAssociations = FileIconProvider.getAssociations().getAssociations();
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

  public void setFormState(final AtomFileIconsConfig config) {
    afterStateSet();
  }

  @SuppressWarnings({"HardCodedStringLiteral",
                      "SerializableNonStaticInnerClassWithoutSerialVersionUID"})
  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    final ResourceBundle bundle = ResourceBundle.getBundle("config.AtomFileIconsBundle");
    tabbedContainer = new JTabbedPane();
    fileIcons = new JPanel();
    scrollPane2 = new JScrollPane();
    fileIconsTable = new JTable();

    //======== this ========
    setLayout(new MigLayout(
      "hidemode 3",
      // columns
      "[585,grow,fill]",
      // rows
      "[]"));

    //======== tabbedContainer ========
    {

      //======== fileIcons ========
      {
        fileIcons.setLayout(new MigLayout(
          "hidemode 3",
          // columns
          "[grow,fill]",
          // rows
          "[]0"));

        //======== scrollPane2 ========
        {

          //---- fileIconsTable ----
          fileIconsTable.setModel(new DefaultTableModel(
            new Object[][]{
              {"",
                null,
                ""},
              {null,
                null,
                null},
            },
            new String[]{
              "Name",
              "Pattern",
              "Icon"
            }
          ) {
            final Class<?>[] columnTypes = new Class<?>[]{
              String.class,
              Object.class,
              Object.class
            };

            @Override
            public Class<?> getColumnClass(final int columnIndex) {
              return columnTypes[columnIndex];
            }
          });
          fileIconsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
          scrollPane2.setViewportView(fileIconsTable);
        }
        fileIcons.add(scrollPane2, "cell 0 0,growx");
      }
      tabbedContainer.addTab(bundle.getString("AssociationsForm.fileIcons.tab.title"), fileIcons);
    }
    add(tabbedContainer, "cell 0 0");

    //---- bindings ----
    bindingGroup = new BindingGroup();
    {
      final JTableBinding binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
                                                                      fileAssociations, fileIconsTable);
      binding.addColumnBinding(BeanProperty.create("name"))
        .setColumnName(bundle.getString("AssociationsForm.fileIconsTable.columnName_2"))
        .setColumnClass(String.class);
      binding.addColumnBinding(BeanProperty.create("icon"))
        .setColumnName(bundle.getString("AssociationsForm.fileIconsTable.columnName.1"))
        .setColumnClass(String.class);
      binding.addColumnBinding(ELProperty.create("${matcher}"))
        .setColumnName(bundle.getString("AssociationsForm.fileIconsTable.columnName_3"))
        .setColumnClass(String.class);
      bindingGroup.addBinding(binding);
    }
    bindingGroup.bind();
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JTabbedPane tabbedContainer;
  private JPanel fileIcons;
  private JScrollPane scrollPane2;
  private JTable fileIconsTable;
  private final List<com.mallowigi.icons.associations.Association> fileAssociations;
  private BindingGroup bindingGroup;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
}
