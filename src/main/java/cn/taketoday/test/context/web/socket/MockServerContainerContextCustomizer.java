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

package cn.taketoday.test.context.web.socket;

import cn.taketoday.context.ConfigurableApplicationContext;
import cn.taketoday.lang.Nullable;
import cn.taketoday.test.context.ContextCustomizer;
import cn.taketoday.test.context.MergedContextConfiguration;
import cn.taketoday.web.servlet.WebServletApplicationContext;

import javax.servlet.ServletContext;

/**
 * {@link ContextCustomizer} that instantiates a new {@link MockServerContainer}
 * and stores it in the {@code ServletContext} under the attribute named
 * {@code "javax.websocket.server.ServerContainer"}.
 *
 * @author Sam Brannen
 */
class MockServerContainerContextCustomizer implements ContextCustomizer {

	@Override
	public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
		if (context instanceof WebServletApplicationContext) {
			WebServletApplicationContext wac = (WebServletApplicationContext) context;
			ServletContext sc = wac.getServletContext();
			if (sc != null) {
				sc.setAttribute("javax.websocket.server.ServerContainer", new MockServerContainer());
			}
		}
	}

	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other != null && getClass() == other.getClass()));
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
