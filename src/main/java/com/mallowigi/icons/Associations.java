/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Chris Magnussen and Elior Boukhobza
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

import com.google.common.annotations.VisibleForTesting;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.mallowigi.icons.associations.PsiElementAssociation;
import com.mallowigi.icons.associations.RegexAssociation;
import com.mallowigi.icons.associations.TypeAssociation;
import com.mallowigi.icons.utils.StaticPatcher;
import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

/**
 * Represents a list of association parsed from the XML
 */
public final class Associations implements Serializable {

  private List<Association> associations;

  public List<Association> getAssociations() {
    return associations;
  }

  public void setAssociations(List<Association> associations) {
    this.associations = associations;
  }

  /**
   * Find the Association for the given FileInfo
   *
   * @param file a file
   * @return the association if found
   */
  @VisibleForTesting
  @Nullable
  protected Association findAssociationForFile(FileInfo file) {
    Association result = null;
    for (Association association : associations) {
      if (association.matches(file)) {
        result = association;
        break;
      }
    }

    // Special case for images
    if (result != null && "Images".equals(result.getName())) {
      try {
        // Icon viewer plugin
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("ch.dasoft.iconviewer"));
        if (plugin != null) {
          return null;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * Parse XML files to build the list of associations
   */
  static final class AssociationsFactory {
    /**
     * Parse icon_associations.xml to build the list of Associations
     *
     * @return the associations list
     */
    static Associations create() {
      URL associationsXml = AssociationsFactory.class.getResource("/icon_associations.xml");
      XStream xStream = new XStream();
      xStream.alias("associations", Associations.class);
      xStream.alias("regex", RegexAssociation.class);
      xStream.alias("type", TypeAssociation.class);

      if (StaticPatcher.isClass("com.intellij.psi.PsiClass")) {
        xStream.alias("psi", PsiElementAssociation.class);
      } else {
        xStream.alias("psi", TypeAssociation.class);
      }

      xStream.useAttributeFor(Association.class, "icon");
      xStream.useAttributeFor(Association.class, "name");
      xStream.useAttributeFor(RegexAssociation.class, "pattern");
      xStream.useAttributeFor(TypeAssociation.class, "type");

      if (StaticPatcher.isClass("com.intellij.psi.PsiClass")) {
        xStream.useAttributeFor(PsiElementAssociation.class, "type");
      }

      return (Associations) xStream.fromXML(associationsXml);
    }
  }
}
