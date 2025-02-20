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

/** a simplified http request */
public class SimpleHttpRequest {
  public final String method;
  public final String url;
  public final Map<String, String> headers;
  public final SimpleHttpRequestBody body;

  public SimpleHttpRequest(String method, String url, Map<String, String> headers, SimpleHttpRequestBody body) {
    this.method = method;
    this.url = url;
    this.headers = headers;
    this.body = body;
  }
}
