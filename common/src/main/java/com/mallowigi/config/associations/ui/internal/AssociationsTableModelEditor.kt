/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2024 Elior "Mallowigi" Boukhobza
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
 */
package com.mallowigi.config.associations.ui.internal

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.Comparing
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.*
import com.intellij.ui.table.JBTable
import com.intellij.ui.table.TableView
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.ui.*
import com.intellij.util.ui.table.ComboBoxTableCellEditor
import com.mallowigi.config.AtomSettingsConfig
import com.mallowigi.config.BundledAssociations
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.PsiAssociation
import com.mallowigi.icons.associations.RegexAssociation
import com.mallowigi.models.IconType
import com.mallowigi.utils.toHex
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.ListSelectionModel
import javax.swing.RowSorter
import javax.swing.SortOrder
import javax.swing.Timer
import javax.swing.event.DocumentEvent

/**
 * [Association] table model editor
 *
 * @param items the [Association]s
 * @param columns list of [ColumnInfo]s
 * @param itemEditor the [Association] editor
 * @param emptyText the text to show when the table is empty
 * @param searchTextField the search text field (optional)
 * @constructor
 */
@Suppress("HardCodedStringLiteral", "KDocMissingDocumentation", "OutdatedDocumentation")
class AssociationsTableModelEditor(
  items: List<Association>,
  columns: Array<ColumnInfo<*, *>>,
  itemEditor: CollectionItemEditor<Association>,
  emptyText: String,
  val searchTextField: SearchTextField?,
  val type: IconType = IconType.FILE,
) : CollectionModelEditor<Association, CollectionItemEditor<Association>?>(itemEditor) {
  /** Table View. */
  private val table: TableView<Association>

  /** Toolbar actions. */
  private val toolbarDecorator: ToolbarDecorator

  /** Association Table Model. */
  private val model: AssociationTableModel = AssociationTableModel(columns, items)

  /** Backing field for model's unfiltered list. */
  private val myList: MutableList<Association>
    get() = model.allItems

  /** Backing field for model's filtered list. */
  private val myFilteredList: MutableList<Association>
    get() = model.filteredItems

  /** Own Increment for adding. */
  private var increment: Int = 0

  /**
   * Debouncing timer for filtering the table
   */
  private val filterTimer = Timer(FILTER_DELAY_MS) { filterTable() }.apply { isRepeats = false }

  init {
    initUnfilteredList()

    // Table settings
    table = TableView(model)
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    table.isStriped = true
    table.setMaxItemsForSizeCalculation(MAX_ITEMS)
    table.ignoreRepaint = true
    table.fillsViewportHeight = true
    table.setShowGrid(false)
    table.setDefaultEditor(Enum::class.java, ComboBoxTableCellEditor.INSTANCE)
    table.setEnableAntialiasing(true)
    table.intercellSpacing = Dimension(0, 0)
    table.preferredScrollableViewportSize = JBUI.size(PREFERABLE_VIEWPORT_WIDTH, PREFERABLE_VIEWPORT_HEIGHT)
    table.visibleRowCount = MIN_ROW_COUNT
    table.rowHeight = ROW_HEIGHT
    table.rowMargin = 0
    // sort by touched but remove the column from the table
    table.rowSorter.sortKeys = listOf(RowSorter.SortKey(Columns.TOUCHED.index, SortOrder.DESCENDING))
    table.removeColumn(table.columnModel.getColumn(Columns.TOUCHED.index))

    // Special support for checkbox: toggle by clicking or space
    TableUtil.setupCheckboxColumn(table.columnModel.getColumn(Columns.ENABLED.index), 0)
    JBTable.setupCheckboxShortcut(table, Columns.ENABLED.index)

    // Display empty text when loading
    table.emptyText.setFont(UIUtil.getLabelFont().deriveFont(LOADING_FONT_SIZE))
    table.emptyText.text = emptyText

    // Setup actions
    toolbarDecorator = ToolbarDecorator.createDecorator(table, this)
    toolbarDecorator.run {
      disableUpDownActions()
      setRemoveActionUpdater { table.selectedObject?.touched == true }
      addExtraAction(object : AnAction("Reset", "Reset", AllIcons.Actions.Rollback) {
        override fun actionPerformed(e: AnActionEvent) {
          val association = table.selectedObject ?: return
          resetItem(association)
        }

        override fun update(e: AnActionEvent) {
          e.presentation.isEnabled = this.isEnabled()
        }

        fun isEnabled(): Boolean {
          val association = table.selectedObject ?: return false
          if (!association.touched) return false

          return BundledAssociations.instance.getDefault(association.name, type) != null
        }

        override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
      })
    }

    // Search and filter table
    if (searchTextField != null) {
      table.addKeyListener(object : KeyAdapter() {
        override fun keyTyped(e: KeyEvent) {
          val keyChar = e.keyChar
          if (Character.isLetter(keyChar) || Character.isDigit(keyChar)) {
            searchTextField.text = keyChar.toString()
            searchTextField.requestFocus()
          }
          super.keyPressed(e)
        }
      })

      searchTextField.addDocumentListener(object : DocumentAdapter() {
        override fun textChanged(e: DocumentEvent) = scheduleFilter()
      })
    }

    // Color picker listening
    ColorPickerListener().installOn(table)
  }

  constructor(
    columns: Array<ColumnInfo<*, *>>,
    itemEditor: AssociationsTableItemEditor,
    emptyText: String,
    searchTextField: SearchTextField,
    type: IconType,
  ) : this(emptyList<Association>(), columns, itemEditor, emptyText, searchTextField, type)

  /** Inits the unfiltered list (before any search). */
  private fun initUnfilteredList() {
    myList.clear()
    myList.addAll(model.items)
    filterTable()
  }

  /** Filters the table - this will set the [model]'s filteredItems. */
  private fun filterTable() {
    if (searchTextField == null) return

    val text: String = searchTextField.text.trim()
    myFilteredList.clear()
    // Search by name or pattern only
    for (assoc in myList) {
      if (text.isEmpty() || StringUtil.containsIgnoreCase(
          assoc.matcher, text
        ) || StringUtil.containsIgnoreCase(assoc.name, text)
      ) {
        myFilteredList.add(assoc)
      }
    }
//    model.filteredItems = myFilteredList
    model.fireTableDataChanged()
  }

  /**
   * Schedules a filter operation by restarting the debounce timer.
   */
  private fun scheduleFilter() = filterTimer.restart()

  /**
   * Convenience method to disable/enable the table.
   *
   * @param isEnabled new enabled state
   * @return self
   */
  fun enabled(isEnabled: Boolean): AssociationsTableModelEditor {
    table.isEnabled = isEnabled
    return this
  }

  /**
   * Returns the [AssociationTableModel].
   *
   * @return the [AssociationTableModel]
   */
  fun getModel(): AssociationTableModel = model

  /** Create component with toolbar. */
  fun createComponent(): JComponent = toolbarDecorator.createPanel()

  /**
   * Resets the [Association] to its default values.
   *
   * @param item the [Association] to reset
   */
  private fun resetItem(item: Association) {
    val default = BundledAssociations.instance.getDefault(item.name, type) ?: return
    item.apply(default)
    item.touched = false
    model.fireTableDataChanged()
  }

  /**
   * Apply changes to elements.
   *
   * @return the new items after changes
   */
  fun apply(): List<Association> {
    if (helper.hasModifiedItems()) {
      val columns = model.columnInfos

      helper.process { newItem: Association, oldItem: Association ->
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
   * Return the model items.
   *
   * @return the model items
   */
  override fun getItems(): List<Association> = model.items

  /**
   * Resets the [model]'s items.
   *
   * @param originalItems the elements
   */
  override fun reset(originalItems: List<Association>) {
    filterTimer.stop()
    super.reset(originalItems)
    model.allItems = ArrayList(originalItems)
    model.filteredItems = ArrayList(originalItems)
    initUnfilteredList()
  }

  /** Create a new custom association. */
  override fun createElement(): Association {
    increment++

    val association = when (type) {
      IconType.PSI -> {
        val psiAssociation = PsiAssociation()
        psiAssociation.name = "New PSI Association (${increment})"
        psiAssociation.path = "identifier${increment}"
        psiAssociation.priority = DEFAULT_PRIORITY
        psiAssociation.icon = "/glyphs/nodes/class.svg"
        psiAssociation.iconType = IconType.PSI
        psiAssociation
      }

      else         -> {
        val regexAssociation = RegexAssociation()
        regexAssociation.name = "New Association (${increment})"
        regexAssociation.pattern = "^.*\\.ext${increment}$"
        regexAssociation.priority = DEFAULT_PRIORITY
        regexAssociation.iconColor = DEFAULT_ICON_COLOR
        regexAssociation.folderColor = DEFAULT_FOLDER_COLOR
        regexAssociation.folderIconColor = DEFAULT_ICON_COLOR
        regexAssociation.icon = ""
        regexAssociation.iconType = type
        regexAssociation
      }
    }
    return association
  }

  /**
   * Overrides [silentlyReplaceItem] - we need to modify the unfiltered list when a change occurs since we're working on the filtered list.
   *
   * @param oldItem item changed (in the filtered list)
   * @param newItem new item to insert
   * @param index index in the filtered lisst
   */
  override fun silentlyReplaceItem(oldItem: Association, newItem: Association, index: Int) {
    super.silentlyReplaceItem(oldItem, newItem, index)
    newItem.touched = true
    // silently replace item in unfiltered list
    val items = model.allItems
    val allItemsIndex = items.indexOfFirst { it.name == newItem.name }
    items[if (allItemsIndex == -1) ContainerUtil.indexOfIdentity(items, oldItem) else allItemsIndex] = newItem
  }

  /**
   * [Association] table model inheriting the [ListTableModel]
   *
   * @param columnNames the columns
   * @param items the items
   * @constructor
   */
  inner class AssociationTableModel(columnNames: Array<ColumnInfo<*, *>>, items: List<Association>) :
    ListTableModel<Association>(columnNames, items) {

    /** This contains all items, before any filter is applied. This is also what will be persisted. */
    var allItems: MutableList<Association> = items.toMutableList()

    /** This is the currently filtered table. */
    var filteredItems: MutableList<Association> = items.toMutableList()
      set(value) {
        field = value
        super.setItems(value)
      }

    /**
     * We display only the filtered items.
     *
     * @return the [filteredItems]
     */
    override fun getItems(): MutableList<Association> = filteredItems

    /**
     * When items are set, we reset the table's items.
     *
     * @param items
     */
    override fun setItems(items: MutableList<Association>) {
      allItems = items
      filteredItems = items
      fireTableDataChanged()
    }

    /**
     * Remove a row @unused.
     *
     * @param index
     */
    override fun removeRow(index: Int) {
      val item = getItem(index)
      if (!item.touched) return

      helper.remove(item)
      super.removeRow(index)
      allItems.remove(item)
    }

    override fun addRow(item: Association) {
      super.addRow(item)
      allItems.add(item)
    }

    /**
     * Set the value at the given row and column using the [helper].
     *
     * @param aValue value to set
     * @param rowIndex row number
     * @param columnIndex column number
     */
    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
      if (aValue == null) return

      if (rowIndex < rowCount) {
        val columnInfo = columnInfos[columnIndex]
        val item = getItem(rowIndex)
        val oldValue = columnInfo.valueOf(item)

        val comparator = when (columnInfo.columnClass) {
          String::class.java -> Comparing.strEqual(oldValue as? String, aValue as String)
          else               -> Comparing.equal(oldValue, aValue)
        }

        if (!comparator) {
          columnInfo.setValue(helper.getMutable(item, rowIndex), aValue)
        }
      }
    }

  }

  inner class ColorPickerListener : ClickListener() {
    override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
      val point = event.point
      val row: Int = table.rowAtPoint(point)
      val column: Int = table.columnAtPoint(point) + 1 // Because the touched takes a slot...
      if (row < 0 || column < 0) return false
      val modelIndex = table.convertRowIndexToModel(row)

      if (modelIndex < 0 || modelIndex >= table.rowCount) return false

      return when (type) {
        IconType.FILE        -> when (column) {
          Columns.ICONCOLOR.index -> setIconColor(modelIndex)
          else                    -> false
        }

        IconType.FOLDER      -> when (column) {
          Columns.FOLDERCOLOR.index     -> setFolderColor(modelIndex)
          Columns.FOLDERICONCOLOR.index -> setFolderIconColor(modelIndex)
          else                          -> false
        }

        IconType.FOLDER_OPEN -> when (column) {
          Columns.FOLDERCOLOR.index     -> setFolderColor(modelIndex)
          Columns.FOLDERICONCOLOR.index -> setFolderIconColor(modelIndex)
          else                          -> false
        }

        IconType.PSI         -> false
      }
    }

    private fun setFolderColor(row: Int): Boolean {
      val colorValue: Any = model.getValueAt(row, Columns.FOLDERCOLOR.index)
      val modelColor: Color = ColorUtil.fromHex(colorValue as String)

      ColorChooserService.getInstance().showPopup(null, modelColor, { color, _ ->
        color?.let { model.setValueAt(it.toHex(), row, Columns.FOLDERCOLOR.index) }
      })

      return true
    }

    private fun setFolderIconColor(row: Int): Boolean {
      val colorValue: Any = model.getValueAt(row, Columns.FOLDERICONCOLOR.index)
      val modelColor: Color = ColorUtil.fromHex(colorValue as String)

      ColorChooserService.getInstance().showPopup(null, modelColor, { color, _ ->
        color?.let { model.setValueAt(it.toHex(), row, Columns.FOLDERICONCOLOR.index) }
      })

      return true
    }

    private fun setIconColor(row: Int): Boolean {
      val colorValue: Any = model.getValueAt(row, Columns.ICONCOLOR.index)
      val modelColor: Color = ColorUtil.fromHex(colorValue as String)

      ColorChooserService.getInstance().showPopup(null, modelColor, { color, _ ->
        color?.let { model.setValueAt(it.toHex(), row, Columns.ICONCOLOR.index) }
      })

      return true
    }
  }

  @Suppress("HardCodedStringLiteral", "KDocMissingDocumentation")
  companion object {
    const val MAX_ITEMS: Int = 60
    const val MIN_ROW_COUNT: Int = 18
    const val ROW_HEIGHT: Int = 32
    const val LOADING_FONT_SIZE: Float = 24.0F
    const val DEFAULT_PRIORITY: Int = 10_000
    const val PREFERABLE_VIEWPORT_WIDTH: Int = 200
    const val PREFERABLE_VIEWPORT_HEIGHT: Int = 280
    const val FILTER_DELAY_MS: Int = 150

    /** Default color for icon in the associations table. */
    val DEFAULT_ICON_COLOR: String
      get() = AtomSettingsConfig.instance.getCurrentAccentColor()

    /** Default color for folder in the associations table. */
    val DEFAULT_FOLDER_COLOR: String
      get() = AtomSettingsConfig.instance.getCurrentThemedColor()

    // columns (yes this is hardcoded, but I have no idea how to do it differently)
    @Suppress("unused")
    private enum class Columns(val index: Int) {
      ENABLED(0),
      TOUCHED(1),
      NAME(2),
      PATTERN(3),
      ICON(4),
      SELECT(5),
      PRIORITY(6),
      ICONCOLOR(7),
      FOLDERCOLOR(7),
      FOLDERICONCOLOR(8),
    }
  }
}
