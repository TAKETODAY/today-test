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

package cn.taketoday.test.web.servlet.setup;

import cn.taketoday.lang.Assert;
import cn.taketoday.lang.Nullable;
import cn.taketoday.mock.web.MockServletConfig;
import cn.taketoday.test.web.servlet.DispatcherServletCustomizer;
import cn.taketoday.test.web.servlet.MockMvc;
import cn.taketoday.test.web.servlet.MockMvcBuilder;
import cn.taketoday.test.web.servlet.MockMvcBuilderSupport;
import cn.taketoday.test.web.servlet.RequestBuilder;
import cn.taketoday.test.web.servlet.ResultHandler;
import cn.taketoday.test.web.servlet.ResultMatcher;
import cn.taketoday.test.web.servlet.request.ConfigurableSmartRequestBuilder;
import cn.taketoday.test.web.servlet.request.MockMvcRequestBuilders;
import cn.taketoday.test.web.servlet.request.RequestPostProcessor;
import cn.taketoday.web.context.WebApplicationContext;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of {@link MockMvcBuilder} with common methods for
 * configuring filters, default request properties, global expectations and
 * global result actions.
 *
 * <p>Subclasses can use different strategies to prepare the Spring
 * {@code WebApplicationContext} that will be passed to the
 * {@code DispatcherServlet}.
 *
 * @param <B> a self reference to the builder type
 * @author Rossen Stoyanchev
 * @author Stephane Nicoll
 * @author Sam Brannen
 */
public abstract class AbstractMockMvcBuilder<B extends AbstractMockMvcBuilder<B>>
				extends MockMvcBuilderSupport implements ConfigurableMockMvcBuilder<B> {

	private final List<Filter> filters = new ArrayList<>();

	@Nullable
	private RequestBuilder defaultRequestBuilder;

	@Nullable
	private Charset defaultResponseCharacterEncoding;

	private final List<ResultMatcher> globalResultMatchers = new ArrayList<>();

	private final List<ResultHandler> globalResultHandlers = new ArrayList<>();

	private final List<DispatcherServletCustomizer> dispatcherServletCustomizers = new ArrayList<>();

	private final List<MockMvcConfigurer> configurers = new ArrayList<>(4);


	@Override
	public final <T extends B> T addFilters(Filter... filters) {
		Assert.notNull(filters, "filters cannot be null");
		for (Filter f : filters) {
			Assert.notNull(f, "filters cannot contain null values");
			this.filters.add(f);
		}
		return self();
	}

	@Override
	public final <T extends B> T addFilter(Filter filter, String... urlPatterns) {
		Assert.notNull(filter, "filter cannot be null");
		Assert.notNull(urlPatterns, "urlPatterns cannot be null");
		if (urlPatterns.length > 0) {
			filter = new PatternMappingFilterProxy(filter, urlPatterns);
		}
		this.filters.add(filter);
		return self();
	}

	@Override
	public final <T extends B> T defaultRequest(RequestBuilder requestBuilder) {
		this.defaultRequestBuilder = requestBuilder;
		return self();
	}

	/**
	 * Define the default character encoding to be applied to every response.
	 *
	 * @param defaultResponseCharacterEncoding the default response character encoding
	 */
	@Override
	public final <T extends B> T defaultResponseCharacterEncoding(Charset defaultResponseCharacterEncoding) {
		this.defaultResponseCharacterEncoding = defaultResponseCharacterEncoding;
		return self();
	}

	@Override
	public final <T extends B> T alwaysExpect(ResultMatcher resultMatcher) {
		this.globalResultMatchers.add(resultMatcher);
		return self();
	}

	@Override
	public final <T extends B> T alwaysDo(ResultHandler resultHandler) {
		this.globalResultHandlers.add(resultHandler);
		return self();
	}

	@Override
	public final <T extends B> T addDispatcherServletCustomizer(DispatcherServletCustomizer customizer) {
		this.dispatcherServletCustomizers.add(customizer);
		return self();
	}

	@Override
	public final <T extends B> T dispatchOptions(boolean dispatchOptions) {
		return addDispatcherServletCustomizer(
						dispatcherServlet -> dispatcherServlet.setDispatchOptionsRequest(dispatchOptions));
	}

	@Override
	public final <T extends B> T apply(MockMvcConfigurer configurer) {
		configurer.afterConfigurerAdded(this);
		this.configurers.add(configurer);
		return self();
	}

	@SuppressWarnings("unchecked")
	protected <T extends B> T self() {
		return (T) this;
	}


	/**
	 * Build a {@link cn.taketoday.test.web.servlet.MockMvc} instance.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public final MockMvc build() {
		WebApplicationContext wac = initWebAppContext();
		ServletContext servletContext = wac.getServletContext();
		MockServletConfig mockServletConfig = new MockServletConfig(servletContext);

		for (MockMvcConfigurer configurer : this.configurers) {
			RequestPostProcessor processor = configurer.beforeMockMvcCreated(this, wac);
			if (processor != null) {
				if (this.defaultRequestBuilder == null) {
					this.defaultRequestBuilder = MockMvcRequestBuilders.get("/");
				}
				if (this.defaultRequestBuilder instanceof ConfigurableSmartRequestBuilder) {
					((ConfigurableSmartRequestBuilder) this.defaultRequestBuilder).with(processor);
				}
			}
		}

		Filter[] filterArray = this.filters.toArray(new Filter[0]);

		return super.createMockMvc(filterArray, mockServletConfig, wac, this.defaultRequestBuilder,
						this.defaultResponseCharacterEncoding, this.globalResultMatchers, this.globalResultHandlers,
						this.dispatcherServletCustomizers);
	}

	/**
	 * A method to obtain the {@code WebApplicationContext} to be passed to the
	 * {@code DispatcherServlet}. Invoked from {@link #build()} before the
	 * {@link MockMvc} instance is created.
	 */
	protected abstract WebApplicationContext initWebAppContext();

}