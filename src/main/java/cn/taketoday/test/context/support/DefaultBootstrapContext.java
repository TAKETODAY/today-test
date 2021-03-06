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

import cn.taketoday.core.style.ToStringBuilder;
import cn.taketoday.lang.Assert;
import cn.taketoday.test.context.BootstrapContext;
import cn.taketoday.test.context.CacheAwareContextLoaderDelegate;

/**
 * Default implementation of the {@link BootstrapContext} interface.
 *
 * @author Sam Brannen
 */
public class DefaultBootstrapContext implements BootstrapContext {

  private final Class<?> testClass;
  private final CacheAwareContextLoaderDelegate cacheAwareContextLoaderDelegate;

  /**
   * Construct a new {@code DefaultBootstrapContext} from the supplied arguments.
   *
   * @param testClass the test class for this bootstrap context; never {@code null}
   * @param cacheAwareContextLoaderDelegate the context loader delegate to use for
   * transparent interaction with the {@code ContextCache}; never {@code null}
   */
  public DefaultBootstrapContext(Class<?> testClass, CacheAwareContextLoaderDelegate cacheAwareContextLoaderDelegate) {
    Assert.notNull(testClass, "Test class must not be null");
    Assert.notNull(cacheAwareContextLoaderDelegate, "CacheAwareContextLoaderDelegate must not be null");
    this.testClass = testClass;
    this.cacheAwareContextLoaderDelegate = cacheAwareContextLoaderDelegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getTestClass() {
    return this.testClass;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CacheAwareContextLoaderDelegate getCacheAwareContextLoaderDelegate() {
    return this.cacheAwareContextLoaderDelegate;
  }

  /**
   * Provide a String representation of this bootstrap context's state.
   */
  @Override
  public String toString() {
    return new ToStringBuilder(this)//
            .append("testClass", this.testClass.getName())//
            .append("cacheAwareContextLoaderDelegate", this.cacheAwareContextLoaderDelegate.getClass().getName())//
            .toString();
  }

}
