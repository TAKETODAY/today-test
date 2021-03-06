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

package cn.taketoday.test.context.junit4.rules;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;

import cn.taketoday.logging.Logger;
import cn.taketoday.logging.LoggerFactory;
import cn.taketoday.test.context.TestContextManager;
import cn.taketoday.test.context.junit4.TodayJUnit4ClassRunner;
import cn.taketoday.test.context.junit4.statements.ProfileValueChecker;
import cn.taketoday.test.context.junit4.statements.RunAfterTestMethodCallbacks;
import cn.taketoday.test.context.junit4.statements.RunBeforeTestMethodCallbacks;
import cn.taketoday.test.context.junit4.statements.RunPrepareTestInstanceCallbacks;
import cn.taketoday.test.context.junit4.statements.TodayFailOnTimeout;
import cn.taketoday.test.context.junit4.statements.TodayRepeat;

/**
 * {@code TodayMethodRule} is a custom JUnit 4 {@link MethodRule} that
 * supports instance-level and method-level features of the
 * <em>TestContext Framework</em> in standard JUnit tests by means
 * of the {@link TestContextManager} and associated support classes and
 * annotations.
 *
 * <p>In contrast to the {@link TodayJUnit4ClassRunner
 * TodayJUnit4ClassRunner}, Today's rule-based JUnit support has the advantage
 * that it is independent of any {@link org.junit.runner.Runner Runner} and
 * can therefore be combined with existing alternative runners like JUnit's
 * {@code Parameterized} or third-party runners such as the {@code MockitoJUnitRunner}.
 *
 * <p>In order to achieve the same functionality as the {@code TodayJUnit4ClassRunner},
 * however, a {@code TodayMethodRule} must be combined with a {@link TodayClassRule},
 * since {@code TodayMethodRule} only supports the instance-level and method-level
 * features of the {@code TodayJUnit4ClassRunner}.
 *
 * <h3>Example Usage</h3>
 * <pre><code> public class ExampleTodayIntegrationTest {
 *
 *    &#064;ClassRule
 *    public static final TodayClassRule TodayClassRule = new TodayClassRule();
 *
 *    &#064;Rule
 *    public final TodayMethodRule TodayMethodRule = new TodayMethodRule();
 *
 *    // ...
 * }</code></pre>
 *
 * <p>The following list constitutes all annotations currently supported directly
 * or indirectly by {@code TodayMethodRule}. <em>(Note that additional annotations
 * may be supported by various
 * {@link cn.taketoday.test.context.TestExecutionListener TestExecutionListener} or
 * {@link cn.taketoday.test.context.TestContextBootstrapper TestContextBootstrapper}
 * implementations.)</em>
 *
 * <ul>
 * <li>{@link cn.taketoday.test.annotation.Timed @Timed}</li>
 * <li>{@link cn.taketoday.test.annotation.Repeat @Repeat}</li>
 * <li>{@link cn.taketoday.test.annotation.ProfileValueSourceConfiguration @ProfileValueSourceConfiguration}</li>
 * <li>{@link cn.taketoday.test.annotation.IfProfileValue @IfProfileValue}</li>
 * </ul>
 *
 * <p><strong>NOTE:</strong> As of Today Framework 4.3, this class requires JUnit 4.12 or higher.
 *
 * <p><strong>WARNING:</strong> Due to the shortcomings of JUnit rules, the
 * {@code TodayMethodRule}
 * {@linkplain cn.taketoday.test.context.TestExecutionListener#prepareTestInstance
 * prepares the test instance} before {@code @Before} lifecycle methods instead of
 * immediately after instantiation of the test class. In addition, the
 * {@code TodayMethodRule} does <strong>not</strong> support the
 * {@code beforeTestExecution()} and {@code afterTestExecution()} callbacks of the
 * {@link cn.taketoday.test.context.TestExecutionListener TestExecutionListener}
 * API.
 *
 * @author Sam Brannen
 * @author Philippe Marschall
 * @see #apply(Statement, FrameworkMethod, Object)
 * @see TodayClassRule
 * @see cn.taketoday.test.context.TestContextManager
 * @see TodayJUnit4ClassRunner
 */
public class TodayMethodRule implements MethodRule {

  private static final Logger logger = LoggerFactory.getLogger(TodayMethodRule.class);

  /**
   * Apply <em>instance-level</em> and <em>method-level</em> features of
   * the <em>TestContext Framework</em> to the supplied {@code base}
   * statement.
   * <p>Specifically, this method invokes the
   * {@link TestContextManager#prepareTestInstance prepareTestInstance()},
   * {@link TestContextManager#beforeTestMethod beforeTestMethod()}, and
   * {@link TestContextManager#afterTestMethod afterTestMethod()} methods
   * on the {@code TestContextManager}, potentially with Today timeouts
   * and repetitions.
   * <p>In addition, this method checks whether the test is enabled in
   * the current execution environment. This prevents methods with a
   * non-matching {@code @IfProfileValue} annotation from running altogether,
   * even skipping the execution of {@code prepareTestInstance()} methods
   * in {@code TestExecutionListeners}.
   *
   * @param base the base {@code Statement} that this rule should be applied to
   * @param frameworkMethod the method which is about to be invoked on the test instance
   * @param testInstance the current test instance
   * @return a statement that wraps the supplied {@code base} with instance-level
   * and method-level features of the TestContext Framework
   * @see #withBeforeTestMethodCallbacks
   * @see #withAfterTestMethodCallbacks
   * @see #withPotentialRepeat
   * @see #withPotentialTimeout
   * @see #withTestInstancePreparation
   * @see #withProfileValueCheck
   */
  @Override
  public Statement apply(Statement base, FrameworkMethod frameworkMethod, Object testInstance) {
    Method testMethod = frameworkMethod.getMethod();
    if (logger.isDebugEnabled()) {
      logger.debug("Applying TodayMethodRule to test method [" + testMethod + "]");
    }
    Class<?> testClass = testInstance.getClass();
    TestContextManager testContextManager = TodayClassRule.getTestContextManager(testClass);

    Statement statement = base;
    statement = withBeforeTestMethodCallbacks(statement, testMethod, testInstance, testContextManager);
    statement = withAfterTestMethodCallbacks(statement, testMethod, testInstance, testContextManager);
    statement = withTestInstancePreparation(statement, testInstance, testContextManager);
    statement = withPotentialRepeat(statement, testMethod, testInstance);
    statement = withPotentialTimeout(statement, testMethod, testInstance);
    statement = withProfileValueCheck(statement, testMethod, testInstance);
    return statement;
  }

  /**
   * Wrap the supplied {@link Statement} with a {@code RunBeforeTestMethodCallbacks} statement.
   *
   * @see RunBeforeTestMethodCallbacks
   */
  private Statement withBeforeTestMethodCallbacks(Statement next, Method testMethod,
                                                  Object testInstance, TestContextManager testContextManager) {

    return new RunBeforeTestMethodCallbacks(
            next, testInstance, testMethod, testContextManager);
  }

  /**
   * Wrap the supplied {@link Statement} with a {@code RunAfterTestMethodCallbacks} statement.
   *
   * @see RunAfterTestMethodCallbacks
   */
  private Statement withAfterTestMethodCallbacks(Statement next, Method testMethod,
                                                 Object testInstance, TestContextManager testContextManager) {

    return new RunAfterTestMethodCallbacks(
            next, testInstance, testMethod, testContextManager);
  }

  /**
   * Wrap the supplied {@link Statement} with a {@code RunPrepareTestInstanceCallbacks} statement.
   *
   * @see RunPrepareTestInstanceCallbacks
   */
  private Statement withTestInstancePreparation(
          Statement next, Object testInstance, TestContextManager testContextManager) {

    return new RunPrepareTestInstanceCallbacks(next, testInstance, testContextManager);
  }

  /**
   * Wrap the supplied {@link Statement} with a {@code TodayRepeat} statement.
   * <p>Supports Today's {@link cn.taketoday.test.annotation.Repeat @Repeat}
   * annotation.
   *
   * @see TodayRepeat
   */
  private Statement withPotentialRepeat(Statement next, Method testMethod, Object testInstance) {
    return new TodayRepeat(next, testMethod);
  }

  /**
   * Wrap the supplied {@link Statement} with a {@code TodayFailOnTimeout} statement.
   * <p>Supports Today's {@link cn.taketoday.test.annotation.Timed @Timed}
   * annotation.
   *
   * @see TodayFailOnTimeout
   */
  private Statement withPotentialTimeout(Statement next, Method testMethod, Object testInstance) {
    return new TodayFailOnTimeout(next, testMethod);
  }

  /**
   * Wrap the supplied {@link Statement} with a {@code ProfileValueChecker} statement.
   *
   * @see ProfileValueChecker
   */
  private Statement withProfileValueCheck(Statement next, Method testMethod, Object testInstance) {
    return new ProfileValueChecker(next, testInstance.getClass(), testMethod);
  }

}
