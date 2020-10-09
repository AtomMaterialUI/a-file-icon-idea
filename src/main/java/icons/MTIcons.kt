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

object MTIcons {
  const val FILES_PATH = "/iconGenerator/assets"
  const val FOLDERS_PATH = "/iconGenerator/assets/icons/folders"
  const val FOLDERS_OPEN_PATH = "/iconGenerator/assets/icons/foldersOpen"
  val EXCLUDED = load("/icons/mt/modules/ExcludedTreeOpen.svg")
  val MODULE = load("/icons/mt/modules/ModuleOpen.svg")
  val SOURCE = load("/icons/mt/modules/sourceRootOpen.svg")
  val TEST = load("/icons/mt/modules/testRootOpen.svg")
  val SEARCH_WITH_HISTORY_HOVERED = load("/icons/actions/searchWithHistoryHovered.svg")
  val SEARCH_WITH_HISTORY = load("/icons/actions/searchWithHistory.svg")
  val SEARCH = load("/icons/actions/search.svg")
  val CLEAR = load("/icons/actions/clear.svg")
  val EYE_ON = load("/icons/mt/eye.svg")
  val EYE_OFF = load("/icons/mt/eyeOff.svg")

  fun getFileIcon(iconPath: String): Icon {
    return IconLoader.getIcon(FILES_PATH + iconPath, MTIcons.javaClass)
  }

  fun getFolderIcon(iconPath: String): DirIcon {
    return DirIcon(
      IconLoader.getIcon(FOLDERS_PATH + iconPath, MTIcons.javaClass),
      IconLoader.getIcon(FOLDERS_OPEN_PATH + iconPath, MTIcons.javaClass)
    )
  }

  private fun load(@NonNls path: String): Icon {
    return IconLoader.findIcon(path)!!
  }

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
    var MaterialDownSelected = load("/icons/mac/material/down_selected.svg")
    var MaterialRightSelected = load("/icons/mac/material/right_selected.svg")
    var DarculaDownSelected = load("/icons/mac/darcula/down_selected.svg")
    var DarculaRightSelected = load("/icons/mac/darcula/right_selected.svg")
    var PlusSelected = load("/icons/mac/plusminus/plus_selected.svg")
    var MinusSelected = load("/icons/mac/plusminus/minus_selected.svg")
    var DownSelected = load("/icons/mac/arrow/down_selected.svg")
    var RightSelected = load("/icons/mac/arrow/right_selected.svg")
    var MaterialDown = load("/icons/mac/material/down.svg")
    var MaterialRight = load("/icons/mac/material/right.svg")
    var DarculaDown = load("/icons/mac/darcula/down.svg")
    var DarculaRight = load("/icons/mac/darcula/right.svg")
    var Plus = load("/icons/mac/plusminus/plus.svg")
    var Minus = load("/icons/mac/plusminus/minus.svg")
    var Down = load("/icons/mac/arrow/down.svg")
    var Right = load("/icons/mac/arrow/right.svg")
  }

  object Nodes2 {
    val FolderOpen = load("/icons/nodes/folderOpen.svg")
  }
}