/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.service;

import org.adamalang.web.service.mocks.MockServiceBase;
import org.junit.Assert;
import org.junit.Test;

public class ServiceRunnableTests {

  @Test
  public void test_interrupt() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.Mock1);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, base);
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(2500));
    Assert.assertTrue(runnable.isAccepting());
    thread.interrupt();
    thread.join();
    runnable.shutdown();
  }

  @Test
  public void test_shutdown() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.Mock2);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, base);
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    runnable.shutdown();
    thread.join();
    runnable.shutdown();
  }

  @Test
  public void test_tight_shutdown() throws Exception {
    WebConfig webConfig = WebConfigTests.mockConfig(WebConfigTests.Scenario.Mock3);
    MockServiceBase base = new MockServiceBase();
    final var runnable = new ServiceRunnable(webConfig, base);
    runnable.shutdown();
    final var thread = new Thread(runnable);
    thread.start();
    thread.join();
    runnable.shutdown();
  }
}
