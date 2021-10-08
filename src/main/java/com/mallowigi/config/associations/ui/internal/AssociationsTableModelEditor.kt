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
package com.mallowigi.config.associations.ui.internal

import com.intellij.configurationStore.serialize
import com.intellij.openapi.util.Ref
import com.intellij.ui.TableSpeedSearch
import com.intellij.ui.TableUtil
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import com.intellij.ui.table.TableView
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.ui.CollectionItemEditor
import com.intellij.util.ui.CollectionModelEditor
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.ListTableModel
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.table.ComboBoxTableCellEditor
import com.intellij.util.xmlb.XmlSerializer
import com.mallowigi.icons.associations.Association
import javax.swing.JComponent

/**
 * [Association] table model editor
 *
 * @param T type of items
 * @constructor
 *
 * @param items the [Association]
 * @param columns list of [ColumnInfo]
 * @param itemEditor the [Association] editor
 * @param emptyText the text to show when the table is empty
 */
class AssociationsTableModelEditor<T : Association>(
  items: List<T>,
  columns: Array<ColumnInfo<*, *>>,
  itemEditor: CollectionItemEditor<T>,
  emptyText: String,
) : CollectionModelEditor<T, CollectionItemEditor<T>?>(itemEditor) {
  /**
   * Table View
   */
  private val table: TableView<T>

  /**
   * Toolbar actions
   */
  private val toolbarDecorator: ToolbarDecorator

  /**
   * Association Table Model
   */
  private val model: AssociationTableModel<T>

  init {
    model = AssociationTableModel(columns, items)
    // Table settings
    table = TableView(model)
    table.isStriped = true
    table.setMaxItemsForSizeCalculation(MAX_ITEMS)
    table.ignoreRepaint = true
    table.fillsViewportHeight = true
    table.setShowGrid(false)
    table.setDefaultEditor(Enum::class.java, ComboBoxTableCellEditor.INSTANCE)
    table.setEnableAntialiasing(true)
    table.preferredScrollableViewportSize = JBUI.size(PREFERABLE_VIEWPORT_WIDTH, -1)
    table.visibleRowCount = JBTable.PREFERRED_SCROLLABLE_VIEWPORT_HEIGHT_IN_ROWS
    table.rowMargin = 0

    TableSpeedSearch(table)

    // Special support for checkbox: toggle by clicking or space
    TableUtil.setupCheckboxColumn(table.columnModel.getColumn(0), 0)
    JBTable.setupCheckboxShortcut(table, 0)

    // Display empty text when loading
    table.emptyText.setFont(UIUtil.getLabelFont().deriveFont(24.0f))
    table.emptyText.text = emptyText

    // Setup actions
    toolbarDecorator = ToolbarDecorator.createDecorator(table, this)
    toolbarDecorator.run {
      disableUpDownActions()
      disableAddAction()
      disableRemoveAction()
    }
  }

  /**
   *
   * @constructor for empty items
   */
  constructor(
    columns: Array<ColumnInfo<*, *>>,
    itemEditor: CollectionItemEditor<T>,
    emptyText: String,
  ) : this(emptyList<T>(), columns, itemEditor, emptyText)

  /**
   * Convenience method to disable/enable the table
   *
   * @param isEnabled new enabled state
   * @return self
   */
  fun enabled(isEnabled: Boolean): AssociationsTableModelEditor<T> {
    table.isEnabled = isEnabled
    return this
  }

  /**
   * Add a model listener for changes
   *
   * @param listener a data listener
   * @return self
   */
  internal fun setModelListener(listener: AssociationsDataChangedListener<T>): AssociationsTableModelEditor<T> {
    model.dataChangedListener = listener
    model.addTableModelListener(listener)
    return this
  }

  /**
   * Returns the [AssociationTableModel]
   *
   * @return the [AssociationTableModel]
   */
  fun getModel(): ListTableModel<T> = model

  /**
   * Create component with toolbar
   *
   */
  fun createComponent(): JComponent = toolbarDecorator.createPanel()

  /**
   * Selects an item programmatically
   *
   * @param item the item to select
   */
  fun selectItem(item: T) {
    table.clearSelection()
    val ref: Ref<T>?

    if (helper.hasModifiedItems()) {
      ref = Ref.create()
      helper.process { modified: T, original: T ->
        if (item === original) ref.set(modified)
        ref.isNull
      }
    } else {
      ref = null
    }

    table.addSelection(if (ref == null || ref.isNull) item else ref.get())
  }

  /**
   * Apply changes to elements
   *
   * @return the new items after changes
   */
  fun apply(): List<T> {
    if (helper.hasModifiedItems()) {
      val columns = model.columnInfos

      helper.process { newItem: T, oldItem: T ->
        // set all modified items new values
        for (column in columns) {
          if (column.isCellEditable(newItem)) column.setValue(oldItem, column.valueOf(newItem))
        }
        // Sets the newItem in place of the old item
        model.items[ContainerUtil.indexOfIdentity(model.items, newItem)] = oldItem
        true
      }
    }
    // Resets the helper
    helper.reset(model.items)
    return model.items
  }

  /**
   * Return the model items
   *
   * @return the model items
   */
  override fun getItems(): List<T> = model.items

  /**
   * Restore the original items on reset
   *
   * @param originalItems
   */
  override fun reset(originalItems: List<T>) {
    super.reset(originalItems)
    model.items = ArrayList(originalItems)
  }

  fun <T> cloneUsingXmlSerialization(oldItem: T, newItem: T) {
    val serialized = serialize(oldItem!!)
    if (serialized != null) {
      XmlSerializer.deserializeInto(newItem!!, serialized)
    }
  }

  companion object {
    const val MAX_ITEMS: Int = 20
    const val PREFERABLE_VIEWPORT_WIDTH: Int = 200
  }
}
