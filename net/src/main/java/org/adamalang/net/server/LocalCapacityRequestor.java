/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.server;

import org.adamalang.common.Callback;

/** indicate an async request to deploy a space locally */
public interface LocalCapacityRequestor {

  public void requestCodeDeployment(String space, Callback<Void> callback);
}
