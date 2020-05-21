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
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.util.xmlb.annotations.Property;
import com.intellij.util.xmlb.annotations.XCollection;
import com.mallowigi.config.AtomSettingsBundle;
import com.mallowigi.icons.FileInfo;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Represents a list of association parsed from the XML
 */
public final class Associations implements Serializable {
  private static final Logger LOG = Logger.getInstance(Associations.class);

  @NonNls
  private static final Set<String> IMAGE_TYPES = ImmutableSet.of("Images", "SVG");
  @Property
  @XCollection
  private final List<RegexAssociation> customAssociations;
  private List<Association> associations;

  public Associations() {
    associations = Collections.emptyList();
    customAssociations = Collections.emptyList();
  }

  public Associations(final List<RegexAssociation> associations) {
    this.associations = Collections.emptyList();
    customAssociations = Collections.unmodifiableList(associations);
  }

  public List<Association> getAssociations() {
    return Collections.unmodifiableList(associations);
  }

  public void setAssociations(final List<? extends Association> associations) {
    this.associations = Collections.unmodifiableList(associations);
  }

  /**
   * Find the Association for the given FileInfo
   *
   * @param file file
   */
  @Nullable
  public Association findAssociationForFile(final FileInfo file) {
    final Association matching;
    // First check in custom assocs
    if (customAssociations != null) {
      matching = customAssociations.stream()
        .filter(association -> association.matches(file))
        .findAny()
        .orElse(null);
    }
    else {
      matching = associations.stream()
        .filter(association -> association.matches(file))
        .findAny()
        .orElse(null);
    }

    if (matching != null && IMAGE_TYPES.contains(matching.getName())) {
      try {
        // If those plugins are installed, let them handle the icon
        final String pluginID = AtomSettingsBundle.message("plugins.iconViewer");
        final String imageIconViewerID = AtomSettingsBundle.message("plugins.imageIconViewer");

        final IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId(pluginID));
        final IdeaPluginDescriptor plugin2 = PluginManagerCore.getPlugin(PluginId.getId(imageIconViewerID));

        if (plugin != null || plugin2 != null) {
          return null;
        }
      }
      catch (final RuntimeException e) {
        LOG.error(e);
      }
    }

    return matching;
  }

  public List<RegexAssociation> getCustomAssociations() {
    return Collections.unmodifiableList(customAssociations);
  }

}
