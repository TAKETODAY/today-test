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

package cn.taketoday.test.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.taketoday.core.annotation.AliasFor;

/**
 * {@code TestExecutionListeners} defines class-level metadata for configuring
 * which {@link TestExecutionListener TestExecutionListeners} should be
 * registered with a {@link TestContextManager}.
 *
 * <p>Typically, {@code @TestExecutionListeners} will be used in conjunction
 * with {@link ContextConfiguration @ContextConfiguration}.
 *
 * <p>This annotation may be used as a <em>meta-annotation</em> to create custom
 * <em>composed annotations</em>.
 *
 * <p></p>this annotation will be inherited from an
 * enclosing test class by default. See
 * {@link NestedTestConfiguration @NestedTestConfiguration} for details.
 *
 * @author Sam Brannen
 * @see TestExecutionListener
 * @see TestContextManager
 * @see ContextConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TestExecutionListeners {

  /**
   * Alias for {@link #listeners}.
   * <p>This attribute may <strong>not</strong> be used in conjunction with
   * {@link #listeners}, but it may be used instead of {@link #listeners}.
   */
  @AliasFor("listeners")
  Class<? extends TestExecutionListener>[] value() default {};

  /**
   * The {@link TestExecutionListener TestExecutionListeners} to register with
   * the {@link TestContextManager}.
   * <p>This attribute may <strong>not</strong> be used in conjunction with
   * {@link #value}, but it may be used instead of {@link #value}.
   *
   * @see cn.taketoday.test.context.web.ServletTestExecutionListener
   * @see cn.taketoday.test.context.support.DirtiesContextBeforeModesTestExecutionListener
   * @see cn.taketoday.test.context.event.ApplicationEventsTestExecutionListener
   * @see cn.taketoday.test.context.support.DependencyInjectionTestExecutionListener
   * @see cn.taketoday.test.context.support.DirtiesContextTestExecutionListener
   * @see cn.taketoday.test.context.transaction.TransactionalTestExecutionListener
   * @see cn.taketoday.test.context.jdbc.SqlScriptsTestExecutionListener
   * @see cn.taketoday.test.context.event.EventPublishingTestExecutionListener
   */
  @AliasFor("value")
  Class<? extends TestExecutionListener>[] listeners() default {};

  /**
   * Whether or not {@link #listeners TestExecutionListeners} from superclasses
   * should be <em>inherited</em>.
   * <p>The default value is {@code true}, which means that an annotated
   * class will <em>inherit</em> the listeners defined by an annotated
   * superclass. Specifically, the listeners for an annotated class will be
   * appended to the list of listeners defined by an annotated superclass.
   * Thus, subclasses have the option of <em>extending</em> the list of
   * listeners. In the following example, {@code AbstractBaseTest} will
   * be configured with {@code DependencyInjectionTestExecutionListener}
   * and {@code DirtiesContextTestExecutionListener}; whereas,
   * {@code TransactionalTest} will be configured with
   * {@code DependencyInjectionTestExecutionListener},
   * {@code DirtiesContextTestExecutionListener}, <strong>and</strong>
   * {@code TransactionalTestExecutionListener}, in that order.
   * <pre class="code">
   * &#064;TestExecutionListeners({
   *     DependencyInjectionTestExecutionListener.class,
   *     DirtiesContextTestExecutionListener.class
   * })
   * public abstract class AbstractBaseTest {
   * 	 // ...
   * }
   *
   * &#064;TestExecutionListeners(TransactionalTestExecutionListener.class)
   * public class TransactionalTest extends AbstractBaseTest {
   * 	 // ...
   * }</pre>
   * <p>If {@code inheritListeners} is set to {@code false}, the listeners for
   * the annotated class will <em>shadow</em> and effectively replace any
   * listeners defined by a superclass.
   */
  boolean inheritListeners() default true;

  /**
   * The <em>merge mode</em> to use when {@code @TestExecutionListeners} is
   * declared on a class that does <strong>not</strong> inherit listeners
   * from a superclass.
   * <p>Can be set to {@link MergeMode#MERGE_WITH_DEFAULTS MERGE_WITH_DEFAULTS}
   * to have locally declared listeners <em>merged</em> with the default
   * listeners.
   * <p>The mode is ignored if listeners are inherited from a superclass.
   * <p>Defaults to {@link MergeMode#REPLACE_DEFAULTS REPLACE_DEFAULTS}
   * for backwards compatibility.
   *
   * @see MergeMode
   */
  MergeMode mergeMode() default MergeMode.REPLACE_DEFAULTS;

  /**
   * Enumeration of <em>modes</em> that dictate whether or not explicitly
   * declared listeners are merged with the default listeners when
   * {@code @TestExecutionListeners} is declared on a class that does
   * <strong>not</strong> inherit listeners from a superclass.
   */
  enum MergeMode {

    /**
     * Indicates that locally declared listeners should replace the default
     * listeners.
     */
    REPLACE_DEFAULTS,

    /**
     * Indicates that locally declared listeners should be merged with the
     * default listeners.
     * <p>The merging algorithm ensures that duplicates are removed from
     * the list and that the resulting set of merged listeners is sorted
     * according to the semantics of
     * {@link cn.taketoday.core.annotation.AnnotationAwareOrderComparator
     * AnnotationAwareOrderComparator}. If a listener implements
     * {@link cn.taketoday.core.Ordered Ordered} or is annotated
     * with {@link cn.taketoday.core.Order @Order} it can
     * influence the position in which it is merged with the defaults; otherwise,
     * locally declared listeners will simply be appended to the list of default
     * listeners when merged.
     */
    MERGE_WITH_DEFAULTS
  }

}
