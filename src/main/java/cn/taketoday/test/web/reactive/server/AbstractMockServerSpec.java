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

package cn.taketoday.test.web.reactive.server;

import cn.taketoday.lang.Nullable;
import cn.taketoday.util.CollectionUtils;
import cn.taketoday.web.server.WebFilter;
import cn.taketoday.web.server.adapter.WebHttpHandlerBuilder;
import cn.taketoday.web.server.session.DefaultWebSessionManager;
import cn.taketoday.web.server.session.WebSessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for implementations of {@link WebTestClient.MockServerSpec}.
 *
 * @param <B> a self reference to the builder type
 * @author Rossen Stoyanchev
 */
abstract class AbstractMockServerSpec<B extends WebTestClient.MockServerSpec<B>>
				implements WebTestClient.MockServerSpec<B> {

	@Nullable
	private List<WebFilter> filters;

	@Nullable
	private WebSessionManager sessionManager;

	@Nullable
	private List<MockServerConfigurer> configurers;


	AbstractMockServerSpec() {
		// Default instance to be re-used across requests, unless one is configured explicitly
		this.sessionManager = new DefaultWebSessionManager();
	}


	@Override
	public <T extends B> T webFilter(WebFilter... filters) {
		if (filters.length > 0) {
			this.filters = (this.filters != null ? this.filters : new ArrayList<>(4));
			this.filters.addAll(Arrays.asList(filters));
		}
		return self();
	}

	@Override
	public <T extends B> T webSessionManager(WebSessionManager sessionManager) {
		this.sessionManager = sessionManager;
		return self();
	}

	@Override
	public <T extends B> T apply(MockServerConfigurer configurer) {
		configurer.afterConfigureAdded(this);
		this.configurers = (this.configurers != null ? this.configurers : new ArrayList<>(4));
		this.configurers.add(configurer);
		return self();
	}

	@SuppressWarnings("unchecked")
	private <T extends B> T self() {
		return (T) this;
	}

	@Override
	public WebTestClient.Builder configureClient() {
		WebHttpHandlerBuilder builder = initHttpHandlerBuilder();
		if (!CollectionUtils.isEmpty(this.filters)) {
			builder.filters(theFilters -> theFilters.addAll(0, this.filters));
		}
		if (!builder.hasSessionManager() && this.sessionManager != null) {
			builder.sessionManager(this.sessionManager);
		}
		if (!CollectionUtils.isEmpty(this.configurers)) {
			this.configurers.forEach(configurer -> configurer.beforeServerCreated(builder));
		}
		return new DefaultWebTestClientBuilder(builder);
	}

	/**
	 * Sub-classes must create an {@code WebHttpHandlerBuilder} that will then
	 * be used to create the HttpHandler for the mock server.
	 */
	protected abstract WebHttpHandlerBuilder initHttpHandlerBuilder();

	@Override
	public WebTestClient build() {
		return configureClient().build();
	}

}