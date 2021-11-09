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

package cn.taketoday.test.context.cache;

import cn.taketoday.context.ApplicationContext;
import cn.taketoday.lang.Assert;
import cn.taketoday.lang.NonNull;
import cn.taketoday.lang.Nullable;
import cn.taketoday.logging.Logger;
import cn.taketoday.logging.LoggerFactory;
import cn.taketoday.test.annotation.DirtiesContext.HierarchyMode;
import cn.taketoday.test.context.CacheAwareContextLoaderDelegate;
import cn.taketoday.test.context.ContextLoader;
import cn.taketoday.test.context.MergedContextConfiguration;
import cn.taketoday.test.context.SmartContextLoader;

/**
 * Default implementation of the {@link CacheAwareContextLoaderDelegate} interface.
 *
 * <p>To use a static {@link DefaultContextCache}, invoke the
 * {@link #DefaultCacheAwareContextLoaderDelegate()} constructor; otherwise,
 * invoke the {@link #DefaultCacheAwareContextLoaderDelegate(ContextCache)}
 * and provide a custom {@link ContextCache} implementation.
 *
 * @author Sam Brannen
 */
public class DefaultCacheAwareContextLoaderDelegate implements CacheAwareContextLoaderDelegate {

  private static final Logger logger = LoggerFactory.getLogger(DefaultCacheAwareContextLoaderDelegate.class);

  /**
   * Default static cache of Spring application contexts.
   */
  static final ContextCache defaultContextCache = new DefaultContextCache();

  private final ContextCache contextCache;

  /**
   * Construct a new {@code DefaultCacheAwareContextLoaderDelegate} using
   * a static {@link DefaultContextCache}.
   * <p>This default cache is static so that each context can be cached
   * and reused for all subsequent tests that declare the same unique
   * context configuration within the same JVM process.
   *
   * @see #DefaultCacheAwareContextLoaderDelegate(ContextCache)
   */
  public DefaultCacheAwareContextLoaderDelegate() {
    this(defaultContextCache);
  }

  /**
   * Construct a new {@code DefaultCacheAwareContextLoaderDelegate} using
   * the supplied {@link ContextCache}.
   *
   * @see #DefaultCacheAwareContextLoaderDelegate()
   */
  public DefaultCacheAwareContextLoaderDelegate(ContextCache contextCache) {
    Assert.notNull(contextCache, "ContextCache must not be null");
    this.contextCache = contextCache;
  }

  /**
   * Get the {@link ContextCache} used by this context loader delegate.
   */
  protected ContextCache getContextCache() {
    return this.contextCache;
  }

  /**
   * Load the {@code ApplicationContext} for the supplied merged context configuration.
   * <p>Supports both the {@link SmartContextLoader} and {@link ContextLoader} SPIs.
   *
   * @throws Exception if an error occurs while loading the application context
   */
  protected ApplicationContext loadContextInternal(MergedContextConfiguration mergedContextConfiguration)
          throws Exception {

    ContextLoader contextLoader = mergedContextConfiguration.getContextLoader();
    Assert.notNull(contextLoader, "Cannot load an ApplicationContext with a NULL 'contextLoader'. " +
            "Consider annotating your test class with @ContextConfiguration or @ContextHierarchy.");

    ApplicationContext applicationContext;

    if (contextLoader instanceof SmartContextLoader smartContextLoader) {
      applicationContext = smartContextLoader.loadContext(mergedContextConfiguration);
    }
    else {
      String[] locations = mergedContextConfiguration.getLocations();
      Assert.notNull(locations, "Cannot load an ApplicationContext with a NULL 'locations' array. " +
              "Consider annotating your test class with @ContextConfiguration or @ContextHierarchy.");
      applicationContext = contextLoader.loadContext(locations);
    }

    return applicationContext;
  }

  @Override
  public boolean isContextLoaded(MergedContextConfiguration mergedContextConfiguration) {
    synchronized(this.contextCache) {
      return this.contextCache.contains(mergedContextConfiguration);
    }
  }

  @Override
  public ApplicationContext loadContext(@NonNull MergedContextConfiguration mergedContextConfiguration) {
    synchronized(this.contextCache) {
      ApplicationContext context = this.contextCache.get(mergedContextConfiguration);
      if (context == null) {
        try {
          context = loadContextInternal(mergedContextConfiguration);
          if (logger.isDebugEnabled()) {
            logger.debug(String.format("Storing ApplicationContext [%s] in cache under key [%s]",
                    System.identityHashCode(context), mergedContextConfiguration));
          }
          this.contextCache.put(mergedContextConfiguration, context);
        }
        catch (Exception ex) {
          throw new IllegalStateException("Failed to load ApplicationContext", ex);
        }
      }
      else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Retrieved ApplicationContext [%s] from cache with key [%s]",
                  System.identityHashCode(context), mergedContextConfiguration));
        }
      }

      this.contextCache.logStatistics();

      return context;
    }
  }

  @Override
  public void closeContext(@NonNull MergedContextConfiguration mergedContextConfiguration, @Nullable HierarchyMode hierarchyMode) {
    synchronized(this.contextCache) {
      this.contextCache.remove(mergedContextConfiguration, hierarchyMode);
    }
  }

}
