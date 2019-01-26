/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Chris Magnussen and Elior Boukhobza
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

package com.mallowigi.icons;

import com.intellij.ide.IconProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import com.mallowigi.config.AtomFileIconsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provider for file icons
 */
public final class FileIconProvider extends IconProvider implements DumbAware {

  private final Associations associations = Associations.AssociationsFactory.create("/icon_associations.xml");
  private final Associations dirAssociations = Associations.AssociationsFactory.create("/folder_associations.xml");

  @Nullable
  @Override
  public Icon getIcon(@NotNull final PsiElement psiElement, final int i) {
    Icon icon = null;
    final boolean enabledIcons = AtomFileIconsConfig.getInstance().isEnabledIcons();
    final boolean enabledDirectories = AtomFileIconsConfig.getInstance().isEnabledDirectories();

    if (enabledDirectories && psiElement instanceof PsiDirectory) {
      icon = getDirectoryIcon(psiElement);
    } else if (enabledIcons && psiElement instanceof PsiFile) {
      final VirtualFile virtualFile = PsiUtilCore.getVirtualFile(psiElement);
      if (virtualFile != null) {
        final FileInfo file = new VirtualFileInfo(virtualFile);
        icon = getIconForAssociation(file, associations.findAssociationForFile(file));
      }
    }

    return icon;
  }

  private Icon getDirectoryIcon(final PsiElement psiElement) {
    Icon icon = null;
    final VirtualFile virtualFile = PsiUtilCore.getVirtualFile(psiElement);
    if (virtualFile != null) {
      final FileInfo file = new VirtualFileInfo(virtualFile);
      icon = getDirectoryIconForAssociation(file, dirAssociations.findAssociationForFile(file));
    }
    return icon;
  }

  /**
   * Get the relevant icon for association
   *
   * @param file        a file
   * @param association the found association
   * @return the replaced icon, or null if not found
   */
  private Icon getIconForAssociation(final FileInfo file, final Association association) {
    final boolean isInputInvalid = association == null || association.getIcon() == null;
    return isInputInvalid ? null : loadIcon(file, association);
  }

  private Icon getDirectoryIconForAssociation(final FileInfo file, final Association association) {
    final boolean isInputInvalid = association == null || association.getIcon() == null;
    return isInputInvalid ? null : loadIcon(file, association);
  }

  /**
   * Find the icon specified by the association and load it
   *
   * @param file        the file
   * @param association the association found
   * @return the icon if found, null otherwise
   */
  private Icon loadIcon(final FileInfo file, final Association association) {
    Icon icon = null;

    try {
      icon = IconLoader.getIcon(association.getIcon());
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return icon;
  }
}
