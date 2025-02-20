/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.contracts;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.client.contracts.impl.CallbackByteStreamWriter;
import org.adamalang.net.mocks.LatchedVoidCallback;
import org.junit.Test;

public class CallbackByteStreamWriterTests {

  @Test
  public void proxy() {
    LatchedVoidCallback callback = new LatchedVoidCallback();
    CallbackByteStreamWriter<Void> test = new CallbackByteStreamWriter<Void>(callback) {
      @Override
      public void write(ByteStream stream) {

      }
    };
    test.failure(new ErrorCodeException(123));
    callback.assertFail(123);
  }
}
