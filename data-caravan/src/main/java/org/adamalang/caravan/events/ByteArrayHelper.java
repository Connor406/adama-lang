/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.events;

import io.netty.buffer.ByteBuf;

public class ByteArrayHelper {
  public static byte[] convert(ByteBuf buf) {
    byte[] memory = new byte[buf.writerIndex()];
    while (buf.readerIndex() < buf.writerIndex()) {
      buf.readBytes(memory, buf.readerIndex(), memory.length - buf.readerIndex());
    }
    return memory;
  }
}
