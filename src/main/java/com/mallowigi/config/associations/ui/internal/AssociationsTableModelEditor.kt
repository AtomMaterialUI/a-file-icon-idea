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
import com.intellij.openapi.util.Comparing
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
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.ListSelectionModel
import javax.swing.RowSorter
import javax.swing.SortOrder

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
  private val model: AssociationTableModel

  init {
    model = AssociationTableModel(columns, items)

    // Table settings
    table = TableView(model)
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    table.isStriped = true
    table.setMaxItemsForSizeCalculation(MAX_ITEMS)
    table.ignoreRepaint = true
    table.fillsViewportHeight = true
    table.setShowGrid(false)
    table.setDefaultEditor(Enum::class.java, ComboBoxTableCellEditor.INSTANCE)
    table.setEnableAntialiasing(false)
    table.intercellSpacing = Dimension(0, 0)
    table.preferredScrollableViewportSize = JBUI.size(PREFERABLE_VIEWPORT_WIDTH, -1)
    table.visibleRowCount = MIN_ROW_COUNT
    table.rowHeight = 32
    table.rowMargin = 0
    // sort by touched but remove the column from the table
    table.rowSorter.sortKeys = listOf(RowSorter.SortKey(Columns.TOUCHED.index, SortOrder.DESCENDING))
    table.removeColumn(table.columnModel.getColumn(Columns.TOUCHED.index))

    // search by name or pattern only
    TableSpeedSearch(table) { o, cell ->
      o.toString().takeIf { cell.column == Columns.NAME.index || cell.column == Columns.PATTERN.index }
    }

    // Special support for checkbox: toggle by clicking or space
    TableUtil.setupCheckboxColumn(table.columnModel.getColumn(Columns.ENABLED.index), 0)
    JBTable.setupCheckboxShortcut(table, Columns.ENABLED.index)

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

  /**
   * Clone using xml serialization
   *
   * @param T
   * @param oldItem
   * @param newItem
   */
  fun <T> cloneUsingXmlSerialization(oldItem: T, newItem: T) {
    val serialized = serialize(oldItem!!)
    if (serialized != null) {
      XmlSerializer.deserializeInto(newItem!!, serialized)
    }
  }

  private inner class AssociationTableModel(columnNames: Array<ColumnInfo<*, *>>, items: List<T>) :
    ListTableModel<T>(columnNames, items) {

    // Our items
    var assocs: MutableList<T> = items.toMutableList()

    // An optional [DataChangedListener]
    var dataChangedListener: AssociationsDataChangedListener<T>? = null

    override fun getItems(): MutableList<T> = assocs

    override fun setItems(items: MutableList<T>) {
      assocs = items
      super.setItems(items)
    }

    override fun removeRow(index: Int) {
      helper.remove(getItem(index))
      super.removeRow(index)
    }

    /**
     * Set the value at the given row/column
     *
     * @param aValue value to set
     * @param rowIndex row number
     * @param columnIndex column number
     */
    override fun setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int) {
      if (rowIndex < rowCount) {
        val columnInfo = columnInfos[columnIndex]
        val item = getItem(rowIndex)
        val oldValue = columnInfo.valueOf(item)

        val comparator = when (columnInfo.columnClass) {
          String::class.java -> Comparing.strEqual(oldValue as String?, aValue as String)
          else               -> Comparing.equal(oldValue, aValue)
        }

        if (!comparator) {
          columnInfo.setValue(helper.getMutable(item, rowIndex), aValue)
          dataChangedListener?.dataChanged(columnInfo, rowIndex)
        }
      }
    }

  }

  companion object {
    const val MAX_ITEMS: Int = 60
    const val PREFERABLE_VIEWPORT_WIDTH: Int = 200
    const val MIN_ROW_COUNT: Int = 15

    // columns (yes this is hardcoded but I have no idea how to do it differently)
    private enum class Columns(val displayName: String, val index: Int) {
      ENABLED("Enabled", 0),
      NAME("Name", 1),
      PATTERN("Pattern", 2),
      ICON("Icon", 3),
      PRIORITY("Priority", 4),
      TOUCHED("Touched", 5),
    }
  }
}
