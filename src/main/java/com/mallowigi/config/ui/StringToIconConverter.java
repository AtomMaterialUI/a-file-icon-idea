package com.mallowigi.config.ui;

import com.intellij.openapi.util.IconLoader;
import org.jdesktop.beansbinding.Converter;

import javax.swing.*;

public final class StringToIconConverter extends Converter<String, Icon> {
  @Override
  public Icon convertForward(final String value) {
    return IconLoader.getIcon(value);
  }

  @Override
  public String convertReverse(final Icon value) {
    return value.toString();
  }
}
