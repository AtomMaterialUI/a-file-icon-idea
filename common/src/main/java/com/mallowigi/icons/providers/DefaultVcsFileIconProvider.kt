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

package com.mallowigi.icons.providers

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.mallowigi.config.AtomFileIconsConfig
import com.mallowigi.config.select.AtomSelectConfig
import com.mallowigi.icons.associations.SelectedAssociations
import com.mallowigi.models.IconType
import icons.AtomIcons
import javax.swing.Icon

/** Default file icon provider. */
class DefaultVcsFileIconProvider : AbstractVcsFileIconProvider() {

  override fun getIcon(iconPath: String): Icon =
    AtomIcons.loadIconWithFallback(AtomIcons.getFileIcon(iconPath), iconPath)

  override fun getSource(): SelectedAssociations = AtomSelectConfig.instance.selectedFileAssociations

  override fun getType(): IconType = IconType.FILE

  override fun isDefault(): Boolean = true

  override fun isNotApplicable(): Boolean =
    AtomFileIconsConfig.instance.isLowPowerMode || !AtomFileIconsConfig.instance.isEnabledIcons

  override fun isOfType(element: PsiElement): Boolean = element is PsiFile

}
