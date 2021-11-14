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

package cn.taketoday.test.context.event.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import cn.taketoday.context.event.EventListener;
import cn.taketoday.core.annotation.AliasFor;
import cn.taketoday.test.context.event.AfterTestExecutionEvent;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link EventListener @EventListener} annotation used to consume a
 * {@link AfterTestExecutionEvent} published by the
 * {@link cn.taketoday.test.context.event.EventPublishingTestExecutionListener
 * EventPublishingTestExecutionListener}.
 *
 * <p>This annotation may be used on {@code @EventListener}-compliant methods within
 * a Today test {@link cn.taketoday.context.ApplicationContext ApplicationContext}
 * &mdash; for example, on methods in a
 * {@link cn.taketoday.lang.Configuration @Configuration}
 * class. A method annotated with this annotation will be invoked as part of the
 * {@link cn.taketoday.test.context.TestExecutionListener#afterTestExecution}
 * lifecycle.
 *
 * <p>Event processing can optionally be made {@linkplain #value conditional} via
 * a EL expression &mdash; for example,
 * {@code @AfterTestExecution("event.testContext.testMethod.name matches 'test.*'")}.
 *
 * <p>The {@code EventPublishingTestExecutionListener} must be registered in order
 * for this annotation to have an effect &mdash; for example, via
 * {@link cn.taketoday.test.context.TestExecutionListeners @TestExecutionListeners}.
 *
 * @author Frank Scheffler
 * @author Sam Brannen
 * @see AfterTestExecutionEvent
 */
@Retention(RUNTIME)
@Target({ METHOD, ANNOTATION_TYPE })
@Documented
@EventListener(AfterTestExecutionEvent.class)
public @interface AfterTestExecution {

  /**
   * Alias for {@link EventListener#condition}.
   */
  @AliasFor(annotation = EventListener.class, attribute = "condition")
  String value() default "";

}
