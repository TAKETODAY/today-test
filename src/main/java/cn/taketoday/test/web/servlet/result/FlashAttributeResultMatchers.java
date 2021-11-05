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

package cn.taketoday.test.web.servlet.result;

import cn.taketoday.lang.Nullable;
import cn.taketoday.test.web.servlet.ResultMatcher;
import org.hamcrest.Matcher;

import static cn.taketoday.test.util.AssertionErrors.assertEquals;
import static cn.taketoday.test.util.AssertionErrors.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Factory for "output" flash attribute assertions.
 *
 * <p>An instance of this class is typically accessed via
 * {@link MockMvcResultMatchers#flash}.
 *
 * @author Rossen Stoyanchev
 */
public class FlashAttributeResultMatchers {

	/**
	 * Protected constructor.
	 * Use {@link MockMvcResultMatchers#flash()}.
	 */
	protected FlashAttributeResultMatchers() {
	}


	/**
	 * Assert a flash attribute's value with the given Hamcrest {@link Matcher}.
	 */
	@SuppressWarnings("unchecked")
	public <T> ResultMatcher attribute(String name, Matcher<? super T> matcher) {
		return result -> assertThat("Flash attribute '" + name + "'", (T) result.getFlashMap().get(name), matcher);
	}

	/**
	 * Assert a flash attribute's value.
	 */
	public ResultMatcher attribute(String name, @Nullable Object value) {
		return result -> assertEquals("Flash attribute '" + name + "'", value, result.getFlashMap().get(name));
	}

	/**
	 * Assert the existence of the given flash attributes.
	 */
	public ResultMatcher attributeExists(String... names) {
		return result -> {
			for (String name : names) {
				assertNotNull("Flash attribute '" + name + "' does not exist", result.getFlashMap().get(name));
			}
		};
	}

	/**
	 * Assert the number of flash attributes.
	 */
	public ResultMatcher attributeCount(int count) {
		return result -> assertEquals("FlashMap size", count, result.getFlashMap().size());
	}

}