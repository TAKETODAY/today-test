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

package cn.taketoday.test.context.junit.jupiter;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.taketoday.core.annotation.AliasFor;

/**
 * {@code @EnabledIf} is used to signal that the annotated test class or test
 * method is <em>enabled</em> and should be executed if the supplied
 * {@link #expression} evaluates to {@code true}.
 *
 * <p>When applied at the class level, all test methods within that class
 * are automatically enabled by default as well.
 *
 * <p>For basic examples, see the Javadoc for {@link #expression}.
 *
 * <p>This annotation may be used as a <em>meta-annotation</em> to create
 * custom <em>composed annotations</em>. For example, a custom
 * {@code @EnabledOnMac} annotation can be created as follows.
 *
 * <pre style="code">
 * {@literal @}Target({ElementType.TYPE, ElementType.METHOD})
 * {@literal @}Retention(RetentionPolicy.RUNTIME)
 * {@literal @}EnabledIf(
 *     expression = "#{systemProperties['os.name'].toLowerCase().contains('mac')}",
 *     reason = "Enabled on Mac OS"
 * )
 * public {@literal @}interface EnabledOnMac {}
 * </pre>
 *
 * @author Sam Brannen
 * @see TodayExtension
 * @see DisabledIf
 * @see org.junit.jupiter.api.Disabled
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(EnabledIfCondition.class)
public @interface EnabledIf {

  /**
   * Alias for {@link #expression}; only intended to be used if {@link #reason}
   * and {@link #loadContext} are not specified.
   *
   * @see #expression
   */
  @AliasFor("expression")
  String value() default "";

  /**
   * The expression that will be evaluated to determine if the annotated test
   * class or test method is <em>enabled</em>.
   *
   * <p>If the expression evaluates to {@link Boolean#TRUE} or a {@link String}
   * equal to {@code "true"} (ignoring case), the test will be enabled.
   *
   * <p>Expressions can be any of the following.
   *
   * <ul>
   * <li>Expression Language (EL) expression &mdash; for example:
   * <pre style="code">@EnabledIf("#{systemProperties['os.name'].toLowerCase().contains('mac')}")</pre>
   * <li>Placeholder for a property available in the
   * {@link cn.taketoday.core.env.Environment Environment} &mdash; for example:
   * <pre style="code">@EnabledIf("${smoke.tests.enabled}")</pre>
   * <li>Text literal &mdash; for example:
   * <pre style="code">@EnabledIf("true")</pre>
   * </ul>
   *
   * <p>Note, however, that a <em>text literal</em> which is not the result of
   * dynamic resolution of a property placeholder is of zero practical value
   * since {@code @EnabledIf("false")} is equivalent to {@code @Disabled}
   * and {@code @EnabledIf("true")} is logically meaningless.
   *
   * @see #reason
   * @see #loadContext
   * @see #value
   */
  @AliasFor("value")
  String expression() default "";

  /**
   * The reason this test is enabled.
   *
   * @see #expression
   */
  String reason() default "";

  /**
   * Whether the {@code ApplicationContext} associated with the current test
   * should be eagerly loaded in order to evaluate the {@link #expression}.
   *
   * <p>Defaults to {@code false} so that test application contexts are not
   * eagerly loaded unnecessarily. If an expression is based solely on system
   * properties or environment variables or does not interact with beans in
   * the test's application context, there is no need to load the context
   * prematurely since doing so would be a waste of time if the test ends up
   * being disabled.
   *
   * @see #expression
   */
  boolean loadContext() default false;

}
