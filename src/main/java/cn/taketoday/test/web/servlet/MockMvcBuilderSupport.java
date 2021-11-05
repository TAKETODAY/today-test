/*
 * Copyright 2002-2021 the original author or authors.
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

import cn.taketoday.core.NestedRuntimeException;
import cn.taketoday.lang.Nullable;
import cn.taketoday.mock.web.MockServletConfig;
import cn.taketoday.web.context.WebApplicationContext;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Base class for MockMvc builder implementations, providing the capability to
 * create a {@link MockMvc} instance.
 *
 * <p>{@link cn.taketoday.test.web.servlet.setup.DefaultMockMvcBuilder},
 * which derives from this class, provides a concrete {@code build} method,
 * and delegates to abstract methods to obtain a {@link WebApplicationContext}.
 *
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @author Stephane Nicoll
 * @author Sam Brannen
 */
public abstract class MockMvcBuilderSupport {

	/**
	 * Delegates to {@link #createMockMvc(Filter[], MockServletConfig, WebApplicationContext, RequestBuilder, List, List, List)}
	 * for creation of the {@link MockMvc} instance and configures that instance
	 * with the supplied {@code defaultResponseCharacterEncoding}.
	 */
	protected final MockMvc createMockMvc(Filter[] filters, MockServletConfig servletConfig,
																				WebApplicationContext webAppContext, @Nullable RequestBuilder defaultRequestBuilder,
																				@Nullable Charset defaultResponseCharacterEncoding,
																				List<ResultMatcher> globalResultMatchers, List<ResultHandler> globalResultHandlers,
																				@Nullable List<DispatcherServletCustomizer> dispatcherServletCustomizers) {

		MockMvc mockMvc = createMockMvc(filters, servletConfig, webAppContext, defaultRequestBuilder, globalResultMatchers, globalResultHandlers, dispatcherServletCustomizers);
		mockMvc.setDefaultResponseCharacterEncoding(defaultResponseCharacterEncoding);
		return mockMvc;
	}

	protected final MockMvc createMockMvc(Filter[] filters, MockServletConfig servletConfig,
																				WebApplicationContext webAppContext, @Nullable RequestBuilder defaultRequestBuilder,
																				List<ResultMatcher> globalResultMatchers, List<ResultHandler> globalResultHandlers,
																				@Nullable List<DispatcherServletCustomizer> dispatcherServletCustomizers) {

		TestDispatcherServlet dispatcherServlet = new TestDispatcherServlet(webAppContext);
		if (dispatcherServletCustomizers != null) {
			for (DispatcherServletCustomizer customizers : dispatcherServletCustomizers) {
				customizers.customize(dispatcherServlet);
			}
		}
		try {
			dispatcherServlet.init(servletConfig);
		}
		catch (ServletException ex) {
			// should never happen..
			throw new MockMvcBuildException("Failed to initialize TestDispatcherServlet", ex);
		}

		MockMvc mockMvc = new MockMvc(dispatcherServlet, filters);
		mockMvc.setDefaultRequest(defaultRequestBuilder);
		mockMvc.setGlobalResultMatchers(globalResultMatchers);
		mockMvc.setGlobalResultHandlers(globalResultHandlers);

		return mockMvc;
	}


	@SuppressWarnings("serial")
	private static class MockMvcBuildException extends NestedRuntimeException {

		public MockMvcBuildException(String msg, Throwable cause) {
			super(msg, cause);
		}
	}

}
