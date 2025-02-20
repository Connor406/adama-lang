/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.delta.secure.TestKey;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtDate;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DDateTests {
  @Test
  public void flow() {
    final var db = new DDate();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
    db.show(new NtDate(1, 2, 3), writer);
    db.show(new NtDate(3, 4, 5), writer);
    db.hide(writer);
    db.hide(writer);
    db.show(new NtDate(1, 2, 3), writer);
    db.show(new NtDate(3, 4, 5), writer);
    Assert.assertEquals("\"1-02-03\"\"3-04-05\"null\"1-02-03\"\"3-04-05\"", stream.toString());
    Assert.assertEquals(56, db.__memory());
    db.clear();
  }
}
