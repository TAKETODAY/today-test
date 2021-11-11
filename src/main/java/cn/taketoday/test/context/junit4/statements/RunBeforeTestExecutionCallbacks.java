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

package cn.taketoday.test.context.junit4.statements;

import cn.taketoday.test.context.TestContextManager;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;

/**
 * {@code RunBeforeTestExecutionCallbacks} is a custom JUnit {@link Statement}
 * which allows the <em>Spring TestContext Framework</em> to be plugged into the
 * JUnit 4 execution chain by calling {@link TestContextManager#beforeTestExecution
 * beforeTestExecution()} on the supplied {@link TestContextManager}.
 *
 * @author Sam Brannen
 * @see #evaluate()
 * @see RunAfterTestExecutionCallbacks
 */
public class RunBeforeTestExecutionCallbacks extends Statement {

	private final Statement next;

	private final Object testInstance;

	private final Method testMethod;

	private final TestContextManager testContextManager;


	/**
	 * Construct a new {@code RunBeforeTestExecutionCallbacks} statement.
	 *
	 * @param next the next {@code Statement} in the execution chain
	 * @param testInstance the current test instance (never {@code null})
	 * @param testMethod the test method which is about to be executed on the
	 * test instance
	 * @param testContextManager the TestContextManager upon which to call
	 * {@code beforeTestExecution()}
	 */
	public RunBeforeTestExecutionCallbacks(Statement next, Object testInstance, Method testMethod,
																				 TestContextManager testContextManager) {

		this.next = next;
		this.testInstance = testInstance;
		this.testMethod = testMethod;
		this.testContextManager = testContextManager;
	}

	/**
	 * Invoke {@link TestContextManager#beforeTestExecution(Object, Method)}
	 * and then evaluate the next {@link Statement} in the execution chain
	 * (typically an instance of
	 * {@link org.junit.internal.runners.statements.InvokeMethod InvokeMethod}).
	 */
	@Override
	public void evaluate() throws Throwable {
		this.testContextManager.beforeTestExecution(this.testInstance, this.testMethod);
		this.next.evaluate();
	}

}
