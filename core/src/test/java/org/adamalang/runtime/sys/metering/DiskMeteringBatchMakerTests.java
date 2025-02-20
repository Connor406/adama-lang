/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.metering;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DiskMeteringBatchMakerTests {

  private Runnable makeAdvance1000(MockTime time, DiskMeteringBatchMaker maker, String space) {
    return  () -> {
      maker.write(
          new MeterReading(
              time.nowMilliseconds(),
              100,
              space,
              "hash",
              new PredictiveInventory.MeteringSample(1000, 5234242, 4, 1000, 128, 42, 100, 200)));
      time.time += 1000;
    };
  }

  @Test
  public void happy() throws Exception {
    boolean failedDelete = false;
    MockTime time = new MockTime(150000);
    File root = new File(File.createTempFile("ADAMATEST_", "123").getParentFile(), "temp_" + System.currentTimeMillis());
    try {
      root.mkdirs();
      Assert.assertTrue(root.isDirectory());
      DiskMeteringBatchMaker maker = new DiskMeteringBatchMaker(time, SimpleExecutor.NOW, root, 3600000L);
      Runnable advance1000 = makeAdvance1000(time, maker, "space");
      for (int k = 0; k < 3599; k++) {
        advance1000.run();
      }
      Assert.assertNull(maker.getNextAvailableBatchId());
      for (int k = 0; k < 200; k++) {
        advance1000.run();
      }
      String batchId = maker.getNextAvailableBatchId();
      Assert.assertNotNull(batchId);
      Assert.assertEquals(
          "{\"time\":\"3751000\",\"spaces\":{\"space\":{\"cpu\":\"18853739684\",\"messages\":\"3602000\",\"count_p95\":\"4\",\"memory_p95\":\"1000\",\"connections_p95\":\"128\",\"bandwidth\":\"151284\",\"first_party_service_calls\":\"360200\",\"third_party_service_calls\":\"720400\"}}}",
          maker.getBatch(batchId));
      maker.deleteBatch(batchId);
      CountDownLatch latchFlush = new CountDownLatch(1);
      maker.flush(latchFlush);
      Assert.assertTrue(latchFlush.await(1000, TimeUnit.MILLISECONDS));
      String batchId2 = maker.getNextAvailableBatchId();
      maker.deleteBatch(batchId2);

    } finally {
      for (File child : root.listFiles()) {
        if (child.getName().startsWith("SUMMARY")) {
          failedDelete = true;
        }
        child.delete();
      }
      root.delete();
    }
    Assert.assertFalse(failedDelete);
  }

  @Test
  public void recoverFromClose() throws Exception {
    boolean failedDelete = false;
    MockTime time = new MockTime(150000);
    File root = new File(File.createTempFile("ADAMATEST_", "123").getParentFile(), "temp_" + System.currentTimeMillis());
    try {
      root.mkdirs();
      Assert.assertTrue(root.isDirectory());
      {
        DiskMeteringBatchMaker maker = new DiskMeteringBatchMaker(time, SimpleExecutor.NOW, root, 3600000L);
        Runnable advance1000 = makeAdvance1000(time, maker, "space");
        for (int k = 0; k < 3599; k++) {
          advance1000.run();
        }
        maker.close();
      }
      DiskMeteringBatchMaker maker = new DiskMeteringBatchMaker(time, SimpleExecutor.NOW, root, 3600000L);
      Assert.assertNull(maker.getNextAvailableBatchId());
      Runnable advance1000 = makeAdvance1000(time, maker, "space");
      for (int k = 0; k < 200; k++) {
        advance1000.run();
      }
      String batchId = maker.getNextAvailableBatchId();
      Assert.assertNotNull(batchId);
      Assert.assertEquals(
          "{\"time\":\"3751000\",\"spaces\":{\"space\":{\"cpu\":\"18853739684\",\"messages\":\"3602000\",\"count_p95\":\"4\",\"memory_p95\":\"1000\",\"connections_p95\":\"128\",\"bandwidth\":\"151284\",\"first_party_service_calls\":\"360200\",\"third_party_service_calls\":\"720400\"}}}",
          maker.getBatch(batchId));
      maker.deleteBatch(batchId);
    } finally {
      for (File child : root.listFiles()) {
        if (child.getName().startsWith("SUMMARY")) {
          failedDelete = true;
        }
        child.delete();
      }
      root.delete();
    }
    Assert.assertFalse(failedDelete);
  }


  @Test
  public void recoverFromCorruptionButExperienceDataLoss() throws Exception {
    boolean failedDelete = false;
    MockTime time = new MockTime(150000);
    File root = new File(File.createTempFile("ADAMATEST_", "123").getParentFile(), "temp_" + System.currentTimeMillis());
    try {
      root.mkdirs();
      Assert.assertTrue(root.isDirectory());
      {
        DiskMeteringBatchMaker maker = new DiskMeteringBatchMaker(time, SimpleExecutor.NOW, root, 3600000L);
        Runnable advance1000 = makeAdvance1000(time, maker, "space");
        for (int k = 0; k < 3599; k++) {
          advance1000.run();
        }
        maker.close();
      }
      {
        ArrayList<String> input = new ArrayList<>();
        try(FileReader reader = new FileReader(new File(root, "CURRENT"))) {
          try(BufferedReader buffered = new BufferedReader(reader)) {
            String ln;
            while ((ln = buffered.readLine()) != null) {
              input.add(ln);
            }
          }
        }
        int track = 0;
        try (OutputStream out = new FileOutputStream(new File(root, "CURRENT"))) {
          try (PrintWriter writer = new PrintWriter(out)) {
            for (String in : input) {
              track++;
              if (track > 3000) {
                // just don't write it
              } else if (track > 2500 && track % 3 == 0 || track > 2990) {
                writer.println(in.substring(0, in.length() / 2));
              } else {
                writer.println(in);
              }
            }
            writer.flush();
          }
        }
      }

      DiskMeteringBatchMaker maker = new DiskMeteringBatchMaker(time, SimpleExecutor.NOW, root, 3600000L);
      Assert.assertNull(maker.getNextAvailableBatchId());
      Runnable advance1000 = makeAdvance1000(time, maker, "space");
      for (int k = 0; k < 200; k++) {
        advance1000.run();
      }
      String batchId = maker.getNextAvailableBatchId();
      Assert.assertNotNull(batchId);
      Assert.assertEquals(
          "{\"time\":\"3751000\",\"spaces\":{\"space\":{\"cpu\":\"14812904860\",\"messages\":\"2830000\",\"count_p95\":\"4\",\"memory_p95\":\"1000\",\"connections_p95\":\"128\",\"bandwidth\":\"118860\",\"first_party_service_calls\":\"283000\",\"third_party_service_calls\":\"566000\"}}}",
          maker.getBatch(batchId));
      maker.deleteBatch(batchId);
    } finally {
      for (File child : root.listFiles()) {
        if (child.getName().startsWith("SUMMARY")) {
          failedDelete = true;
        }
        child.delete();
      }
      root.delete();
    }
    Assert.assertFalse(failedDelete);
  }

}
