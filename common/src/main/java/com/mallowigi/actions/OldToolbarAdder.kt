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

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.actionSystem.impl.ActionConfigurationCustomizer
import com.intellij.ui.ExperimentalUI

/** Utility to add older icons to new UI. */
class OldToolbarAdder : ActionConfigurationCustomizer {
  /** Customize. */
  override fun customize(actionManager: ActionManager) {
    if (alreadyRunOnce || !ExperimentalUI.isNewUI()) return

    alreadyRunOnce = true
    val mainToolBar = ActionUtil.getActionGroup("MainToolBar") ?: return
    val experimentalToolbarActions = ActionUtil.getActionGroup("OldToolbar") as DefaultActionGroup? ?: return

    for (action in mainToolBar.getChildren(null)) {
      experimentalToolbarActions.add(action)
    }
  }

  companion object {
    /** Prevents an infinite loop. */
    var alreadyRunOnce: Boolean = false;
  }
}
