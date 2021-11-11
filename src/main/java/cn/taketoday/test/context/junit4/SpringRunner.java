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

package cn.taketoday.test.context.junit4;

import cn.taketoday.test.context.junit4.rules.TodayClassRule;
import cn.taketoday.test.context.junit4.rules.TodayMethodRule;
import org.junit.runners.model.InitializationError;

/**
 * {@code SpringRunner} is an <em>alias</em> for the {@link SpringJUnit4ClassRunner}.
 *
 * <p>To use this class, simply annotate a JUnit 4 based test class with
 * {@code @RunWith(SpringRunner.class)}.
 *
 * <p>If you would like to use the Spring TestContext Framework with a runner other than
 * this one, use {@link TodayClassRule}
 * and {@link TodayMethodRule}.
 *
 * <p><strong>NOTE:</strong> This class requires JUnit 4.12 or higher.
 *
 * @author Sam Brannen
 * @see SpringJUnit4ClassRunner
 * @see TodayClassRule
 * @see TodayMethodRule
 */
public final class SpringRunner extends SpringJUnit4ClassRunner {

	/**
	 * Construct a new {@code SpringRunner} and initialize a
	 * {@link cn.taketoday.test.context.TestContextManager TestContextManager}
	 * to provide Spring testing functionality to standard JUnit 4 tests.
	 *
	 * @param clazz the test class to be run
	 * @see #createTestContextManager(Class)
	 */
	public SpringRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

}
