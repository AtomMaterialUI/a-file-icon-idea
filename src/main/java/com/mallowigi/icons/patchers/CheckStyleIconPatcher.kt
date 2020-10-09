/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2020 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.icons.patchers

import com.intellij.openapi.util.IconPathPatcher
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.util.*

class CheckStyleIconPatcher : IconPathPatcher() {
  companion object {
    @NonNls
    private val REPLACEMENTS: MutableMap<String, String> = HashMap()

    init {
      REPLACEMENTS["/general/autoscrollToSource.png"] = "AllIcons.General.AutoscrollToSource"
      REPLACEMENTS["/actions/expandall.png"] = "AllIcons.Actions.Expandall"
      REPLACEMENTS["/actions/collapseall.png"] = "AllIcons.Actions.Collapseall"
      REPLACEMENTS["/actions/cancel.png"] = "AllIcons.Actions.Cancel"
      REPLACEMENTS["/actions/suspend.png"] = "AllIcons.Actions.Suspend"
      REPLACEMENTS["/actions/execute.png"] = "AllIcons.Actions.Execute"
      REPLACEMENTS["/modules/modulesNode.png"] = "AllIcons.Nodes.ModuleGroup"
      REPLACEMENTS["/general/projectTab.png"] = "AllIcons.General.ProjectTab"
      REPLACEMENTS["/general/toolWindowChanges.png"] = "AllIcons.Toolwindows.ToolWindowChanges"
      REPLACEMENTS["/general/smallConfigurableVcs.png"] = "AllIcons.Actions.ShowAsTree"
    }
  }

  override fun patchPath(path: @NotNull String, classLoader: @Nullable ClassLoader?): @Nullable String? {
    return REPLACEMENTS[path]
  }
}