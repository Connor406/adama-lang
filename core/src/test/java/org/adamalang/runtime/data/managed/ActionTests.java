/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data.managed;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Test;

public class ActionTests {
  @Test
  public void coverage() {
    Action action = new Action(() -> {
    }, new Callback<Void>() {
      @Override
      public void success(Void value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
  }
}
