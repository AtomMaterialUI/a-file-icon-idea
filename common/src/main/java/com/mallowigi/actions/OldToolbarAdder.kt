/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.actions

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.impl.ActionConfigurationCustomizer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.ui.ExperimentalUI
import org.jetbrains.annotations.NonNls

/** Utility to add older icons to new UI. */
class OldToolbarAdder : ActionConfigurationCustomizer {

  /** Customize. */
  override fun customize(actionManager: ActionManager) {
    if (alreadyRunOnce || !ExperimentalUI.isNewUI()) return

    alreadyRunOnce = true
    ApplicationManager.getApplication().invokeLater { executeAdd(actionManager) }
  }

  private fun executeAdd(actionManager: ActionManager) {
    val mainToolBar = getActionGroup(actionManager, MAIN_TOOLBAR)
    val experimentalToolbarActions = getActionGroup(actionManager, OLD_TOOLBAR) as? DefaultActionGroup
    if (mainToolBar == null || experimentalToolbarActions == null) return

    for (action in mainToolBar.getChildren(null, actionManager)) {
      experimentalToolbarActions.add(action)
    }
  }

  /** Get a specific action group of the current actionManager. */
  private fun getActionGroup(actionManager: ActionManager, @NonNls id: String): ActionGroup? {
    val action = actionManager.getAction(id)
    if (action is ActionGroup) return action
    return if (action == null) null else DefaultActionGroup(listOf(action))
  }

  companion object {
    /** Prevents an infinite loop. */
    var alreadyRunOnce: Boolean = false

    /** Old Toolbar key. */
    const val OLD_TOOLBAR: String = "AtomFileIcons.OldToolbar"

    /** Main toolbar key. */
    const val MAIN_TOOLBAR: String = "MainToolBar"
  }

}
