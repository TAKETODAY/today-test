/*
 * Copyright 2002-2018 the original author or authors.
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

package cn.taketoday.test.web.servlet;

/**
 * Builds a {@link MockMvc} instance.
 *
 * <p>See static factory methods in
 * {@link cn.taketoday.test.web.servlet.setup.MockMvcBuilders MockMvcBuilders}.
 *
 * @author Rossen Stoyanchev
 */
public interface MockMvcBuilder {

	/**
	 * Build a {@link MockMvc} instance.
	 */
	MockMvc build();

}