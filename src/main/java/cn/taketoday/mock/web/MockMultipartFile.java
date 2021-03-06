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

package cn.taketoday.mock.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cn.taketoday.lang.Assert;
import cn.taketoday.lang.NonNull;
import cn.taketoday.lang.Nullable;
import cn.taketoday.util.FileCopyUtils;
import cn.taketoday.web.multipart.MultipartFile;

/**
 * Mock implementation of the {@link cn.taketoday.web.multipart.MultipartFile}
 * interface.
 *
 * <p>Useful in conjunction with a {@link MockMultipartHttpServletRequest}
 * for testing application controllers that access multipart uploads.
 *
 * @author Juergen Hoeller
 * @author Eric Crampton
 * @see MockMultipartHttpServletRequest
 */
public class MockMultipartFile implements MultipartFile {

  private final String name;

  private final String originalFilename;

  @Nullable
  private final String contentType;

  private final byte[] content;

  /**
   * Create a new MockMultipartFile with the given content.
   *
   * @param name the name of the file
   * @param content the content of the file
   */
  public MockMultipartFile(String name, @Nullable byte[] content) {
    this(name, "", null, content);
  }

  /**
   * Create a new MockMultipartFile with the given content.
   *
   * @param name the name of the file
   * @param contentStream the content of the file as stream
   * @throws IOException if reading from the stream failed
   */
  public MockMultipartFile(String name, InputStream contentStream) throws IOException {
    this(name, "", null, FileCopyUtils.copyToByteArray(contentStream));
  }

  /**
   * Create a new MockMultipartFile with the given content.
   *
   * @param name the name of the file
   * @param originalFilename the original filename (as on the client's machine)
   * @param contentType the content type (if known)
   * @param content the content of the file
   */
  public MockMultipartFile(
          String name, @Nullable String originalFilename, @Nullable String contentType, @Nullable byte[] content) {

    Assert.hasLength(name, "Name must not be empty");
    this.name = name;
    this.originalFilename = (originalFilename != null ? originalFilename : "");
    this.contentType = contentType;
    this.content = (content != null ? content : new byte[0]);
  }

  /**
   * Create a new MockMultipartFile with the given content.
   *
   * @param name the name of the file
   * @param originalFilename the original filename (as on the client's machine)
   * @param contentType the content type (if known)
   * @param contentStream the content of the file as stream
   * @throws IOException if reading from the stream failed
   */
  public MockMultipartFile(
          String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream)
          throws IOException {

    this(name, originalFilename, contentType, FileCopyUtils.copyToByteArray(contentStream));
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  @NonNull
  public String getFileName() {
    return this.originalFilename;
  }

  @Override
  @Nullable
  public String getContentType() {
    return this.contentType;
  }

  @Override
  public boolean isEmpty() {
    return (this.content.length == 0);
  }

  @Override
  public long getSize() {
    return this.content.length;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return this.content;
  }

  @Override
  public Object getOriginalResource() {
    return content;
  }

  @Override
  public void delete() throws IOException { }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(this.content);
  }

  @Override
  public void save(File dest) throws IOException {
    FileCopyUtils.copy(this.content, dest);
  }

}
