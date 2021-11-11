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

package cn.taketoday.mock.env;

import cn.taketoday.core.env.AbstractEnvironment;
import cn.taketoday.core.env.ConfigurableEnvironment;

/**
 * Simple {@link ConfigurableEnvironment} implementation exposing
 * {@link #setProperty(String, String)} and {@link #withProperty(String, String)}
 * methods for testing purposes.
 *
 * @author Chris Beams
 * @author Sam Brannen
 * @see cn.taketoday.mock.env.MockPropertySource
 */
public class MockEnvironment extends AbstractEnvironment {

	private final MockPropertySource propertySource = new MockPropertySource();

	/**
	 * Create a new {@code MockEnvironment} with a single {@link MockPropertySource}.
	 */
	public MockEnvironment() {
		getPropertySources().addLast(this.propertySource);
	}

	/**
	 * Set a property on the underlying {@link MockPropertySource} for this environment.
	 */
	public void setProperty(String key, String value) {
		this.propertySource.setProperty(key, value);
	}

	/**
	 * Convenient synonym for {@link #setProperty} that returns the current instance.
	 * Useful for method chaining and fluent-style use.
	 *
	 * @return this {@link MockEnvironment} instance
	 * @see MockPropertySource#withProperty
	 */
	public MockEnvironment withProperty(String key, String value) {
		this.setProperty(key, value);
		return this;
	}

}
