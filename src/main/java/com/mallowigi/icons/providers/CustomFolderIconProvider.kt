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

package com.mallowigi.icons.providers

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.config.associations.AtomAssocConfig
import icons.MTIcons
import javax.swing.Icon

class CustomFolderIconProvider : AbstractFileIconProvider() {
  override fun getSource() = AtomAssocConfig.instance.customFolderAssociations

  override fun getType(): IconType = IconType.FOLDER

  override fun isDefault(): Boolean = false

  override fun isOfType(element: PsiElement): Boolean = element is PsiDirectory

  override fun getIcon(iconPath: String): Icon? = MTIcons.loadSVGIcon(iconPath)

  override fun isNotAppliable() = !AtomFileIconsConfig.instance.isEnabledDirectories

}