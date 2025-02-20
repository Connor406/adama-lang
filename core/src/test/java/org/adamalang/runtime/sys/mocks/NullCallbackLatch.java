/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NullCallbackLatch implements Callback<Void> {
  private final CountDownLatch latch;
  private final AtomicBoolean failed;
  private final AtomicInteger failed_code;

  public NullCallbackLatch() {
    this.latch = new CountDownLatch(1);
    this.failed = new AtomicBoolean(false);
    this.failed_code = new AtomicInteger(0);
  }

  @Override
  public void success(Void value) {
    latch.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    failed_code.set(ex.code);
    failed.set(true);
    latch.countDown();
  }

  public void await_success() throws Exception {
    Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
    Assert.assertFalse(failed.get());
  }

  public void await_failure() throws Exception {
    Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(failed.get());
  }

  public void await_failure(int code) throws Exception {
    Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(failed.get());
    Assert.assertEquals(code, failed_code.get());
  }
}
