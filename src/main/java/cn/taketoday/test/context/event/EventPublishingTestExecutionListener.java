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

package cn.taketoday.test.context.event;

import cn.taketoday.test.context.TestContext;
import cn.taketoday.test.context.TestExecutionListener;
import cn.taketoday.test.context.support.AbstractTestExecutionListener;

/**
 * {@link cn.taketoday.test.context.TestExecutionListener TestExecutionListener}
 * that publishes test execution events to the
 * {@link cn.taketoday.context.ApplicationContext ApplicationContext}
 * for the currently executing test. Events are only published if the
 * {@code ApplicationContext} {@linkplain TestContext#hasApplicationContext()
 * has already been loaded}.
 *
 * <h3>Supported Events</h3>
 * <ul>
 * <li>{@link BeforeTestClassEvent}</li>
 * <li>{@link PrepareTestInstanceEvent}</li>
 * <li>{@link BeforeTestMethodEvent}</li>
 * <li>{@link BeforeTestExecutionEvent}</li>
 * <li>{@link AfterTestExecutionEvent}</li>
 * <li>{@link AfterTestMethodEvent}</li>
 * <li>{@link AfterTestClassEvent}</li>
 * </ul>
 *
 * <p>These events may be consumed for various reasons, such as resetting <em>mock</em>
 * beans or tracing test execution. One advantage of consuming test events rather
 * than implementing a custom {@link TestExecutionListener} is that test events
 * may be consumed by any bean registered in the test {@code ApplicationContext},
 * and such beans may benefit directly from dependency injection and other features
 * of the {@code ApplicationContext}. In contrast, a {@link TestExecutionListener}
 * is not a bean in the {@code ApplicationContext}.
 *
 * <h3>Exception Handling</h3>
 * <p>By default, if a test event listener throws an exception while consuming
 * a test event, that exception will propagate to the underlying testing framework
 * in use. For example, if the consumption of a {@code BeforeTestMethodEvent}
 * results in an exception, the corresponding test method will fail as a result
 * of the exception. In contrast, if an asynchronous test event listener throws
 * an exception, the exception will not propagate to the underlying testing framework.
 * For further details on asynchronous exception handling, consult the class-level
 * Javadoc for {@link cn.taketoday.context.event.EventListener @EventListener}.
 *
 * <h3>Asynchronous Listeners</h3>
 * <p>If you want a particular test event listener to process events asynchronously,
 * you can use {@link cn.taketoday.scheduling.annotation.Async @Async}
 * support. For further details, consult the class-level Javadoc for
 * {@link cn.taketoday.context.event.EventListener @EventListener}.
 *
 * @author Sam Brannen
 * @author Frank Scheffler
 * @see cn.taketoday.test.context.event.annotation.BeforeTestClass @BeforeTestClass
 * @see cn.taketoday.test.context.event.annotation.PrepareTestInstance @PrepareTestInstance
 * @see cn.taketoday.test.context.event.annotation.BeforeTestMethod @BeforeTestMethod
 * @see cn.taketoday.test.context.event.annotation.BeforeTestExecution @BeforeTestExecution
 * @see cn.taketoday.test.context.event.annotation.AfterTestExecution @AfterTestExecution
 * @see cn.taketoday.test.context.event.annotation.AfterTestMethod @AfterTestMethod
 * @see cn.taketoday.test.context.event.annotation.AfterTestClass @AfterTestClass
 */
public class EventPublishingTestExecutionListener extends AbstractTestExecutionListener {

  /**
   * Returns {@code 10000}.
   */
  @Override
  public final int getOrder() {
    return 10_000;
  }

  /**
   * Publish a {@link BeforeTestClassEvent} to the {@code ApplicationContext}
   * for the supplied {@link TestContext}.
   */
  @Override
  public void beforeTestClass(TestContext testContext) {
    testContext.publishEvent(BeforeTestClassEvent::new);
  }

  /**
   * Publish a {@link PrepareTestInstanceEvent} to the {@code ApplicationContext}
   * for the supplied {@link TestContext}.
   */
  @Override
  public void prepareTestInstance(TestContext testContext) {
    testContext.publishEvent(PrepareTestInstanceEvent::new);
  }

  /**
   * Publish a {@link BeforeTestMethodEvent} to the {@code ApplicationContext}
   * for the supplied {@link TestContext}.
   */
  @Override
  public void beforeTestMethod(TestContext testContext) {
    testContext.publishEvent(BeforeTestMethodEvent::new);
  }

  /**
   * Publish a {@link BeforeTestExecutionEvent} to the {@code ApplicationContext}
   * for the supplied {@link TestContext}.
   */
  @Override
  public void beforeTestExecution(TestContext testContext) {
    testContext.publishEvent(BeforeTestExecutionEvent::new);
  }

  /**
   * Publish an {@link AfterTestExecutionEvent} to the {@code ApplicationContext}
   * for the supplied {@link TestContext}.
   */
  @Override
  public void afterTestExecution(TestContext testContext) {
    testContext.publishEvent(AfterTestExecutionEvent::new);
  }

  /**
   * Publish an {@link AfterTestMethodEvent} to the {@code ApplicationContext}
   * for the supplied {@link TestContext}.
   */
  @Override
  public void afterTestMethod(TestContext testContext) {
    testContext.publishEvent(AfterTestMethodEvent::new);
  }

  /**
   * Publish an {@link AfterTestClassEvent} to the {@code ApplicationContext}
   * for the supplied {@link TestContext}.
   */
  @Override
  public void afterTestClass(TestContext testContext) {
    testContext.publishEvent(AfterTestClassEvent::new);
  }

}
