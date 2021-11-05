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

package cn.taketoday.test.context.support;

import cn.taketoday.context.ConfigurableApplicationContext;
import cn.taketoday.core.env.MutablePropertySources;
import cn.taketoday.lang.Assert;
import cn.taketoday.test.context.ContextCustomizer;
import cn.taketoday.test.context.DynamicPropertyRegistry;
import cn.taketoday.test.context.DynamicPropertySource;
import cn.taketoday.test.context.MergedContextConfiguration;
import cn.taketoday.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * {@link ContextCustomizer} to support
 * {@link DynamicPropertySource @DynamicPropertySource} methods.
 *
 * @author Phillip Webb
 * @author Sam Brannen
 * @see DynamicPropertiesContextCustomizerFactory
 */
class DynamicPropertiesContextCustomizer implements ContextCustomizer {

	private static final String PROPERTY_SOURCE_NAME = "Dynamic Test Properties";


	private final Set<Method> methods;


	DynamicPropertiesContextCustomizer(Set<Method> methods) {
		methods.forEach(this::assertValid);
		this.methods = methods;
	}


	private void assertValid(Method method) {
		Assert.state(Modifier.isStatic(method.getModifiers()),
						() -> "@DynamicPropertySource method '" + method.getName() + "' must be static");
		Class<?>[] types = method.getParameterTypes();
		Assert.state(types.length == 1 && types[0] == DynamicPropertyRegistry.class,
						() -> "@DynamicPropertySource method '" + method.getName() + "' must accept a single DynamicPropertyRegistry argument");
	}

	@Override
	public void customizeContext(ConfigurableApplicationContext context,
															 MergedContextConfiguration mergedConfig) {

		MutablePropertySources sources = context.getEnvironment().getPropertySources();
		sources.addFirst(new DynamicValuesPropertySource(PROPERTY_SOURCE_NAME, buildDynamicPropertiesMap()));
	}

	private Map<String, Supplier<Object>> buildDynamicPropertiesMap() {
		Map<String, Supplier<Object>> map = new LinkedHashMap<>();
		DynamicPropertyRegistry dynamicPropertyRegistry = (name, valueSupplier) -> {
			Assert.hasText(name, "'name' must not be null or blank");
			Assert.notNull(valueSupplier, "'valueSupplier' must not be null");
			map.put(name, valueSupplier);
		};
		this.methods.forEach(method -> {
			ReflectionUtils.makeAccessible(method);
			ReflectionUtils.invokeMethod(method, null, dynamicPropertyRegistry);
		});
		return Collections.unmodifiableMap(map);
	}

	Set<Method> getMethods() {
		return this.methods;
	}

	@Override
	public int hashCode() {
		return this.methods.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return this.methods.equals(((DynamicPropertiesContextCustomizer) obj).methods);
	}

}
