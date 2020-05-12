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

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.xmlb.annotations.Property;
import com.mallowigi.icons.FileInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.regex.Pattern;

/**
 * Association for Regular Expressions
 */
public final class RegexAssociation extends Association {
  @Property
  private String pattern;

  private transient Pattern compiledPattern;

  public RegexAssociation() {
    name = "";
    pattern = "";
    icon = "";
  }

  public RegexAssociation(@NotNull final String name,
                          @NotNull final String pattern,
                          @NotNull final String icon) {
    this.name = name;
    this.pattern = pattern;
    this.icon = icon;
  }

  @Override
  public Icon getIconForFile(final FileInfo file) {
    return IconLoader.getIcon(getIcon());
  }

  @Override
  public String getMatcher() {
    return pattern;
  }

  @Override
  public boolean matches(final FileInfo file) {
    if (compiledPattern == null) {
      compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }
    return compiledPattern.matcher(file.getName()).matches();
  }

  public RegexAssociation apply(final RegexAssociation other) {
    name = other.getName();
    pattern = other.getMatcher();
    icon = other.getIcon();
    return this;
  }

  public boolean isEmpty() {
    return name.isEmpty() || pattern.isEmpty() || icon.isEmpty();
  }

  public void setIcon(final String value) {
    icon = value;
  }

  public void setName(final String value) {
    name = value;
  }

  public void setPattern(final String value) {
    pattern = value;
  }
}
