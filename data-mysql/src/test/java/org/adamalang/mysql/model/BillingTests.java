/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.model;

import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class BillingTests {
  @Test
  public void billing() {
    Assert.assertEquals(0, Billing.usageValueOfZeroIfNotPresentOrNull(Json.parseJsonObject("{}"), "x"));
    Assert.assertEquals(123, Billing.usageValueOfZeroIfNotPresentOrNull(Json.parseJsonObject("{\"x\":123}"), "x"));
  }
}
