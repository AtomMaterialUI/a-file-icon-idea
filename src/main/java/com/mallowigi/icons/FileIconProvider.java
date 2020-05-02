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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import com.mallowigi.config.AtomFileIconsConfig;
import com.mallowigi.icons.associations.Association;
import com.mallowigi.icons.associations.Associations;
import com.mallowigi.icons.associations.AssociationsFactory;
import icons.MTIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provider for file icons
 */
public final class FileIconProvider extends IconProvider implements DumbAware {

  private static final Associations associations = AssociationsFactory.create("/iconGenerator/icon_associations.xml");
  private static final Associations dirAssociations = AssociationsFactory.create("/iconGenerator/folder_associations.xml");

  @Nullable
  @Override
  public Icon getIcon(@NotNull final PsiElement element, final int flags) {
    Icon icon = null;

    if (element instanceof PsiDirectory) {
      icon = getDirectoryIcon(element);
    }
    else if (element instanceof PsiFile) {
      icon = getFileIcon(element);
    }

    return icon;
  }

  public static Associations getAssociations() {
    return associations;
  }

  public static Associations getDirAssociations() {
    return dirAssociations;
  }

  private static Icon getFileIcon(final PsiElement psiElement) {
    Icon icon = null;
    if (!AtomFileIconsConfig.getInstance().isEnabledIcons()) {
      return null;
    }

    final VirtualFile virtualFile = PsiUtilCore.getVirtualFile(psiElement);
    if (virtualFile != null) {
      final FileInfo file = new VirtualFileInfo(psiElement, virtualFile);
      icon = getIconForAssociation(file, associations.findAssociationForFile(file));
    }
    return icon;
  }

  private static DirIcon getDirectoryIcon(final PsiElement psiElement) {
    DirIcon icon = null;
    if (!AtomFileIconsConfig.getInstance().isEnabledDirectories()) {
      return null;
    }

    final VirtualFile virtualFile = PsiUtilCore.getVirtualFile(psiElement);
    if (virtualFile != null) {
      final FileInfo file = new VirtualFileInfo(psiElement, virtualFile);
      icon = getDirectoryIconForAssociation(dirAssociations.findAssociationForFile(file));
    }
    return icon;
  }

  /**
   * Get the relevant icon for association
   */
  private static Icon getIconForAssociation(final FileInfo file, final Association association) {
    final boolean isInputInvalid = association == null || association.getIcon() == null;
    return isInputInvalid ? null : loadIcon(file, association);
  }

  private static DirIcon getDirectoryIconForAssociation(final Association association) {
    final boolean isInputInvalid = association == null || association.getIcon() == null;
    return isInputInvalid ? null : loadDirIcon(association);
  }

  /**
   * Load the association's icon
   */
  private static Icon loadIcon(final FileInfo file, final Association association) {
    Icon icon = null;

    try {
      final String iconPath = association.getIcon();
      icon = MTIcons.getFileIcon(iconPath);
    }
    catch (final RuntimeException e) {
      e.printStackTrace();
    }
    return icon;
  }

  /**
   * Load the association's icon
   */
  private static DirIcon loadDirIcon(final Association association) {
    DirIcon icon = null;

    try {
      final String iconPath = association.getIcon();
      icon = MTIcons.getFolderIcon(iconPath);
    }
    catch (final RuntimeException e) {
      e.printStackTrace();
    }
    return icon;
  }
}
