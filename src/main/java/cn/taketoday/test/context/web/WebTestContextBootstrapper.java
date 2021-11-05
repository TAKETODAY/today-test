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

package cn.taketoday.test.context.web;

import cn.taketoday.lang.Nullable;
import cn.taketoday.test.context.ContextLoader;
import cn.taketoday.test.context.MergedContextConfiguration;
import cn.taketoday.test.context.TestContextAnnotationUtils;
import cn.taketoday.test.context.TestContextBootstrapper;
import cn.taketoday.test.context.support.DefaultTestContextBootstrapper;

/**
 * Web-specific implementation of the {@link TestContextBootstrapper} SPI.
 *
 * <ul>
 * <li>Uses {@link WebDelegatingSmartContextLoader} as the default {@link ContextLoader}
 * if the test class is annotated with {@link WebAppConfiguration @WebAppConfiguration}
 * and otherwise delegates to the superclass.
 * <li>Builds a {@link WebMergedContextConfiguration} if the test class is annotated
 * with {@link WebAppConfiguration @WebAppConfiguration}.
 * </ul>
 *
 * @author Sam Brannen
 */
public class WebTestContextBootstrapper extends DefaultTestContextBootstrapper {

	/**
	 * Returns {@link WebDelegatingSmartContextLoader} if the supplied class is
	 * annotated with {@link WebAppConfiguration @WebAppConfiguration} and
	 * otherwise delegates to the superclass.
	 */
	@Override
	protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> testClass) {
		if (getWebAppConfiguration(testClass) != null) {
			return WebDelegatingSmartContextLoader.class;
		}
		else {
			return super.getDefaultContextLoaderClass(testClass);
		}
	}

	/**
	 * Returns a {@link WebMergedContextConfiguration} if the test class in the
	 * supplied {@code MergedContextConfiguration} is annotated with
	 * {@link WebAppConfiguration @WebAppConfiguration} and otherwise returns
	 * the supplied instance unmodified.
	 */
	@Override
	protected MergedContextConfiguration processMergedContextConfiguration(MergedContextConfiguration mergedConfig) {
		WebAppConfiguration webAppConfiguration = getWebAppConfiguration(mergedConfig.getTestClass());
		if (webAppConfiguration != null) {
			return new WebMergedContextConfiguration(mergedConfig, webAppConfiguration.value());
		}
		else {
			return mergedConfig;
		}
	}

	@Nullable
	private static WebAppConfiguration getWebAppConfiguration(Class<?> testClass) {
		return TestContextAnnotationUtils.findMergedAnnotation(testClass, WebAppConfiguration.class);
	}

}
