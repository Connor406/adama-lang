/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.mocks;

import org.adamalang.net.client.contracts.MeteringStream;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockMeteringFlow implements MeteringStream {

  private final ArrayList<String> history;
  private ArrayList<CountDownLatch> latches;

  public MockMeteringFlow() {
    this.history = new ArrayList<>();
    latches = new ArrayList<>();
  }

  public synchronized Runnable latchAt(int write) {
    CountDownLatch latch = new CountDownLatch(write);
    latches.add(latch);
    return () -> {
      try {
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
      } catch (InterruptedException ie) {
        Assert.fail();
      }
    };
  }

  private synchronized void write(String x) {
    System.err.println("METER:" + x);
    history.add(x);
    for (CountDownLatch latch : latches) {
      latch.countDown();
    }
  }

  public synchronized void assertWrite(int write, String expected) {
    Assert.assertTrue(write < history.size());
    Assert.assertEquals(expected, history.get(write));
  }

  @Override
  public synchronized void handle(String target, String batch, Runnable after) {
    write("HANDLE!");
    after.run();
  }

  @Override
  public synchronized void failure(int code) {
    write("ERROR:" + code);
  }

  @Override
  public synchronized void finished() {
    write("FINISHED");
  }
}
