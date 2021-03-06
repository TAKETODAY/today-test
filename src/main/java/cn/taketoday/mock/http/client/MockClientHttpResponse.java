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

package cn.taketoday.mock.http.client;

import java.io.IOException;
import java.io.InputStream;

import cn.taketoday.http.HttpStatus;
import cn.taketoday.http.client.ClientHttpResponse;
import cn.taketoday.lang.Assert;
import cn.taketoday.mock.http.MockHttpInputMessage;

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
