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

package cn.taketoday.test.context.web;

import cn.taketoday.context.ApplicationContext;
import cn.taketoday.context.ConfigurableApplicationContext;
import cn.taketoday.core.Conventions;
import cn.taketoday.core.annotation.AnnotatedElementUtils;
import cn.taketoday.lang.Assert;
import cn.taketoday.logging.Logger;
import cn.taketoday.logging.LoggerFactory;
import cn.taketoday.mock.web.MockHttpServletRequest;
import cn.taketoday.mock.web.MockHttpServletResponse;
import cn.taketoday.mock.web.MockServletContext;
import cn.taketoday.test.context.TestContext;
import cn.taketoday.test.context.TestExecutionListener;
import cn.taketoday.test.context.support.AbstractTestExecutionListener;
import cn.taketoday.test.context.support.DependencyInjectionTestExecutionListener;
import cn.taketoday.web.RequestContextHolder;
import jakarta.servlet.ServletContext;

/**
 * {@code TestExecutionListener} which provides mock Servlet API support to
 * {@link WebApplicationContext WebApplicationContexts} loaded by the <em>Spring
 * TestContext Framework</em>.
 *
 * <p>Specifically, {@code ServletTestExecutionListener} sets up thread-local
 * state via Spring Web's {@link RequestContextHolder} during {@linkplain
 * #prepareTestInstance(TestContext) test instance preparation} and {@linkplain
 * #beforeTestMethod(TestContext) before each test method} and creates a {@link
 * MockHttpServletRequest}, {@link MockHttpServletResponse}, and
 * {@link ServletWebRequest} based on the {@link MockServletContext} present in
 * the {@code WebApplicationContext}. This listener also ensures that the
 * {@code MockHttpServletResponse} and {@code ServletWebRequest} can be injected
 * into the test instance, and once the test is complete this listener {@linkplain
 * #afterTestMethod(TestContext) cleans up} thread-local state.
 *
 * <p>Note that {@code ServletTestExecutionListener} is enabled by default but
 * generally takes no action if the {@linkplain TestContext#getTestClass() test
 * class} is not annotated with {@link WebAppConfiguration @WebAppConfiguration}.
 * See the javadocs for individual methods in this class for details.
 *
 * @author Sam Brannen
 * @author Phillip Webb
 */
public class ServletTestExecutionListener extends AbstractTestExecutionListener {

  /**
   * Attribute name for a {@link TestContext} attribute which indicates
   * whether or not the {@code ServletTestExecutionListener} should {@linkplain
   * RequestContextHolder#resetRequestAttributes() reset} Spring Web's
   * {@code RequestContextHolder} in {@link #afterTestMethod(TestContext)}.
   * <p>Permissible values include {@link Boolean#TRUE} and {@link Boolean#FALSE}.
   */
  public static final String RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE = Conventions.getQualifiedAttributeName(
          ServletTestExecutionListener.class, "resetRequestContextHolder");

  /**
   * Attribute name for a {@link TestContext} attribute which indicates that
   * {@code ServletTestExecutionListener} has already populated Spring Web's
   * {@code RequestContextHolder}.
   * <p>Permissible values include {@link Boolean#TRUE} and {@link Boolean#FALSE}.
   */
  public static final String POPULATED_REQUEST_CONTEXT_HOLDER_ATTRIBUTE = Conventions.getQualifiedAttributeName(
          ServletTestExecutionListener.class, "populatedRequestContextHolder");

  /**
   * Attribute name for a request attribute which indicates that the
   * {@link MockHttpServletRequest} stored in the {@link RequestAttributes}
   * in Spring Web's {@link RequestContextHolder} was created by the TestContext
   * framework.
   * <p>Permissible values include {@link Boolean#TRUE} and {@link Boolean#FALSE}.
   */
  public static final String CREATED_BY_THE_TESTCONTEXT_FRAMEWORK = Conventions.getQualifiedAttributeName(
          ServletTestExecutionListener.class, "createdByTheTestContextFramework");

  /**
   * Attribute name for a {@link TestContext} attribute which indicates that the
   * {@code ServletTestExecutionListener} should be activated. When not set to
   * {@code true}, activation occurs when the {@linkplain TestContext#getTestClass()
   * test class} is annotated with {@link WebAppConfiguration @WebAppConfiguration}.
   * <p>Permissible values include {@link Boolean#TRUE} and {@link Boolean#FALSE}.
   */
  public static final String ACTIVATE_LISTENER = Conventions.getQualifiedAttributeName(
          ServletTestExecutionListener.class, "activateListener");


  private static final Logger logger = LoggerFactory.getLogger(ServletTestExecutionListener.class);


  /**
   * Returns {@code 1000}.
   */
  @Override
  public final int getOrder() {
    return 1000;
  }

  /**
   * Sets up thread-local state during the <em>test instance preparation</em>
   * callback phase via Spring Web's {@link RequestContextHolder}, but only if
   * the {@linkplain TestContext#getTestClass() test class} is annotated with
   * {@link WebAppConfiguration @WebAppConfiguration}.
   *
   * @see TestExecutionListener#prepareTestInstance(TestContext)
   * @see #setUpRequestContextIfNecessary(TestContext)
   */
  @Override
  public void prepareTestInstance(TestContext testContext) throws Exception {
    setUpRequestContextIfNecessary(testContext);
  }

  /**
   * Sets up thread-local state before each test method via Spring Web's
   * {@link RequestContextHolder}, but only if the
   * {@linkplain TestContext#getTestClass() test class} is annotated with
   * {@link WebAppConfiguration @WebAppConfiguration}.
   *
   * @see TestExecutionListener#beforeTestMethod(TestContext)
   * @see #setUpRequestContextIfNecessary(TestContext)
   */
  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    setUpRequestContextIfNecessary(testContext);
  }

  /**
   * If the {@link #RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE} in the supplied
   * {@code TestContext} has a value of {@link Boolean#TRUE}, this method will
   * (1) clean up thread-local state after each test method by {@linkplain
   * RequestContextHolder#resetRequestAttributes() resetting} Spring Web's
   * {@code RequestContextHolder} and (2) ensure that new mocks are injected
   * into the test instance for subsequent tests by setting the
   * {@link DependencyInjectionTestExecutionListener#REINJECT_DEPENDENCIES_ATTRIBUTE}
   * in the test context to {@code true}.
   * <p>The {@link #RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE} and
   * {@link #POPULATED_REQUEST_CONTEXT_HOLDER_ATTRIBUTE} will be subsequently
   * removed from the test context, regardless of their values.
   *
   * @see TestExecutionListener#afterTestMethod(TestContext)
   */
  @Override
  public void afterTestMethod(TestContext testContext) throws Exception {
    if (Boolean.TRUE.equals(testContext.getAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE))) {
      if (logger.isDebugEnabled()) {
        logger.debug(String.format("Resetting RequestContextHolder for test context %s.", testContext));
      }
      RequestContextHolder.resetContext();
      testContext.setAttribute(DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE,
              Boolean.TRUE);
    }
    testContext.removeAttribute(POPULATED_REQUEST_CONTEXT_HOLDER_ATTRIBUTE);
    testContext.removeAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE);
  }

  private boolean isActivated(TestContext testContext) {
    return (Boolean.TRUE.equals(testContext.getAttribute(ACTIVATE_LISTENER)) ||
            AnnotatedElementUtils.hasAnnotation(testContext.getTestClass(), WebAppConfiguration.class));
  }

  private boolean alreadyPopulatedRequestContextHolder(TestContext testContext) {
    return Boolean.TRUE.equals(testContext.getAttribute(POPULATED_REQUEST_CONTEXT_HOLDER_ATTRIBUTE));
  }

  private void setUpRequestContextIfNecessary(TestContext testContext) {
    if (!isActivated(testContext) || alreadyPopulatedRequestContextHolder(testContext)) {
      return;
    }

    ApplicationContext context = testContext.getApplicationContext();

    if (context instanceof WebApplicationContext wac) {
      ServletContext servletContext = wac.getServletContext();
      Assert.state(servletContext instanceof MockServletContext, () -> String.format(
              "The WebApplicationContext for test context %s must be configured with a MockServletContext.",
              testContext));

      if (logger.isDebugEnabled()) {
        logger.debug(String.format(
                "Setting up MockHttpServletRequest, MockHttpServletResponse, ServletWebRequest, and RequestContextHolder for test context %s.",
                testContext));
      }

      MockServletContext mockServletContext = (MockServletContext) servletContext;
      MockHttpServletRequest request = new MockHttpServletRequest(mockServletContext);
      request.setAttribute(CREATED_BY_THE_TESTCONTEXT_FRAMEWORK, Boolean.TRUE);
      MockHttpServletResponse response = new MockHttpServletResponse();
      ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);

      RequestContextHolder.setRequestAttributes(servletWebRequest);
      testContext.setAttribute(POPULATED_REQUEST_CONTEXT_HOLDER_ATTRIBUTE, Boolean.TRUE);
      testContext.setAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE, Boolean.TRUE);

      if (wac instanceof ConfigurableApplicationContext configurableApplicationContext) {
        ConfigurableListableBeanFactory bf = configurableApplicationContext.getBeanFactory();
        bf.registerResolvableDependency(MockHttpServletResponse.class, response);
        bf.registerResolvableDependency(ServletWebRequest.class, servletWebRequest);
      }
    }
  }

}
