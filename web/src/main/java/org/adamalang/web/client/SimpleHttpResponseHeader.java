/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.client;

import java.util.Map;

/** simplified http response */
public class SimpleHttpResponseHeader {
  public final int status;
  public final Map<String, String> headers;

  public SimpleHttpResponseHeader(int status, Map<String, String> headers) {
    this.status = status;
    this.headers = headers;
  }
}
