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

package cn.taketoday.test.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Test annotation for use with JUnit 4 to indicate that a test method should be
 * invoked repeatedly.
 *
 * <p>Note that the scope of execution to be repeated includes execution of the
 * test method itself as well as any <em>set up</em> or <em>tear down</em> of
 * the test fixture. When used with the
 * {@link cn.taketoday.test.context.junit4.rules.SpringMethodRule
 * SpringMethodRule}, the scope additionally includes
 * {@linkplain cn.taketoday.test.context.TestExecutionListener#prepareTestInstance
 * preparation of the test instance}.
 *
 * <p>This annotation may be used as a <em>meta-annotation</em> to create custom
 * <em>composed annotations</em>.
 *
 * @author Rod Johnson
 * @author Sam Brannen
 * @see cn.taketoday.test.annotation.Timed
 * @see cn.taketoday.test.context.junit4.SpringJUnit4ClassRunner
 * @see cn.taketoday.test.context.junit4.rules.SpringMethodRule
 * @see cn.taketoday.test.context.junit4.statements.SpringRepeat
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Repeat {

	/**
	 * The number of times that the annotated test method should be repeated.
	 */
	int value() default 1;

}
