/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.contracts;

import org.adamalang.web.client.socket.WebClientConnection;

/** lifecycle of a connection */
public interface WebLifecycle {
  void connected(WebClientConnection connection, String version);

  void ping(int latency);

  void failure(Throwable t);

  void disconnected();
}
