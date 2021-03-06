/*
 * Original Author -> Harry Yang (taketoday@foxmail.com) https://taketoday.cn
 * Copyright © TODAY & 2017 - 2021 All Rights Reserved.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/]
 */

package cn.taketoday.test.context.web;

import java.io.Serial;
import java.util.Set;

import cn.taketoday.context.ApplicationContextInitializer;
import cn.taketoday.core.style.ToStringBuilder;
import cn.taketoday.lang.Nullable;
import cn.taketoday.test.context.CacheAwareContextLoaderDelegate;
import cn.taketoday.test.context.ContextCustomizer;
import cn.taketoday.test.context.ContextLoader;
import cn.taketoday.test.context.MergedContextConfiguration;
import cn.taketoday.util.ObjectUtils;
import cn.taketoday.util.StringUtils;

/**
 * {@code WebMergedContextConfiguration} encapsulates the <em>merged</em>
 * context configuration declared on a test class and all of its superclasses
 * via {@link cn.taketoday.test.context.ContextConfiguration @ContextConfiguration},
 * {@link WebAppConfiguration @WebAppConfiguration}, and
 * {@link cn.taketoday.test.context.ActiveProfiles @ActiveProfiles}.
 *
 * <p>{@code WebMergedContextConfiguration} extends the contract of
 * {@link MergedContextConfiguration} by adding support for the {@link
 * #getResourceBasePath() resource base path} configured via {@code @WebAppConfiguration}.
 * This allows the {@link cn.taketoday.test.context.TestContext TestContext}
 * to properly cache the corresponding {@link
 * cn.taketoday.web.WebApplicationContext WebApplicationContext}
 * that was loaded using properties of this {@code WebMergedContextConfiguration}.
 *
 * @author Sam Brannen
 * @see WebAppConfiguration
 * @see MergedContextConfiguration
 * @see cn.taketoday.test.context.ContextConfiguration
 * @see cn.taketoday.test.context.ActiveProfiles
 * @see cn.taketoday.test.context.ContextConfigurationAttributes
 * @see cn.taketoday.test.context.SmartContextLoader#loadContext(MergedContextConfiguration)
 */
public class WebMergedContextConfiguration extends MergedContextConfiguration {

  @Serial
  private static final long serialVersionUID = 7323361588604247458L;

  private final String resourceBasePath;

  /**
   * Create a new {@code WebMergedContextConfiguration} instance by copying
   * all properties from the supplied {@code MergedContextConfiguration}.
   * <p>If an <em>empty</em> value is supplied for the {@code resourceBasePath}
   * an empty string will be used.
   *
   * @param resourceBasePath the resource path to the root directory of the web application
   */
  public WebMergedContextConfiguration(MergedContextConfiguration mergedConfig, String resourceBasePath) {
    super(mergedConfig);
    this.resourceBasePath = !StringUtils.hasText(resourceBasePath) ? "" : resourceBasePath;
  }

  /**
   * Create a new {@code WebMergedContextConfiguration} instance for the
   * supplied parameters.
   * <p>If a {@code null} value is supplied for {@code locations},
   * {@code classes}, {@code activeProfiles}, {@code propertySourceLocations},
   * or {@code propertySourceProperties} an empty array will be stored instead.
   * If a {@code null} value is supplied for the
   * {@code contextInitializerClasses} an empty set will be stored instead.
   * If an <em>empty</em> value is supplied for the {@code resourceBasePath}
   * an empty string will be used. Furthermore, active profiles will be sorted,
   * and duplicate profiles will be removed.
   *
   * @param testClass the test class for which the configuration was merged
   * @param locations the merged resource locations
   * @param classes the merged annotated classes
   * @param contextInitializerClasses the merged context initializer classes
   * @param activeProfiles the merged active bean definition profiles
   * @param propertySourceLocations the merged {@code PropertySource} locations
   * @param propertySourceProperties the merged {@code PropertySource} properties
   * @param resourceBasePath the resource path to the root directory of the web application
   * @param contextLoader the resolved {@code ContextLoader}
   * @param cacheAwareContextLoaderDelegate a cache-aware context loader
   * delegate with which to retrieve the parent context
   * @param parent the parent configuration or {@code null} if there is no parent
   */
  public WebMergedContextConfiguration(Class<?> testClass, @Nullable String[] locations, @Nullable Class<?>[] classes,
                                       @Nullable Set<Class<? extends ApplicationContextInitializer<?>>> contextInitializerClasses,
                                       @Nullable String[] activeProfiles, @Nullable String[] propertySourceLocations, @Nullable String[] propertySourceProperties,
                                       String resourceBasePath, ContextLoader contextLoader,
                                       CacheAwareContextLoaderDelegate cacheAwareContextLoaderDelegate, @Nullable MergedContextConfiguration parent) {

    this(testClass, locations, classes, contextInitializerClasses, activeProfiles, propertySourceLocations,
         propertySourceProperties, null, resourceBasePath, contextLoader, cacheAwareContextLoaderDelegate, parent);
  }

  /**
   * Create a new {@code WebMergedContextConfiguration} instance for the
   * supplied parameters.
   * <p>If a {@code null} value is supplied for {@code locations},
   * {@code classes}, {@code activeProfiles}, {@code propertySourceLocations},
   * or {@code propertySourceProperties} an empty array will be stored instead.
   * If a {@code null} value is supplied for {@code contextInitializerClasses}
   * or {@code contextCustomizers}, an empty set will be stored instead.
   * If an <em>empty</em> value is supplied for the {@code resourceBasePath}
   * an empty string will be used. Furthermore, active profiles will be sorted,
   * and duplicate profiles will be removed.
   *
   * @param testClass the test class for which the configuration was merged
   * @param locations the merged context resource locations
   * @param classes the merged annotated classes
   * @param contextInitializerClasses the merged context initializer classes
   * @param activeProfiles the merged active bean definition profiles
   * @param propertySourceLocations the merged {@code PropertySource} locations
   * @param propertySourceProperties the merged {@code PropertySource} properties
   * @param contextCustomizers the context customizers
   * @param resourceBasePath the resource path to the root directory of the web application
   * @param contextLoader the resolved {@code ContextLoader}
   * @param cacheAwareContextLoaderDelegate a cache-aware context loader
   * delegate with which to retrieve the parent context
   * @param parent the parent configuration or {@code null} if there is no parent
   */
  public WebMergedContextConfiguration(Class<?> testClass, @Nullable String[] locations, @Nullable Class<?>[] classes,
                                       @Nullable Set<Class<? extends ApplicationContextInitializer<?>>> contextInitializerClasses,
                                       @Nullable String[] activeProfiles, @Nullable String[] propertySourceLocations, @Nullable String[] propertySourceProperties,
                                       @Nullable Set<ContextCustomizer> contextCustomizers, String resourceBasePath, ContextLoader contextLoader,
                                       CacheAwareContextLoaderDelegate cacheAwareContextLoaderDelegate, @Nullable MergedContextConfiguration parent) {

    super(testClass, locations, classes, contextInitializerClasses, activeProfiles, propertySourceLocations,
          propertySourceProperties, contextCustomizers, contextLoader, cacheAwareContextLoaderDelegate, parent);

    this.resourceBasePath = (StringUtils.hasText(resourceBasePath) ? resourceBasePath : "");
  }

  /**
   * Get the resource path to the root directory of the web application for the
   * {@linkplain #getTestClass() test class}, configured via {@code @WebAppConfiguration}.
   *
   * @see WebAppConfiguration
   */
  public String getResourceBasePath() {
    return this.resourceBasePath;
  }

  /**
   * Determine if the supplied object is equal to this {@code WebMergedContextConfiguration}
   * instance by comparing both object's {@linkplain #getLocations() locations},
   * {@linkplain #getClasses() annotated classes},
   * {@linkplain #getContextInitializerClasses() context initializer classes},
   * {@linkplain #getActiveProfiles() active profiles},
   * {@linkplain #getResourceBasePath() resource base path},
   * {@linkplain #getParent() parents}, and the fully qualified names of their
   * {@link #getContextLoader() ContextLoaders}.
   */
  @Override
  public boolean equals(@Nullable Object other) {
    return (this == other || (super.equals(other) &&
            this.resourceBasePath.equals(((WebMergedContextConfiguration) other).resourceBasePath)));
  }

  /**
   * Generate a unique hash code for all properties of this
   * {@code WebMergedContextConfiguration} excluding the
   * {@linkplain #getTestClass() test class}.
   */
  @Override
  public int hashCode() {
    return (31 * super.hashCode() + this.resourceBasePath.hashCode());
  }

  /**
   * Provide a String representation of the {@linkplain #getTestClass() test class},
   * {@linkplain #getLocations() locations}, {@linkplain #getClasses() annotated classes},
   * {@linkplain #getContextInitializerClasses() context initializer classes},
   * {@linkplain #getActiveProfiles() active profiles},
   * {@linkplain #getPropertySourceLocations() property source locations},
   * {@linkplain #getPropertySourceProperties() property source properties},
   * {@linkplain #getContextCustomizers() context customizers},
   * {@linkplain #getResourceBasePath() resource base path}, the name of the
   * {@link #getContextLoader() ContextLoader}, and the
   * {@linkplain #getParent() parent configuration}.
   */
  @Override
  public String toString() {
    return new ToStringBuilder(this)
            .append("testClass", getTestClass())
            .append("locations", ObjectUtils.nullSafeToString(getLocations()))
            .append("classes", ObjectUtils.nullSafeToString(getClasses()))
            .append("contextInitializerClasses", ObjectUtils.nullSafeToString(getContextInitializerClasses()))
            .append("activeProfiles", ObjectUtils.nullSafeToString(getActiveProfiles()))
            .append("propertySourceLocations", ObjectUtils.nullSafeToString(getPropertySourceLocations()))
            .append("propertySourceProperties", ObjectUtils.nullSafeToString(getPropertySourceProperties()))
            .append("contextCustomizers", getContextCustomizers())
            .append("resourceBasePath", getResourceBasePath())
            .append("contextLoader", nullSafeClassName(getContextLoader()))
            .append("parent", getParent())
            .toString();
  }

}
