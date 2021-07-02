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
package icons

import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.Ref
import com.intellij.ui.scale.ScaleContext
import com.intellij.util.IconUtil
import com.intellij.util.SVGLoader
import com.mallowigi.icons.special.DirIcon
import org.jetbrains.annotations.NonNls
import java.awt.Image
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.logging.Logger
import javax.swing.Icon

@Suppress("UndocumentedPublicClass")
object MTIcons {
  private const val FILES_PATH: String = "/iconGenerator/assets"
  private const val FOLDERS_PATH: String = "/iconGenerator/assets/icons/folders"
  private const val FOLDERS_OPEN_PATH: String = "/iconGenerator/assets/icons/foldersOpen"
  val EXCLUDED: Icon = load("/icons/mt/modules/ExcludedTreeOpen.svg")
  val MODULE: Icon = load("/icons/mt/modules/ModuleOpen.svg")
  val SOURCE: Icon = load("/icons/mt/modules/sourceRootOpen.svg")
  val TEST: Icon = load("/icons/mt/modules/testRootOpen.svg")
  val SEARCH_WITH_HISTORY_HOVERED: Icon = load("/icons/actions/searchWithHistoryHovered.svg")
  val SEARCH_WITH_HISTORY: Icon = load("/icons/actions/searchWithHistory.svg")
  val SEARCH: Icon = load("/icons/actions/search.svg")
  val CLEAR: Icon = load("/icons/actions/clear.svg")
  val EYE_ON: Icon = load("/icons/mt/eye.svg")
  val EYE_OFF: Icon = load("/icons/mt/eyeOff.svg")

  fun getFileIcon(iconPath: String): Icon = IconLoader.getIcon(FILES_PATH + iconPath, MTIcons.javaClass)

  fun getFolderIcon(iconPath: String): DirIcon {
    return DirIcon(
      IconLoader.getIcon(FOLDERS_PATH + iconPath, MTIcons.javaClass),
      IconLoader.getIcon(FOLDERS_OPEN_PATH + iconPath, MTIcons.javaClass)
    )
  }

  private fun load(@NonNls path: String): Icon = IconLoader.findIcon(path)!!

  @Throws(IOException::class)
  fun loadSVGIcon(canonicalPath: String): Icon {
    val url = Ref.create<URL>()
    try {
      url.set(File(canonicalPath).toURI().toURL())
    } catch (e: MalformedURLException) {
      Logger.getAnonymousLogger().info(e.message)
    }
    val bufferedImage: Image = SVGLoader.loadHiDPI(url.get(), FileInputStream(canonicalPath), ScaleContext.create())
    return IconUtil.toSize(IconUtil.createImageIcon(bufferedImage), 16, 16)
  }

  object Arrows {
    var MaterialDownSelected: Icon = load("/icons/mac/material/down_selected.svg")
    var MaterialRightSelected: Icon = load("/icons/mac/material/right_selected.svg")
    var DarculaDownSelected: Icon = load("/icons/mac/darcula/down_selected.svg")
    var DarculaRightSelected: Icon = load("/icons/mac/darcula/right_selected.svg")
    var PlusSelected: Icon = load("/icons/mac/plusminus/plus_selected.svg")
    var MinusSelected: Icon = load("/icons/mac/plusminus/minus_selected.svg")
    var DownSelected: Icon = load("/icons/mac/arrow/down_selected.svg")
    var RightSelected: Icon = load("/icons/mac/arrow/right_selected.svg")
    var MaterialDown: Icon = load("/icons/mac/material/down.svg")
    var MaterialRight: Icon = load("/icons/mac/material/right.svg")
    var DarculaDown: Icon = load("/icons/mac/darcula/down.svg")
    var DarculaRight: Icon = load("/icons/mac/darcula/right.svg")
    var Plus: Icon = load("/icons/mac/plusminus/plus.svg")
    var Minus: Icon = load("/icons/mac/plusminus/minus.svg")
    var Down: Icon = load("/icons/mac/arrow/down.svg")
    var Right: Icon = load("/icons/mac/arrow/right.svg")
  }

  object Nodes2 {
    val FolderOpen: Icon = load("/icons/nodes/folderOpen.svg")
  }
}
