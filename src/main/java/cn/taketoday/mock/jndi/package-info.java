/**
 * <strong>Deprecated</strong> favor of complete
 * solutions from third parties such as
 * <a href="https://github.com/h-thurow/Simple-JNDI">Simple-JNDI</a>.
 *
 * <p>The simplest implementation of the JNDI SPI that could possibly work.
 *
 * <p>Useful for setting up a simple JNDI environment for test suites
 * or stand-alone applications. If, for example, JDBC DataSources get bound to the
 * same JNDI names as within a Jakarta EE container, both application code and
 * configuration can be reused without changes.
 */
@NonNullApi
@NonNullFields
package cn.taketoday.mock.jndi;

import cn.taketoday.lang.NonNullApi;
import cn.taketoday.lang.NonNullFields;
