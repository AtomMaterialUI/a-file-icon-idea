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
package icons

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.io.FileUtil.toCanonicalPath
import com.intellij.openapi.vfs.VFileProperty
import com.intellij.openapi.vfs.VirtualFile
import com.mallowigi.icons.special.DirIcon
import com.mallowigi.utils.LayeredIconService
import org.jetbrains.annotations.NonNls
import java.io.IOException
import java.util.logging.Logger
import javax.swing.Icon

/** Loader for Plugin's Icons. */
object AtomIcons {
  private const val FILES_PATH: String = "/assets"
  private const val FOLDERS_PATH: String = "/assets/icons/folders"
  private const val FOLDERS_OPEN_PATH: String = "/assets/icons/foldersOpen"
  val EXCLUDED: Icon = load("/icons/mt/modules/ExcludedTreeOpen.svg")
  val MODULE: Icon = load("/icons/mt/modules/ModuleOpen.svg")
  val SOURCE: Icon = load("/icons/mt/modules/sourceRootOpen.svg")
  val TEST: Icon = load("/icons/mt/modules/testRootOpen.svg")
  val LOGO: Icon = load("/logo.svg")

  /**
   * Get file icon from the resources folder
   *
   * @param iconPath path without the prefix
   * @return the full path
   */
  fun getFileIcon(iconPath: String): Icon =
    IconLoader.getIcon(toCanonicalPath(FILES_PATH) + toCanonicalPath(iconPath), AtomIcons.javaClass)

  /**
   * Get folder icons from the resources folder
   *
   * @param iconPath path without the prefix
   * @return the DirIcon (closed+opened)
   */
  fun getFolderIcon(iconPath: String): DirIcon = DirIcon(
    IconLoader.getIcon(toCanonicalPath(FOLDERS_PATH) + toCanonicalPath(iconPath), AtomIcons.javaClass),
    IconLoader.getIcon(toCanonicalPath(FOLDERS_OPEN_PATH) + toCanonicalPath(iconPath), AtomIcons.javaClass),
  )

  /**
   * Loads an icon
   *
   * @param path absolute path to the icon
   * @return the icon. must not be null
   */
  internal fun load(@NonNls path: String): Icon = IconLoader.findIcon(toCanonicalPath(path), AtomIcons.javaClass)!!

  /**
   * Tries to load a svg icon
   *
   * @param canonicalPath
   * @return
   */
  @Throws(IOException::class)
  fun loadSVGIcon(canonicalPath: String): Icon {
    return try {
      IconLoader.getIcon(canonicalPath, AtomIcons.javaClass)
    } catch (e: Exception) {
      Logger.getAnonymousLogger().info(e.message)
      throw e
    }
  }

  /**
   * If the icon's height is 1, load a fallback icon, otherwise return the
   * icon
   *
   * @param icon The icon to use if the SVG icon can't be loaded.
   * @param path The path to the SVG file.
   */
  fun loadIconWithFallback(icon: Icon, path: String): Icon = if (icon.iconHeight == 1) loadSVGIcon(path) else icon

  /**
   * If the file is a symlink, add the symlink icon to the file's icon;
   * if the file is not writable, add the locked icon the file's icon;
   * otherwise, return the file's icon
   *
   * @param icon The icon to be decorated.
   * @param virtualFile VirtualFile — the file to get the icon for
   */
  fun getLayeredIcon(icon: Icon, virtualFile: VirtualFile): Icon = when {
    virtualFile.`is`(VFileProperty.SYMLINK) -> LayeredIconService.create(icon, AllIcons.Nodes.Symlink)
    !virtualFile.isWritable -> LayeredIconService.create(icon, AllIcons.Nodes.Locked)
    else -> icon
  }

  object Settings {
    val FILES: Icon = load("/settings/atom.svg")
    val FOLDERS: Icon = load("/settings/compiledClassesFolder.svg")
    val MONOCHROME: Icon = load("/settings/monochrome.svg")
    val SATURATION: Icon = load("/settings/saturation.svg")
    val UI: Icon = load("/settings/plugin.svg")
    val PSI: Icon = load("/settings/psi.svg")
    val HIDE_FILES: Icon = load("/settings/hideFileAction.svg")
    val HIDE_FOLDERS: Icon = load("/settings/hideFolderAction.svg")
    val HOLLOW: Icon = load("/settings/folderOpen.svg")
    val SIZE: Icon = load("/settings/plus.svg")
    val LINE_HEIGHT: Icon = load("/settings/lineHeight.svg")
    val ARROWS: Icon = load("/settings/arrowRight.svg")
    val ACCENT: Icon = load("/settings/accentColor.svg")
    val THEMED: Icon = load("/settings/folder.svg")

    val RUBY: Icon = load("/settings/ruby.svg")
    val RAILS: Icon = load("/settings/rails.svg")
    val ANGULAR: Icon = load("/settings/angular.svg")
    val ANGULAR2: Icon = load("/settings/angular2.svg")
    val NEST: Icon = load("/settings/nest.svg")
    val REDUX: Icon = load("/settings/redux.svg")
    val NGRX: Icon = load("/settings/ngrx.svg")
    val NEXTJS: Icon = load("/settings/nextjs.svg")
    val RECOIL: Icon = load("/settings/recoil.svg")
    val TESTS: Icon = load("/settings/tests.svg")
  }

  /** Node icons. */
  object Nodes2 {
    val FolderOpen: Icon = load("/icons/nodes/folderOpen.svg")
  }
}
