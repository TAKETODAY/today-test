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

package cn.taketoday.test.context;

import cn.taketoday.lang.Nullable;
import cn.taketoday.logging.Logger;
import cn.taketoday.logging.LoggerFactory;
import cn.taketoday.test.context.junit.jupiter.TodayExtension;
import cn.taketoday.test.context.junit.jupiter.TodayJUnitConfig;
import cn.taketoday.test.context.junit.jupiter.web.TodayJUnitWebConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @TestConstructor} is a type-level annotation that is used to configure
 * how the parameters of a test class constructor are autowired from components
 * in the test's {@link cn.taketoday.context.ApplicationContext
 * ApplicationContext}.
 *
 * <p>If {@code @TestConstructor} is not <em>present</em> or <em>meta-present</em>
 * on a test class, the default <em>test constructor autowire mode</em> will be
 * used. See {@link #TEST_CONSTRUCTOR_AUTOWIRE_MODE_PROPERTY_NAME} for details on
 * how to change the default mode. Note, however, that a local declaration of
 * {@link cn.taketoday.lang.Autowired @Autowired} on
 * a constructor takes precedence over both {@code @TestConstructor} and the default
 * mode.
 *
 * <p>This annotation may be used as a <em>meta-annotation</em> to create custom
 * <em>composed annotations</em>.
 *
 * <p>As of Spring Framework 5.2, this annotation is only supported in conjunction
 * with the {@link TodayExtension
 * SpringExtension} for use with JUnit Jupiter. Note that the {@code SpringExtension} is
 * often automatically registered for you &mdash; for example, when using annotations such as
 * {@link TodayJUnitConfig @SpringJUnitConfig} and
 * {@link TodayJUnitWebConfig @SpringJUnitWebConfig}
 * or various test-related annotations from Spring Boot Test.
 *
 * <p>As of Spring Framework 5.3, this annotation will be inherited from an
 * enclosing test class by default. See
 * {@link NestedTestConfiguration @NestedTestConfiguration} for details.
 *
 * @author Sam Brannen
 * @see cn.taketoday.lang.Autowired @Autowired
 * @see TodayExtension SpringExtension
 * @see TodayJUnitConfig @SpringJUnitConfig
 * @see TodayJUnitWebConfig @SpringJUnitWebConfig
 * @see ContextConfiguration @ContextConfiguration
 * @see ContextHierarchy @ContextHierarchy
 * @see ActiveProfiles @ActiveProfiles
 * @see TestPropertySource @TestPropertySource
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TestConstructor {

  /**
   * JVM system property used to change the default <em>test constructor
   * autowire mode</em>: {@value #TEST_CONSTRUCTOR_AUTOWIRE_MODE_PROPERTY_NAME}.
   * <p>Acceptable values include enum constants defined in {@link AutowireMode},
   * ignoring case. For example, the default may be changed to {@link AutowireMode#ALL}
   * by supplying the following JVM system property via the command line.
   * <pre style="code">-Dspring.test.constructor.autowire.mode=all</pre>
   * <p>If the property is not set to {@code ALL}, parameters for test class
   * constructors will be autowired according to {@link AutowireMode#ANNOTATED}
   * semantics by default.
   * <p>May alternatively be configured via the
   * {@link cn.taketoday.lang.TodayStrategies TodayStrategies}
   * mechanism.
   * <p>As of Spring Framework 5.3, this property may also be configured as a
   * <a href="https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params">JUnit
   * Platform configuration parameter</a>.
   *
   * @see #autowireMode
   */
  String TEST_CONSTRUCTOR_AUTOWIRE_MODE_PROPERTY_NAME = "spring.test.constructor.autowire.mode";


  /**
   * Flag for setting the <em>test constructor {@linkplain AutowireMode autowire
   * mode}</em> for the current test class.
   * <p>Setting this flag overrides the global default. See
   * {@link #TEST_CONSTRUCTOR_AUTOWIRE_MODE_PROPERTY_NAME} for details on how
   * to change the global default.
   *
   * @return an {@link AutowireMode} to take precedence over the global default
   * @see #TEST_CONSTRUCTOR_AUTOWIRE_MODE_PROPERTY_NAME
   * @see cn.taketoday.lang.Autowired @Autowired
   * @see AutowireMode#ALL
   * @see AutowireMode#ANNOTATED
   */
  AutowireMode autowireMode();


  /**
   * Defines autowiring modes for parameters in a test constructor.
   *
   * @see #ALL
   * @see #ANNOTATED
   */
  enum AutowireMode {

    /**
     * All test constructor parameters will be autowired as if the constructor
     * itself were annotated with
     * {@link cn.taketoday.lang.Autowired @Autowired}.
     *
     * @see #ANNOTATED
     */
    ALL,

    /**
     * Each individual test constructor parameter will only be autowired if it
     * is annotated with
     * {@link cn.taketoday.lang.Autowired @Autowired},
     * {@link cn.taketoday.lang.Qualifier @Qualifier},
     * or {@link cn.taketoday.lang.Value @Value},
     * or if the constructor itself is annotated with {@code @Autowired}.
     *
     * @see #ALL
     */
    ANNOTATED;


    /**
     * Get the {@code AutowireMode} enum constant with the supplied name,
     * ignoring case.
     *
     * @param name the name of the enum constant to retrieve
     * @return the corresponding enum constant or {@code null} if not found
     * @see AutowireMode#valueOf(String)
     */
    @Nullable
    public static AutowireMode from(@Nullable String name) {
      if (name == null) {
        return null;
      }
      try {
        return AutowireMode.valueOf(name.trim().toUpperCase());
      }
      catch (IllegalArgumentException ex) {
        Logger logger = LoggerFactory.getLogger(AutowireMode.class);
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Failed to parse autowire mode from '%s': %s", name, ex.getMessage()));
        }
        return null;
      }
    }
  }

}
