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

package cn.taketoday.test.web.servlet.client;

import cn.taketoday.format.support.FormattingConversionService;
import cn.taketoday.http.converter.HttpMessageConverter;
import cn.taketoday.lang.Nullable;
import cn.taketoday.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import cn.taketoday.test.web.servlet.setup.MockMvcBuilders;
import cn.taketoday.test.web.servlet.setup.StandaloneMockMvcBuilder;
import cn.taketoday.validation.Validator;
import cn.taketoday.web.accept.ContentNegotiationManager;
import cn.taketoday.web.method.support.HandlerMethodArgumentResolver;
import cn.taketoday.web.method.support.HandlerMethodReturnValueHandler;
import cn.taketoday.web.servlet.FlashMapManager;
import cn.taketoday.web.servlet.HandlerExceptionResolver;
import cn.taketoday.web.servlet.HandlerInterceptor;
import cn.taketoday.web.servlet.LocaleResolver;
import cn.taketoday.web.servlet.View;
import cn.taketoday.web.servlet.ViewResolver;
import cn.taketoday.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import cn.taketoday.web.util.pattern.PathPatternParser;

import java.util.function.Supplier;

/**
 * Simple wrapper around a {@link StandaloneMockMvcBuilder} that implements
 * {@link MockMvcWebTestClient.ControllerSpec}.
 *
 * @author Rossen Stoyanchev
 */
class StandaloneMockMvcSpec extends AbstractMockMvcServerSpec<MockMvcWebTestClient.ControllerSpec>
				implements MockMvcWebTestClient.ControllerSpec {

	private final StandaloneMockMvcBuilder mockMvcBuilder;


	StandaloneMockMvcSpec(Object... controllers) {
		this.mockMvcBuilder = MockMvcBuilders.standaloneSetup(controllers);
	}

	@Override
	public StandaloneMockMvcSpec controllerAdvice(Object... controllerAdvice) {
		this.mockMvcBuilder.setControllerAdvice(controllerAdvice);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec messageConverters(HttpMessageConverter<?>... messageConverters) {
		this.mockMvcBuilder.setMessageConverters(messageConverters);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec validator(Validator validator) {
		this.mockMvcBuilder.setValidator(validator);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec conversionService(FormattingConversionService conversionService) {
		this.mockMvcBuilder.setConversionService(conversionService);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec interceptors(HandlerInterceptor... interceptors) {
		mappedInterceptors(null, interceptors);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec mappedInterceptors(
					@Nullable String[] pathPatterns, HandlerInterceptor... interceptors) {

		this.mockMvcBuilder.addMappedInterceptors(pathPatterns, interceptors);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec contentNegotiationManager(ContentNegotiationManager manager) {
		this.mockMvcBuilder.setContentNegotiationManager(manager);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec asyncRequestTimeout(long timeout) {
		this.mockMvcBuilder.setAsyncRequestTimeout(timeout);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec customArgumentResolvers(HandlerMethodArgumentResolver... argumentResolvers) {
		this.mockMvcBuilder.setCustomArgumentResolvers(argumentResolvers);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec customReturnValueHandlers(HandlerMethodReturnValueHandler... handlers) {
		this.mockMvcBuilder.setCustomReturnValueHandlers(handlers);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec handlerExceptionResolvers(HandlerExceptionResolver... exceptionResolvers) {
		this.mockMvcBuilder.setHandlerExceptionResolvers(exceptionResolvers);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec viewResolvers(ViewResolver... resolvers) {
		this.mockMvcBuilder.setViewResolvers(resolvers);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec singleView(View view) {
		this.mockMvcBuilder.setSingleView(view);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec localeResolver(LocaleResolver localeResolver) {
		this.mockMvcBuilder.setLocaleResolver(localeResolver);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec flashMapManager(FlashMapManager flashMapManager) {
		this.mockMvcBuilder.setFlashMapManager(flashMapManager);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec patternParser(PathPatternParser parser) {
		this.mockMvcBuilder.setPatternParser(parser);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec useTrailingSlashPatternMatch(boolean useTrailingSlashPatternMatch) {
		this.mockMvcBuilder.setUseTrailingSlashPatternMatch(useTrailingSlashPatternMatch);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec placeholderValue(String name, String value) {
		this.mockMvcBuilder.addPlaceholderValue(name, value);
		return this;
	}

	@Override
	public StandaloneMockMvcSpec customHandlerMapping(Supplier<RequestMappingHandlerMapping> factory) {
		this.mockMvcBuilder.setCustomHandlerMapping(factory);
		return this;
	}

	@Override
	public ConfigurableMockMvcBuilder<?> getMockMvcBuilder() {
		return this.mockMvcBuilder;
	}
}
