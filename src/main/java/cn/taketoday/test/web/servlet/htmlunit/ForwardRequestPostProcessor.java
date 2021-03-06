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

package cn.taketoday.test.web.servlet.htmlunit;

import cn.taketoday.lang.Assert;
import cn.taketoday.mock.web.MockHttpServletRequest;
import cn.taketoday.test.web.servlet.request.RequestPostProcessor;

/**
 * {@link RequestPostProcessor} for forward requests.
 *
 * @author Rob Winch
 * @author Sam Brannen
 */
final class ForwardRequestPostProcessor implements RequestPostProcessor {

  private final String forwardUrl;

  public ForwardRequestPostProcessor(String forwardUrl) {
    Assert.hasText(forwardUrl, "Forward URL must not be null or empty");
    this.forwardUrl = forwardUrl;
  }

  @Override
  public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
    request.setServletPath(this.forwardUrl);
    return request;
  }

}
