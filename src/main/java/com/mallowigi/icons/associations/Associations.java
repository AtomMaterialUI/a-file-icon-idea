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

package com.mallowigi.icons.associations;

import com.google.common.collect.ImmutableSet;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.mallowigi.config.AtomSettingsBundle;
import com.mallowigi.icons.FileInfo;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Represents a list of association parsed from the XML
 */
public final class Associations implements Serializable {

  /**
   * Image types
   */
  @NonNls
  private static final Set<String> IMAGE_TYPES = ImmutableSet.of("Images", "SVG");

  /**
   * Parsed associations
   */
  @SuppressWarnings("InstanceVariableMayNotBeInitialized")
  private List<? extends Association> associations;

  /**
   * Return parsed associations
   */
  public List<Association> getAssociations() {
    return Collections.unmodifiableList(associations);
  }

  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public void setAssociations(final List<? extends Association> associations) {
    this.associations = associations;
  }

  /**
   * Find the Association for the given FileInfo
   *
   * @param file file
   */
  @Nullable
  public Association findAssociationForFile(final FileInfo file) {
    final Association result = associations.stream()
      .filter((Predicate<Association>) association -> association.matches(file))
      .findAny()
      .orElse(null);

    if (result != null && IMAGE_TYPES.contains(result.getName())) {
      try {
        // If those plugins are installed, let them handle the icon
        final IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId(AtomSettingsBundle.message("plugins.iconViewer")));
        final IdeaPluginDescriptor plugin2 = PluginManager.getPlugin(
          PluginId.getId(AtomSettingsBundle.message("plugins.imageIconViewer")));

        if (plugin != null || plugin2 != null) {
          return null;
        }
      }
      catch (final RuntimeException e) {
        e.printStackTrace();
      }
    }

    return result;
  }

}
