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

import com.intellij.util.xmlb.annotations.Property;
import com.mallowigi.icons.FileInfo;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Association for Regular Expressions
 */
public final class RegexAssociation extends Association {
  @Property
  private String pattern;

  private transient Pattern compiledPattern;

  public RegexAssociation() {
    this("", "", "");
  }

  public RegexAssociation(@NotNull final String name,
                          @NotNull final String pattern,
                          @NotNull final String icon) {
    super(name, icon);
    this.pattern = pattern;
  }

  public RegexAssociation(final Association association) {
    this(association.getName(),
         association.getMatcher(),
         association.getIcon()
    );
  }

  @Override
  public boolean matches(final FileInfo file) {
    if (compiledPattern == null) {
      compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }
    return compiledPattern.matcher(file.getName()).matches();
  }

  @SuppressWarnings("CallToSimpleGetterFromWithinClass")
  @Override
  public String getMatcher() {
    return getPattern();
  }

  @SuppressWarnings("CallToSimpleSetterFromWithinClass")
  @Override
  public void setMatcher(final String value) {
    setPattern(value);
  }

  @Override
  public void apply(final Association other) {
    super.apply(other);
    pattern = other.getMatcher();
  }

  @Override
  public boolean isEmpty() {
    return super.isEmpty() || pattern.isEmpty();
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(final String pattern) {
    this.pattern = pattern;
  }
}
