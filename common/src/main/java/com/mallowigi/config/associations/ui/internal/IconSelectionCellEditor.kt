/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 Elior "Mallowigi" Boukhobza
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
 */
package com.mallowigi.config.associations.ui.internal

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.JBPopupListener
import com.intellij.openapi.ui.popup.LightweightWindowEvent
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.SearchTextField
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.PathUtil
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.AbstractTableCellEditor
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.LinkedHashMap
import javax.swing.DefaultListModel
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.KeyStroke
import javax.swing.ListSelectionModel
import javax.swing.SwingConstants
import javax.swing.Timer
import javax.swing.event.DocumentEvent

/** Icon selection cell editor. */
class IconSelectionCellEditor(
  icons: List<String>,
  private val loadIcon: (String) -> Icon,
) : AbstractTableCellEditor() {
  private val icons = icons.distinct().sorted()
  private val previewCache = object : LinkedHashMap<String, Icon>(PREVIEW_CACHE_SIZE, LOAD_FACTOR, true) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Icon>?): Boolean = size > PREVIEW_CACHE_SIZE
  }
  private var value: String? = null
  private var popup: JBPopup? = null

  override fun getCellEditorValue(): String? = value

  override fun getTableCellEditorComponent(
    table: JTable,
    value: Any?,
    isSelected: Boolean,
    row: Int,
    column: Int
  ): Component {
    this.value = value as? String

    ApplicationManager.getApplication().invokeLater {
      showChooser(
        table = table,
        row = row,
        column = column,
      )
    }

    return JLabel(this.value?.let(PathUtil::getFileName).orEmpty(), SwingConstants.LEFT)
  }

  /**
   * Displays a popup chooser dialog for selecting an icon in a table cell editor.
   *
   * This method creates and configures a custom popup with a searchable list of icons,
   * allowing users to select an icon for the specified cell in the table.
   *
   * @param table the JTable instance where the chooser is displayed
   * @param row the row index of the cell for which the chooser is invoked
   * @param column the column index of the cell for which the chooser is invoked
   */
  private fun showChooser(table: JTable, row: Int, column: Int) {
    popup?.cancel()

    val searchField = SearchTextField()
    val listModel = DefaultListModel<String>()
    val iconList = JBList(listModel)
    val emptyLabel = JLabel("No matching icons", SwingConstants.CENTER)
    val panel = JPanel(BorderLayout(JBUI.scale(8), JBUI.scale(8)))
    var selectionCommitted = false

    lateinit var chooserPopup: JBPopup

    // Performance improvements!
    iconList.selectionMode = ListSelectionModel.SINGLE_SELECTION
    iconList.fixedCellHeight = JBUI.scale(ICON_ROW_HEIGHT)
    iconList.fixedCellWidth = JBUI.scale(ICON_LIST_WIDTH)
    iconList.visibleRowCount = VISIBLE_ICON_ROWS
    iconList.cellRenderer = iconRenderer()

    panel.border = JBUI.Borders.empty(8)
    panel.add(searchField, BorderLayout.NORTH)
    panel.add(JBScrollPane(iconList), BorderLayout.CENTER)
    panel.add(emptyLabel, BorderLayout.SOUTH)

    /**
     * Update the list with the search filter
     */
    fun updateList() {
      // Filter the list
      val query = searchField.text.trim()
      val filteredIcons = icons.filter { iconPath ->
        when {
          query.isEmpty() -> true
          PathUtil.getFileName(iconPath).contains(query, ignoreCase = true) -> true
          else -> iconPath.contains(query, ignoreCase = true)
        }
      }

      listModel.clear()
      listModel.addAll(filteredIcons)
      emptyLabel.isVisible = filteredIcons.isEmpty()

      selectCurrentValue(iconList = iconList)
    }

    // Debounce
    val filterTimer = Timer(FILTER_DELAY_MS) { updateList() }.apply { isRepeats = false }

    // Search filter
    searchField.addDocumentListener(object : DocumentAdapter() {
      override fun textChanged(event: DocumentEvent) = filterTimer.restart()
    })

    /**
     * Apply the selected icon
     */
    fun selectIcon() {
      val selectedIcon = iconList.selectedValue ?: return

      value = selectedIcon
      selectionCommitted = true
      chooserPopup.cancel()
      fireEditingStopped()
    }

    // Select the icon when pressing enter on the list
    iconList.registerKeyboardAction(
      { selectIcon() },
      KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
      JComponent.WHEN_FOCUSED,
    )

    // Select the current icon when pressing enter on the search field
    searchField.textEditor.registerKeyboardAction(
      { selectIcon() },
      KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
      JComponent.WHEN_FOCUSED,
    )

    // Select the icon when double-clicking on the list
    iconList.addMouseListener(object : MouseAdapter() {
      override fun mouseClicked(event: MouseEvent) {
        if (event.clickCount == DOUBLE_CLICK_COUNT) selectIcon()
      }
    })

    updateList()

    // Create the chooser popup
    chooserPopup = JBPopupFactory.getInstance()
      .createComponentPopupBuilder(panel, searchField.textEditor)
      .setFocusable(true)
      .setRequestFocus(true)
      .setResizable(true)
      .setCancelOnClickOutside(true)
      .createPopup()

    chooserPopup.addListener(object : JBPopupListener {
      override fun onClosed(event: LightweightWindowEvent) {
        filterTimer.stop()
        popup = null

        if (!selectionCommitted) fireEditingCanceled()
      }
    })

    popup = chooserPopup

    // Show the chooser close to the cell being edited
    val cellBounds = table.getCellRect(row, column, true)
    chooserPopup.show(RelativePoint(table, Point(cellBounds.x, cellBounds.y + cellBounds.height)))
  }

  private fun iconRenderer(): SimpleListCellRenderer<String> = object : SimpleListCellRenderer<String>() {
    override fun customize(
      list: JList<out String>,
      value: String?,
      index: Int,
      selected: Boolean,
      hasFocus: Boolean,
    ) {
      if (value == null) return

      text = PathUtil.getFileName(value)
      icon = previewCache.getOrPut(value) { loadIcon(value) }
    }
  }

  private fun selectCurrentValue(iconList: JBList<String>) {
    val currentValue = value
    val currentIndex = currentValue?.let { selectedValue ->
      (0 until iconList.model.size).firstOrNull { iconList.model.getElementAt(it) == selectedValue }
    }

    when {
      currentIndex != null -> {
        iconList.selectedIndex = currentIndex
        iconList.ensureIndexIsVisible(currentIndex)
      }
      iconList.model.size > 0 -> iconList.selectedIndex = 0
    }
  }

  companion object {
    private const val FILTER_DELAY_MS = 150
    private const val ICON_ROW_HEIGHT = 24
    private const val ICON_LIST_WIDTH = 360
    private const val VISIBLE_ICON_ROWS = 12
    private const val PREVIEW_CACHE_SIZE = 256
    private const val LOAD_FACTOR = 0.75f
    private const val DOUBLE_CLICK_COUNT = 2
  }
}
