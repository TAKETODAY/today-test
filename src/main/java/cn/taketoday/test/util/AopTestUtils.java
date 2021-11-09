/*
 * Copyright 2002-2021 the original author or authors.
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

package cn.taketoday.test.util;

import cn.taketoday.aop.proxy.Advised;
import cn.taketoday.aop.support.AopUtils;
import cn.taketoday.lang.Assert;

/**
 * {@code AopTestUtils} is a collection of AOP-related utility methods for
 * use in unit and integration testing scenarios.
 *
 * <p>For Spring's core AOP utilities, see
 * {@link cn.taketoday.aop.support.AopUtils AopUtils} and
 * {@link cn.taketoday.aop.proxy.AopProxyUtils AopProxyUtils}.
 *
 * @author Sam Brannen
 * @author Juergen Hoeller
 * @see cn.taketoday.aop.support.AopUtils
 * @see cn.taketoday.aop.proxy.AopProxyUtils
 * @see ReflectionTestUtils
 */
public abstract class AopTestUtils {

  /**
   * Get the <em>target</em> object of the supplied {@code candidate} object.
   * <p>If the supplied {@code candidate} is a Spring
   * {@linkplain AopUtils#isAopProxy proxy}, the target of the proxy will
   * be returned; otherwise, the {@code candidate} will be returned
   * <em>as is</em>.
   *
   * @param candidate the instance to check (potentially a Spring AOP proxy;
   * never {@code null})
   * @return the target object or the {@code candidate} (never {@code null})
   * @throws IllegalStateException if an error occurs while unwrapping a proxy
   * @see Advised#getTargetSource()
   * @see #getUltimateTargetObject
   */
  @SuppressWarnings("unchecked")
  public static <T> T getTargetObject(Object candidate) {
    Assert.notNull(candidate, "Candidate must not be null");
    try {
      if (AopUtils.isAopProxy(candidate) && candidate instanceof Advised) {
        Advised advised = (Advised) candidate;
        Object target = advised.getTargetSource().getTarget();
        if (target != null) {
          return (T) target;
        }
      }
    }
    catch (Throwable ex) {
      throw new IllegalStateException("Failed to unwrap proxied object", ex);
    }
    return (T) candidate;
  }

  /**
   * Get the ultimate <em>target</em> object of the supplied {@code candidate}
   * object, unwrapping not only a top-level proxy but also any number of
   * nested proxies.
   * <p>If the supplied {@code candidate} is a Spring
   * {@linkplain AopUtils#isAopProxy proxy}, the ultimate target of all
   * nested proxies will be returned; otherwise, the {@code candidate}
   * will be returned <em>as is</em>.
   *
   * @param candidate the instance to check (potentially a Spring AOP proxy;
   * never {@code null})
   * @return the target object or the {@code candidate} (never {@code null})
   * @throws IllegalStateException if an error occurs while unwrapping a proxy
   * @see Advised#getTargetSource()
   * @see cn.taketoday.aop.proxy.AopProxyUtils#ultimateTargetClass
   */
  @SuppressWarnings("unchecked")
  public static <T> T getUltimateTargetObject(Object candidate) {
    Assert.notNull(candidate, "Candidate must not be null");
    try {
      if (AopUtils.isAopProxy(candidate) && candidate instanceof Advised) {
        Advised advised = (Advised) candidate;
        Object target = advised.getTargetSource().getTarget();
        if (target != null) {
          return getUltimateTargetObject(target);
        }
      }
    }
    catch (Throwable ex) {
      throw new IllegalStateException("Failed to unwrap proxied object", ex);
    }
    return (T) candidate;
  }

}
