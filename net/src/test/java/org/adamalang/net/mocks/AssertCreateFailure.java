/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AssertCreateFailure implements Callback<Void> {

  private final CountDownLatch latch;
  public int code;

  public AssertCreateFailure() {
    this.latch = new CountDownLatch(1);
  }

  @Override
  public void success(Void value) {
    latch.countDown();
    Assert.fail();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    this.code = ex.code;
    latch.countDown();

  }

  public void await(int expectedCode) {
    try {
      Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
      Assert.assertEquals(expectedCode, code);
    } catch (InterruptedException ie) {
      Assert.fail();
    }
  }
}
