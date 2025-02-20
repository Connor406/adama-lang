/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client;

public class TestClientConfig extends ClientConfig {
  public TestClientConfig() {
    super();
    this.clientQueueSize = 1024;
    this.clientQueueTimeoutMS = 500;
    this.connectionQueueSize = 8;
    this.connectionQueueTimeoutMS = 2500;
    this.connectionMaximumBackoffConnectionFailuresMS = 750;
    this.connectionMaximumBackoffFindingClientFailuresMS = 750;
    this.sendRetryDelayMS = 50;
  }
}
