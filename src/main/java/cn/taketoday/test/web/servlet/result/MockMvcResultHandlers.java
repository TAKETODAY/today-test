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

package cn.taketoday.test.web.servlet.result;

import cn.taketoday.lang.Nullable;
import cn.taketoday.logging.Logger;
import cn.taketoday.logging.LoggerFactory;
import cn.taketoday.test.web.servlet.MvcResult;
import cn.taketoday.test.web.servlet.ResultHandler;
import cn.taketoday.util.CollectionUtils;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Static factory methods for {@link ResultHandler}-based result actions.
 *
 * <h3>Eclipse Users</h3>
 * <p>Consider adding this class as a Java editor favorite. To navigate to
 * this setting, open the Preferences and type "favorites".
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 */
public abstract class MockMvcResultHandlers {

	private static final Logger logger = LoggerFactory.getLogger("cn.taketoday.test.web.servlet.result");


	/**
	 * Log {@link MvcResult} details as a {@code DEBUG} log message via
	 * Apache Commons Logging using the log category
	 * {@code cn.taketoday.test.web.servlet.result}.
	 *
	 * @see #print()
	 * @see #print(OutputStream)
	 * @see #print(Writer)
	 */
	public static ResultHandler log() {
		return new LoggingResultHandler();
	}

	/**
	 * Print {@link MvcResult} details to the "standard" output stream.
	 *
	 * @see System#out
	 * @see #print(OutputStream)
	 * @see #print(Writer)
	 * @see #log()
	 */
	public static ResultHandler print() {
		return print(System.out);
	}

	/**
	 * Print {@link MvcResult} details to the supplied {@link OutputStream}.
	 *
	 * @see #print()
	 * @see #print(Writer)
	 * @see #log()
	 */
	public static ResultHandler print(OutputStream stream) {
		return new PrintWriterPrintingResultHandler(new PrintWriter(stream, true));
	}

	/**
	 * Print {@link MvcResult} details to the supplied {@link Writer}.
	 *
	 * @see #print()
	 * @see #print(OutputStream)
	 * @see #log()
	 */
	public static ResultHandler print(Writer writer) {
		return new PrintWriterPrintingResultHandler(new PrintWriter(writer, true));
	}


	/**
	 * A {@link PrintingResultHandler} that writes to a {@link PrintWriter}.
	 */
	private static class PrintWriterPrintingResultHandler extends PrintingResultHandler {

		public PrintWriterPrintingResultHandler(PrintWriter writer) {
			super(new ResultValuePrinter() {
				@Override
				public void printHeading(String heading) {
					writer.println();
					writer.println(String.format("%s:", heading));
				}

				@Override
				public void printValue(String label, @Nullable Object value) {
					if (value != null && value.getClass().isArray()) {
						value = CollectionUtils.arrayToList(value);
					}
					writer.println(String.format("%17s = %s", label, value));
				}
			});
		}
	}


	/**
	 * A {@link ResultHandler} that logs {@link MvcResult} details at
	 * {@code DEBUG} level via Apache Commons Logging.
	 *
	 * <p>Delegates to a {@link PrintWriterPrintingResultHandler} for
	 * building the log message.
	 */
	private static class LoggingResultHandler implements ResultHandler {

		@Override
		public void handle(MvcResult result) throws Exception {
			if (logger.isDebugEnabled()) {
				StringWriter stringWriter = new StringWriter();
				ResultHandler printingResultHandler =
								new PrintWriterPrintingResultHandler(new PrintWriter(stringWriter));
				printingResultHandler.handle(result);
				logger.debug("MvcResult details:\n" + stringWriter);
			}
		}
	}

}
