/*
 * Copyright 2002-2020 the original author or authors.
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

package cn.taketoday.test.context.support;

import cn.taketoday.lang.Assert;
import cn.taketoday.logging.Logger;
import cn.taketoday.logging.LoggerFactory;
import cn.taketoday.test.context.ActiveProfiles;
import cn.taketoday.test.context.ActiveProfilesResolver;
import cn.taketoday.test.context.TestContextAnnotationUtils.AnnotationDescriptor;

import static cn.taketoday.test.context.TestContextAnnotationUtils.findAnnotationDescriptor;

/**
 * Default implementation of the {@link ActiveProfilesResolver} strategy that
 * resolves <em>active bean definition profiles</em> based solely on profiles
 * configured declaratively via {@link ActiveProfiles#profiles} or
 * {@link ActiveProfiles#value}.
 *
 * @author Sam Brannen
 * @see ActiveProfiles
 * @see ActiveProfilesResolver
 */
public class DefaultActiveProfilesResolver implements ActiveProfilesResolver {

  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  private static final Logger logger = LoggerFactory.getLogger(DefaultActiveProfilesResolver.class);


  /**
   * Resolve the <em>bean definition profiles</em> for the given {@linkplain
   * Class test class} based on profiles configured declaratively via
   * {@link ActiveProfiles#profiles} or {@link ActiveProfiles#value}.
   *
   * @param testClass the test class for which the profiles should be resolved;
   * never {@code null}
   * @return the list of bean definition profiles to use when loading the
   * {@code ApplicationContext}; never {@code null}
   */
  @Override
  public String[] resolve(Class<?> testClass) {
    Assert.notNull(testClass, "Class must not be null");
    AnnotationDescriptor<ActiveProfiles> descriptor = findAnnotationDescriptor(testClass, ActiveProfiles.class);

    if (descriptor == null) {
      if (logger.isDebugEnabled()) {
        logger.debug(String.format(
                "Could not find an 'annotation declaring class' for annotation type [%s] and class [%s]",
                ActiveProfiles.class.getName(), testClass.getName()));
      }
      return EMPTY_STRING_ARRAY;
    }
    else {
      ActiveProfiles annotation = descriptor.getAnnotation();
      if (logger.isTraceEnabled()) {
        logger.trace(String.format("Retrieved @ActiveProfiles [%s] for declaring class [%s].", annotation,
                descriptor.getDeclaringClass().getName()));
      }
      return annotation.profiles();
    }
  }

}
