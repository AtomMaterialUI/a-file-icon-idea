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

package com.mallowigi.icons.services;

import com.mallowigi.icons.associations.Association;
import com.mallowigi.icons.associations.Associations;
import com.mallowigi.icons.associations.RegexAssociation;
import com.mallowigi.icons.associations.TypeAssociation;
import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NonNls;

import java.net.URL;

@NonNls
public enum AssociationsFactory {
  DEFAULT;

  /**
   * Parse icon_associations.xml to build the list of Associations
   */
  @SuppressWarnings("CastToConcreteClass")
  public static Associations create(final String associationsFile) {
    final URL associationsXml = AssociationsFactory.class.getResource(associationsFile);
    @NonNls final XStream xStream = new XStream();
    XStream.setupDefaultSecurity(xStream);
    xStream.allowTypesByWildcard(new String[]{"com.mallowigi.icons.associations.*"});

    xStream.alias("associations", Associations.class);
    xStream.alias("regex", RegexAssociation.class);
    xStream.alias("type", TypeAssociation.class);

    xStream.useAttributeFor(Association.class, "icon");
    xStream.useAttributeFor(Association.class, "name");
    xStream.useAttributeFor(RegexAssociation.class, "pattern");
    xStream.useAttributeFor(TypeAssociation.class, "type");

    try {
      return (Associations) xStream.fromXML(associationsXml);
    }
    catch (final RuntimeException e) {
      return new Associations();
    }
  }

}
