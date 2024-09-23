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

package com.mallowigi.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ActionGroup

class OldToolbarGroup : DefaultActionGroup() {
  private var children: Array<AnAction> = emptyArray()

  override fun getChildren(event: AnActionEvent?): Array<AnAction> {
    if (children.isEmpty()) {
      val actionManager = event?.actionManager ?: return emptyArray()
      val mainToolBar = getActionGroup(actionManager) ?: return emptyArray()

      children = mainToolBar.getChildren(null)
    }

    return children
  }

  private fun getActionGroup(actionManager: ActionManager): ActionGroup? {
    val action = actionManager.getAction(MAIN_TOOLBAR)
    if (action is ActionGroup) return action
    return if (action == null) null else DefaultActionGroup(listOf(action))
  }

  /** Do not wait. */
  override fun isDumbAware(): Boolean = true

  /** Run on background thread. */
  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  companion object {
    const val MAIN_TOOLBAR = "MainToolbar"
  }
}
