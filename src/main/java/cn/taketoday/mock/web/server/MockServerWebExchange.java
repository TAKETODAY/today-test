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

package cn.taketoday.mock.web.server;

import cn.taketoday.http.codec.ServerCodecConfigurer;
import cn.taketoday.lang.Nullable;
import cn.taketoday.mock.http.server.reactive.MockServerHttpRequest;
import cn.taketoday.mock.http.server.reactive.MockServerHttpResponse;
import cn.taketoday.web.server.WebSession;
import cn.taketoday.web.server.adapter.DefaultServerWebExchange;
import cn.taketoday.web.server.i18n.AcceptHeaderLocaleContextResolver;
import cn.taketoday.web.server.session.DefaultWebSessionManager;
import cn.taketoday.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

/**
 * Extension of {@link DefaultServerWebExchange} for use in tests, along with
 * {@link MockServerHttpRequest} and {@link MockServerHttpResponse}.
 *
 * <p>See static factory methods to create an instance.
 *
 * @author Rossen Stoyanchev
 */
public final class MockServerWebExchange extends DefaultServerWebExchange {

	private MockServerWebExchange(MockServerHttpRequest request, WebSessionManager sessionManager) {
		super(request, new MockServerHttpResponse(), sessionManager,
						ServerCodecConfigurer.create(), new AcceptHeaderLocaleContextResolver());
	}


	@Override
	public MockServerHttpResponse getResponse() {
		return (MockServerHttpResponse) super.getResponse();
	}


	/**
	 * Create a {@link MockServerWebExchange} from the given mock request.
	 *
	 * @param request the request to use.
	 * @return the exchange
	 */
	public static MockServerWebExchange from(MockServerHttpRequest request) {
		return builder(request).build();
	}

	/**
	 * Variant of {@link #from(MockServerHttpRequest)} with a mock request builder.
	 *
	 * @param requestBuilder the builder for the mock request.
	 * @return the exchange
	 */
	public static MockServerWebExchange from(MockServerHttpRequest.BaseBuilder<?> requestBuilder) {
		return builder(requestBuilder).build();
	}

	/**
	 * Create a {@link Builder} starting with the given mock request.
	 *
	 * @param request the request to use.
	 * @return the exchange builder
	 */
	public static Builder builder(MockServerHttpRequest request) {
		return new Builder(request);
	}

	/**
	 * Variant of {@link #builder(MockServerHttpRequest)} with a mock request builder.
	 *
	 * @param requestBuilder the builder for the mock request.
	 * @return the exchange builder
	 */
	public static Builder builder(MockServerHttpRequest.BaseBuilder<?> requestBuilder) {
		return new Builder(requestBuilder.build());
	}


	/**
	 * Builder for a {@link MockServerWebExchange}.
	 */
	public static class Builder {

		private final MockServerHttpRequest request;

		@Nullable
		private WebSessionManager sessionManager;

		public Builder(MockServerHttpRequest request) {
			this.request = request;
		}

		/**
		 * Set the session to use for the exchange.
		 * <p>This method is mutually exclusive with
		 * {@link #sessionManager(WebSessionManager)}.
		 *
		 * @param session the session to use
		 * @see MockWebSession
		 */
		public Builder session(WebSession session) {
			this.sessionManager = exchange -> Mono.just(session);
			return this;
		}

		/**
		 * Provide a {@code WebSessionManager} instance to use with the exchange.
		 * <p>This is mutually exclusive with {@link #session(WebSession)}.
		 *
		 * @param sessionManager the session manager to use
		 */
		public Builder sessionManager(WebSessionManager sessionManager) {
			this.sessionManager = sessionManager;
			return this;
		}

		/**
		 * Build the {@code MockServerWebExchange} instance.
		 */
		public MockServerWebExchange build() {
			return new MockServerWebExchange(this.request,
							this.sessionManager != null ? this.sessionManager : new DefaultWebSessionManager());
		}
	}

}