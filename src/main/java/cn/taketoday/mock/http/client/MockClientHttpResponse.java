/*
 * Copyright 2002-2017 the original author or authors.
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

package cn.taketoday.mock.http.client;

import cn.taketoday.http.HttpStatus;
import cn.taketoday.http.client.ClientHttpResponse;
import cn.taketoday.lang.Assert;
import cn.taketoday.mock.http.MockHttpInputMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Mock implementation of {@link ClientHttpResponse}.
 *
 * @author Rossen Stoyanchev
 */
public class MockClientHttpResponse extends MockHttpInputMessage implements ClientHttpResponse {

	private final HttpStatus status;


	/**
	 * Constructor with response body as a byte array.
	 */
	public MockClientHttpResponse(byte[] body, HttpStatus statusCode) {
		super(body);
		Assert.notNull(statusCode, "HttpStatus is required");
		this.status = statusCode;
	}

	/**
	 * Constructor with response body as InputStream.
	 */
	public MockClientHttpResponse(InputStream body, HttpStatus statusCode) {
		super(body);
		Assert.notNull(statusCode, "HttpStatus is required");
		this.status = statusCode;
	}


	@Override
	public HttpStatus getStatusCode() throws IOException {
		return this.status;
	}

	@Override
	public int getRawStatusCode() throws IOException {
		return this.status.value();
	}

	@Override
	public String getStatusText() throws IOException {
		return this.status.getReasonPhrase();
	}

	@Override
	public void close() {
		try {
			getBody().close();
		}
		catch (IOException ex) {
			// ignore
		}
	}

}
