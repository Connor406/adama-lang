/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

public class DeleteTests {
  @Test
  public void flow() {
    Delete delete = new Delete(123l);
    ByteBuf buf = Unpooled.buffer();
    delete.write(buf);
    Assert.assertEquals(0x66, buf.readByte());
    Delete delete2 = Delete.readAfterTypeId(buf);
    Assert.assertEquals(123L, delete2.id);
  }
}
