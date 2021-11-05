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

package cn.taketoday.test.web.servlet.setup;

import cn.taketoday.beans.support.BeanUtils;
import cn.taketoday.beans.BeansException;
import cn.taketoday.beans.TypeConverter;
import cn.taketoday.beans.factory.BeanFactory;
import cn.taketoday.beans.factory.NoSuchBeanDefinitionException;
import cn.taketoday.beans.factory.ObjectProvider;
import cn.taketoday.beans.factory.config.AutowireCapableBeanFactory;
import cn.taketoday.beans.factory.config.DependencyDescriptor;
import cn.taketoday.beans.factory.config.NamedBeanHolder;
import cn.taketoday.beans.factory.support.StaticListableBeanFactory;
import cn.taketoday.context.ApplicationContext;
import cn.taketoday.context.ApplicationContextAware;
import cn.taketoday.context.ApplicationEvent;
import cn.taketoday.context.MessageSource;
import cn.taketoday.context.MessageSourceResolvable;
import cn.taketoday.context.NoSuchMessageException;
import cn.taketoday.context.support.DelegatingMessageSource;
import cn.taketoday.core.ResolvableType;
import cn.taketoday.core.env.Environment;
import cn.taketoday.core.env.StandardEnvironment;
import cn.taketoday.core.io.Resource;
import cn.taketoday.core.io.support.ResourcePatternResolver;
import cn.taketoday.lang.Nullable;
import cn.taketoday.util.ClassUtils;
import cn.taketoday.util.ObjectUtils;
import cn.taketoday.web.context.WebApplicationContext;
import cn.taketoday.web.context.support.ServletContextResourcePatternResolver;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A stub WebApplicationContext that accepts registrations of object instances.
 *
 * <p>As registered object instances are instantiated and initialized externally,
 * there is no wiring, bean initialization, lifecycle events, as well as no
 * pre-processing and post-processing hooks typically associated with beans
 * managed by an {@link ApplicationContext}. Just a simple lookup into a
 * {@link StaticListableBeanFactory}.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
class StubWebApplicationContext implements WebApplicationContext {

	private final ServletContext servletContext;

	private final StubBeanFactory beanFactory = new StubBeanFactory();

	private final String id = ObjectUtils.identityToString(this);

	private final String displayName = ObjectUtils.identityToString(this);

	private final long startupDate = System.currentTimeMillis();

	private final Environment environment = new StandardEnvironment();

	private final MessageSource messageSource = new DelegatingMessageSource();

	private final ResourcePatternResolver resourcePatternResolver;


	public StubWebApplicationContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		this.resourcePatternResolver = new ServletContextResourcePatternResolver(servletContext);
	}


	/**
	 * Returns an instance that can initialize {@link ApplicationContextAware} beans.
	 */
	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		return this.beanFactory;
	}

	@Override
	public ServletContext getServletContext() {
		return this.servletContext;
	}


	//---------------------------------------------------------------------
	// Implementation of ApplicationContext interface
	//---------------------------------------------------------------------

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getApplicationName() {
		return "";
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public long getStartupDate() {
		return this.startupDate;
	}

	@Override
	public ApplicationContext getParent() {
		return null;
	}

	@Override
	public Environment getEnvironment() {
		return this.environment;
	}

	public void addBean(String name, Object bean) {
		this.beanFactory.addBean(name, bean);
	}

	public void addBeans(@Nullable List<?> beans) {
		if (beans != null) {
			for (Object bean : beans) {
				String name = bean.getClass().getName() + "#" + ObjectUtils.getIdentityHexString(bean);
				this.beanFactory.addBean(name, bean);
			}
		}
	}


	//---------------------------------------------------------------------
	// Implementation of BeanFactory interface
	//---------------------------------------------------------------------

	@Override
	public Object getBean(String name) throws BeansException {
		return this.beanFactory.getBean(name);
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		return this.beanFactory.getBean(name, requiredType);
	}

	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		return this.beanFactory.getBean(name, args);
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		return this.beanFactory.getBean(requiredType);
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		return this.beanFactory.getBean(requiredType, args);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
		return this.beanFactory.getBeanProvider(requiredType);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
		return this.beanFactory.getBeanProvider(requiredType);
	}

	@Override
	public boolean containsBean(String name) {
		return this.beanFactory.containsBean(name);
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return this.beanFactory.isSingleton(name);
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		return this.beanFactory.isPrototype(name);
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		return this.beanFactory.isTypeMatch(name, typeToMatch);
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		return this.beanFactory.isTypeMatch(name, typeToMatch);
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		return this.beanFactory.getType(name);
	}

	@Override
	public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
		return this.beanFactory.getType(name, allowFactoryBeanInit);
	}

	@Override
	public String[] getAliases(String name) {
		return this.beanFactory.getAliases(name);
	}


	//---------------------------------------------------------------------
	// Implementation of ListableBeanFactory interface
	//---------------------------------------------------------------------

	@Override
	public boolean containsBeanDefinition(String beanName) {
		return this.beanFactory.containsBeanDefinition(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		return this.beanFactory.getBeanDefinitionCount();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return this.beanFactory.getBeanDefinitionNames();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
		return this.beanFactory.getBeanProvider(requiredType, allowEagerInit);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit) {
		return this.beanFactory.getBeanProvider(requiredType, allowEagerInit);
	}

	@Override
	public String[] getBeanNamesForType(@Nullable ResolvableType type) {
		return this.beanFactory.getBeanNamesForType(type);
	}

	@Override
	public String[] getBeanNamesForType(@Nullable ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
		return this.beanFactory.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public String[] getBeanNamesForType(@Nullable Class<?> type) {
		return this.beanFactory.getBeanNamesForType(type);
	}

	@Override
	public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		return this.beanFactory.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
		return this.beanFactory.getBeansOfType(type);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
					throws BeansException {

		return this.beanFactory.getBeansOfType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		return this.beanFactory.getBeanNamesForAnnotation(annotationType);
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
					throws BeansException {

		return this.beanFactory.getBeansWithAnnotation(annotationType);
	}

	@Override
	@Nullable
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
					throws NoSuchBeanDefinitionException {

		return this.beanFactory.findAnnotationOnBean(beanName, annotationType);
	}


	//---------------------------------------------------------------------
	// Implementation of HierarchicalBeanFactory interface
	//---------------------------------------------------------------------

	@Override
	public BeanFactory getParentBeanFactory() {
		return null;
	}

	@Override
	public boolean containsLocalBean(String name) {
		return this.beanFactory.containsBean(name);
	}


	//---------------------------------------------------------------------
	// Implementation of MessageSource interface
	//---------------------------------------------------------------------

	@Override
	public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
		return this.messageSource.getMessage(code, args, defaultMessage, locale);
	}

	@Override
	public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
		return this.messageSource.getMessage(code, args, locale);
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return this.messageSource.getMessage(resolvable, locale);
	}


	//---------------------------------------------------------------------
	// Implementation of ResourceLoader interface
	//---------------------------------------------------------------------

	@Override
	@Nullable
	public ClassLoader getClassLoader() {
		return ClassUtils.getDefaultClassLoader();
	}

	@Override
	public Resource getResource(String location) {
		return this.resourcePatternResolver.getResource(location);
	}


	//---------------------------------------------------------------------
	// Other
	//---------------------------------------------------------------------

	@Override
	public void publishEvent(ApplicationEvent event) {
	}

	@Override
	public void publishEvent(Object event) {
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		return this.resourcePatternResolver.getResources(locationPattern);
	}


	/**
	 * An extension of StaticListableBeanFactory that implements
	 * AutowireCapableBeanFactory in order to allow bean initialization of
	 * {@link ApplicationContextAware} singletons.
	 */
	private class StubBeanFactory extends StaticListableBeanFactory implements AutowireCapableBeanFactory {

		@Override
		public Object initializeBean(Object existingBean, String beanName) throws BeansException {
			if (existingBean instanceof ApplicationContextAware) {
				((ApplicationContextAware) existingBean).setApplicationContext(StubWebApplicationContext.this);
			}
			return existingBean;
		}

		@Override
		public <T> T createBean(Class<T> beanClass) {
			return BeanUtils.instantiateClass(beanClass);
		}

		@Override
		public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
			return BeanUtils.instantiateClass(beanClass);
		}

		@Override
		public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
			return BeanUtils.instantiateClass(beanClass);
		}

		@Override
		public void autowireBean(Object existingBean) throws BeansException {
		}

		@Override
		public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) {
		}

		@Override
		public Object configureBean(Object existingBean, String beanName) {
			return existingBean;
		}

		@Override
		public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
			throw new UnsupportedOperationException("Dependency resolution not supported");
		}

		@Override
		public Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException {
			throw new UnsupportedOperationException("Dependency resolution not supported");
		}

		@Override
		@Nullable
		public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) {
			throw new UnsupportedOperationException("Dependency resolution not supported");
		}

		@Override
		@Nullable
		public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
																		@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) {
			throw new UnsupportedOperationException("Dependency resolution not supported");
		}

		@Override
		public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {
		}

		@Override
		public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) {
			return existingBean;
		}

		@Override
		public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) {
			return existingBean;
		}

		@Override
		public void destroyBean(Object existingBean) {
		}
	}

}