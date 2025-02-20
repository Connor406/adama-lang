/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.data;

public class ResourcesPerPenny {
  public double cpu;
  public double messages;
  public double count;
  public double memory;
  public double connections;
  public long storage;
  public long bandwidth;
  public long first_party_service_calls;
  public long third_party_service_calls;

  public ResourcesPerPenny(double cpu, double messages, double count, double memory, double connections, long storage, long bandwidth, long first_party_service_calls, long third_party_service_calls) {
    this.cpu = cpu;
    this.messages = messages;
    this.count = count;
    this.memory = memory;
    this.connections = connections;
    this.storage = storage;
    this.bandwidth = bandwidth;
    this.first_party_service_calls = first_party_service_calls;
    this.third_party_service_calls = third_party_service_calls;
  }
}
