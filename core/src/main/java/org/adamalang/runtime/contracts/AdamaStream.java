/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;

/** interface for a signel Adama stream/connection to a single document */
public interface AdamaStream {

  /** update the viewer state */
  void update(String newViewerState);

  /** send the document a message on the given channel */
  void send(String channel, String marker, String message, Callback<Integer> callback);

  /** send a password to the document for this person */
  void password(String password, Callback<Integer> callback);

  /** can attachments be made with the owner of the connection */
  void canAttach(Callback<Boolean> callback);

  /** attach an asset to the stream */
  void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback);

  /** close the stream */
  void close();
}
