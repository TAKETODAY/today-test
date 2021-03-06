/*
 * Original Author -> Harry Yang (taketoday@foxmail.com) https://taketoday.cn
 * Copyright © TODAY & 2017 - 2021 All Rights Reserved.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/]
 */

package cn.taketoday.test.context.support;

import cn.taketoday.beans.factory.support.BeanDefinitionReader;
import cn.taketoday.beans.factory.xml.XmlBeanDefinitionReader;
import cn.taketoday.context.support.DefaultApplicationContext;
import cn.taketoday.test.context.MergedContextConfiguration;
import cn.taketoday.util.ObjectUtils;

/**
 * Concrete implementation of {@link AbstractGenericContextLoader} that reads
 * bean definitions from XML resources.
 *
 * <p>Default resource locations are detected using the suffix
 * {@code "-context.xml"}.
 *
 * @author Sam Brannen
 * @see XmlBeanDefinitionReader
 * @see GenericGroovyXmlContextLoader
 * @see AnnotationConfigContextLoader
 */
public class GenericXmlContextLoader extends AbstractGenericContextLoader {

  /**
   * Create a new {@link XmlBeanDefinitionReader}.
   *
   * @return a new {@code XmlBeanDefinitionReader}
   */
  @Override
  protected BeanDefinitionReader createBeanDefinitionReader(DefaultApplicationContext context) {
    return new XmlBeanDefinitionReader(context);
  }

  /**
   * Returns {@code "-context.xml"} in order to support detection of a
   * default XML config file.
   */
  @Override
  protected String getResourceSuffix() {
    return "-context.xml";
  }

  /**
   * Ensure that the supplied {@link MergedContextConfiguration} does not
   * contain {@link MergedContextConfiguration#getClasses() classes}.
   *
   * @see AbstractGenericContextLoader#validateMergedContextConfiguration
   */
  @Override
  protected void validateMergedContextConfiguration(MergedContextConfiguration mergedConfig) {
    if (mergedConfig.hasClasses()) {
      String msg = String.format(
              "Test class [%s] has been configured with @ContextConfiguration's 'classes' attribute %s, "
                      + "but %s does not support annotated classes.", mergedConfig.getTestClass().getName(),
              ObjectUtils.nullSafeToString(mergedConfig.getClasses()), getClass().getSimpleName());
      logger.error(msg);
      throw new IllegalStateException(msg);
    }
  }

}
