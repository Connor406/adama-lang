/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.index;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.index.heaps.IndexedHeap;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class IndexTests {
  public void assertEquals(String expected, Index index) {
    Assert.assertEquals(expected, index.toString());
    ByteBuf buf = Unpooled.buffer();
    index.snapshot(buf);
    Index index2 = new Index();
    index2.load(buf);
    Assert.assertEquals(expected, index2.toString());
  }

  private AnnotatedRegion wrap(Region r, AtomicInteger tracker) {
    return new AnnotatedRegion(r.position, r.size, tracker.getAndIncrement(), 0L);
  }

  @Test
  public void flow() {
    Index index = new Index();
    assertEquals("", index);
    Heap heap = new IndexedHeap(1024);
    AtomicInteger tracker = new AtomicInteger(0);

    index.append(1L, wrap(heap.ask(100), tracker));
    index.append(1L, wrap(heap.ask(100), tracker));
    assertEquals("1=[0,100=0][100,200=1];", index);
    index.append(2L, wrap(heap.ask(100), tracker));
    index.append(2L, wrap(heap.ask(100), tracker));
    assertEquals("1=[0,100=0][100,200=1];2=[200,300=2][300,400=3];", index);
    index.append(3L, wrap(heap.ask(100), tracker));
    index.append(3L, wrap(heap.ask(100), tracker));
    assertEquals("1=[0,100=0][100,200=1];2=[200,300=2][300,400=3];3=[400,500=4][500,600=5];", index);

    index.append(4L, wrap(heap.ask(1), tracker));
    index.append(4L, wrap(heap.ask(2), tracker));
    index.append(4L, wrap(heap.ask(3), tracker));
    index.append(4L, wrap(heap.ask(4), tracker));
    assertEquals("1=[0,100=0][100,200=1];2=[200,300=2][300,400=3];3=[400,500=4][500,600=5];4=[600,601=6][601,603=7][603,606=8][606,610=9];", index);

    for (Region region : index.trim(4L, 3)) {
      heap.free(region);
    }
    assertEquals("1=[0,100=0][100,200=1];2=[200,300=2][300,400=3];3=[400,500=4][500,600=5];4=[601,603=7][603,606=8][606,610=9];", index);

    Assert.assertTrue(index.exists(3));
    for (Region region : index.delete(3)) {
      heap.free(region);
    }
    Assert.assertFalse(index.exists(3));
    assertEquals("1=[0,100=0][100,200=1];2=[200,300=2][300,400=3];4=[601,603=7][603,606=8][606,610=9];", index);

    for (Region region : index.delete(4)) {
      heap.free(region);
    }
    for (Region region : index.delete(2)) {
      heap.free(region);
    }
    for (Region region : index.delete(1)) {
      heap.free(region);
    }
    assertEquals("", index);
    Assert.assertEquals("[0,1024)", heap.toString());
    Assert.assertNull(index.trim(500, 1));
  }
}
