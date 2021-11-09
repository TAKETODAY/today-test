/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.taketoday.test.context.web;

import cn.taketoday.test.context.SmartContextLoader;
import cn.taketoday.test.context.support.AbstractDelegatingSmartContextLoader;

/**
 * {@code WebDelegatingSmartContextLoader} is a concrete implementation of
 * {@link AbstractDelegatingSmartContextLoader} that delegates to a
 * {@link GenericXmlWebContextLoader} (or a {@link GenericGroovyXmlWebContextLoader} if
 * Groovy is present on the classpath) and an {@link AnnotationConfigWebContextLoader}.
 *
 * @author Sam Brannen
 * @see SmartContextLoader
 * @see AbstractDelegatingSmartContextLoader
 * @see GenericXmlWebContextLoader
 * @see AnnotationConfigWebContextLoader
 */
public class WebDelegatingSmartContextLoader extends AbstractDelegatingSmartContextLoader {

  private final SmartContextLoader xmlLoader;
  private final SmartContextLoader annotationConfigLoader;

  public WebDelegatingSmartContextLoader() {
    this.xmlLoader = new GenericXmlWebContextLoader();
    this.annotationConfigLoader = new AnnotationConfigWebContextLoader();
  }

  @Override
  protected SmartContextLoader getXmlLoader() {
    return this.xmlLoader;
  }

  @Override
  protected SmartContextLoader getAnnotationConfigLoader() {
    return this.annotationConfigLoader;
  }

}
