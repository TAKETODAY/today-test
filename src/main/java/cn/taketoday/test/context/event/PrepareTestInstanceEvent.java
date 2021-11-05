/*
 * Copyright 2002-2019 the original author or authors.
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

package cn.taketoday.test.context.event;

import cn.taketoday.test.context.TestContext;

/**
 * {@link TestContextEvent} published by the {@link EventPublishingTestExecutionListener} when
 * {@link cn.taketoday.test.context.TestExecutionListener#prepareTestInstance(TestContext)}
 * is invoked.
 *
 * @author Frank Scheffler
 * @see cn.taketoday.test.context.event.annotation.PrepareTestInstance @PrepareTestInstance
 */
@SuppressWarnings("serial")
public class PrepareTestInstanceEvent extends TestContextEvent {

	public PrepareTestInstanceEvent(TestContext source) {
		super(source);
	}

}
