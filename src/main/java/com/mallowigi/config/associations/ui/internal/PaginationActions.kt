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

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.AnActionButton

class PaginationActions<T>(val model: AbstractPaginatedTableModel<T>) {
  private inner class FirstPageAction : AnActionButton("Go to first page", AllIcons.Diff.ApplyNotConflictsRight) {
    override fun actionPerformed(event: AnActionEvent) = model.goToFirstPage()
  }

  private inner class PrevPageAction : AnActionButton("Go to previous page", AllIcons.Diff.Arrow) {
    override fun actionPerformed(event: AnActionEvent) = model.goToPrevPage()
  }

  private inner class NextPageAction : AnActionButton("Go to next page", AllIcons.Diff.ArrowRight) {
    override fun actionPerformed(event: AnActionEvent) = model.goToNextPage()
  }

  private inner class LastPageAction : AnActionButton("Go to last page", AllIcons.Diff.ApplyNotConflictsLeft) {
    override fun actionPerformed(event: AnActionEvent) = model.goToLastPage()
  }
}
