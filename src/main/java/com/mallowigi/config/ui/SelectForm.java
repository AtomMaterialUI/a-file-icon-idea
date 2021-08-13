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

package com.mallowigi.config.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.NlsContexts;
import com.mallowigi.config.AtomSettingsBundle;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * @author Elior Boukhobza
 */
@SuppressWarnings({"FieldCanBeLocal",
  "DuplicateStringLiteralInspection",
  "StringConcatenation",
  "UndesirableClassUsage"})
public final class SelectForm extends JPanel implements SettingsFormUI, SearchableConfigurable {

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JLabel explanation;
  private JTabbedPane tabbedPane1;
  private JPanel fileAssociationsPanel;
  private JPanel folderAssociationsPanel;

  @Override
  public void init() {
    initComponents();
  }

  @Override
  public JComponent getContent() {
    return this;
  }

  @Override
  public void afterStateSet() {

  }

  @Override
  public void dispose() {
    //empty
  }

  @Override
  public @NotNull @NonNls String getId() {
    return "AtomSelectConfigurable";
  }

  @Override
  public @NlsContexts.ConfigurableName String getDisplayName() {
    return AtomSettingsBundle.message("select.titles.main");
  }

  @Override
  public @Nullable JComponent createComponent() {
    return null;
  }

  @Override
  public boolean isModified() {
    return false;
  }

  @Override
  public void apply() throws ConfigurationException {

  }

  @SuppressWarnings("ConfusingFloatingPointLiteral")
  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    explanation = new JLabel();
    tabbedPane1 = new JTabbedPane();
    fileAssociationsPanel = new JPanel();
    folderAssociationsPanel = new JPanel();

    //======== this ========
    setBorder(new TitledBorder(null, "Associations Editor", TitledBorder.CENTER, TitledBorder.TOP));
    setLayout(new MigLayout(
      "hidemode 3",
      // columns
      "[369,fill]",
      // rows
      "[]" +
        "[270,fill]" +
        "[]"));

    //---- explanation ----
    explanation.setText("Use the following tables to disable unwanted associations");
    explanation.setFont(explanation.getFont().deriveFont(explanation.getFont().getSize() - 1f));
    explanation.setForeground(UIManager.getColor("textInactiveText"));
    add(explanation, "cell 0 0");

    //======== tabbedPane1 ========
    {

      //======== fileAssociationsPanel ========
      {
        fileAssociationsPanel.setLayout(new MigLayout(
          "hidemode 3",
          // columns
          "[fill]",
          // rows
          "[]"));
      }
      tabbedPane1.addTab("File Associations", fileAssociationsPanel);

      //======== folderAssociationsPanel ========
      {
        folderAssociationsPanel.setLayout(new MigLayout(
          "hidemode 3",
          // columns
          "[fill]",
          // rows
          "[]"));
      }
      tabbedPane1.addTab("Folder Associations", folderAssociationsPanel);
    }
    add(tabbedPane1, "cell 0 1");
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }
  // JFormDesigner - End of variables declaration  //GEN-END:variables
}
