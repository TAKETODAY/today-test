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

import cn.taketoday.core.MethodIntrospector;
import cn.taketoday.core.annotation.MergedAnnotations;
import cn.taketoday.lang.Nullable;
import cn.taketoday.test.context.ContextConfigurationAttributes;
import cn.taketoday.test.context.ContextCustomizerFactory;
import cn.taketoday.test.context.DynamicPropertySource;
import cn.taketoday.test.context.TestContextAnnotationUtils;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link ContextCustomizerFactory} to support
 * {@link DynamicPropertySource @DynamicPropertySource} methods.
 *
 * @author Phillip Webb
 * @author Sam Brannen
 * @see DynamicPropertiesContextCustomizer
 */
class DynamicPropertiesContextCustomizerFactory implements ContextCustomizerFactory {

	@Override
	@Nullable
	public DynamicPropertiesContextCustomizer createContextCustomizer(Class<?> testClass,
																																		List<ContextConfigurationAttributes> configAttributes) {

		Set<Method> methods = new LinkedHashSet<>();
		findMethods(testClass, methods);
		if (methods.isEmpty()) {
			return null;
		}
		return new DynamicPropertiesContextCustomizer(methods);
	}

	private void findMethods(Class<?> testClass, Set<Method> methods) {
		methods.addAll(MethodIntrospector.selectMethods(testClass, this::isAnnotated));
		if (TestContextAnnotationUtils.searchEnclosingClass(testClass)) {
			findMethods(testClass.getEnclosingClass(), methods);
		}
	}

	private boolean isAnnotated(Method method) {
		return MergedAnnotations.from(method).isPresent(DynamicPropertySource.class);
	}

}
