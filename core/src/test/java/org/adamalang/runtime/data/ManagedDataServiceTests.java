/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.managed.Base;
import org.adamalang.runtime.data.mocks.*;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ManagedDataServiceTests {
  public static final Key KEY1 = new Key("space", "123");
  public static final Key KEY2 = new Key("space", "456");
  public static final Key KEY_CANT_FIND = new Key("space", "cant-find");
  public static final Key KEY_CANT_DELETE = new Key("space", "cant-delete");
  public static final Key KEY_FAIL_RESTORE = new Key("space", "fail-restore");
  public static final Key KEY_CANT_BIND = new Key("space", "cant-bind");
  public static final Key KEY_SLOW_FIND = new Key("space", "slow-find");
  public static final Key KEY_SLOW_FIND_WHILE_FINDING = new Key("space", "slow-find-delete-while-finding");
  public static final Key KEY_OFFBOX = new Key("space", "offbox");
  public static final Key KEY_RETRY_KEY = new Key("space", "retry-key");
  public static final Key KEY_CANT_DELETE_LOCAL = new Key("space", "cant-delete-delete");
  public static final Key KEY_DELETE_WHILE_ARCHIVE = new Key("space", "delete-while-archive");

  private static final RemoteDocumentUpdate UPDATE_1 =
      new RemoteDocumentUpdate(
          1, 1, NtPrincipal.NO_ONE, "REQUEST", "{\"x\":1,\"y\":4}", "{\"x\":0,\"y\":0}", false, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_2 =
      new RemoteDocumentUpdate(
          2, 2, null, "REQUEST", "{\"x\":2}", "{\"x\":1,\"z\":42}", true, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_3 =
      new RemoteDocumentUpdate(
          3, 3, null, "REQUEST", "{\"x\":3}", "{\"x\":2,\"z\":42}", true, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_4 =
      new RemoteDocumentUpdate(
          4, 4, null, "REQUEST", "{\"x\":4}", "{\"x\":3,\"z\":42}", true, 0, 100, UpdateType.AddUserData);

  private static class Setup implements AutoCloseable {
    public final MockFinderService finder;
    public final SimpleExecutor executor;
    public final MockInstantDataService data;
    public final MockArchiveDataSource archive;
    public final Base base;
    public final ManagedDataService managed;
    public final MockPostDocumentDelete delete;

    public Setup() throws Exception {
      this.finder = new MockFinderService();
      this.executor = SimpleExecutor.create("setup");
      this.data = new MockInstantDataService();
      this.archive = new MockArchiveDataSource(data);
      this.delete = new MockPostDocumentDelete();
      this.base = new Base(finder, archive, delete, "test-region", "test-machine", executor, 250);
      this.managed = new ManagedDataService(base);
    }

    @Override
    public void close() throws Exception {
      executor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
  }

  @Test
  public void delete_while_archive_is_happening() throws Exception {
    try (Setup setup = new Setup()) {
      Runnable firstArchive = setup.archive.latchLogAt(1);
      Runnable secondArchive = setup.archive.latchLogAt(2);
      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY_DELETE_WHILE_ARCHIVE, UPDATE_1, cb_Init);
        cb_Init.assertSuccess();
        firstArchive.run();
        {
          SimpleVoidCallback cb_Delete = new SimpleVoidCallback();
          setup.managed.delete(KEY_DELETE_WHILE_ARCHIVE, cb_Delete);
          cb_Delete.assertSuccess();
        }
        setup.archive.driveBackup();
        secondArchive.run();
        Assert.assertTrue(setup.delete.deleted.contains(KEY_DELETE_WHILE_ARCHIVE));
      }
    }
  }

  @Test
  public void delete_while_starting() throws Exception {
    try (Setup setup = new Setup()) {
      setup.finder.bindLocal(KEY_SLOW_FIND_WHILE_FINDING);
      Runnable gotSlow = setup.finder.latchOnSlowFind();
      SimpleDataCallback cb_Get = new SimpleDataCallback();
      setup.managed.get(KEY_SLOW_FIND_WHILE_FINDING, cb_Get);
      SimpleVoidCallback cb_Close = new SimpleVoidCallback();
      setup.managed.delete(KEY_SLOW_FIND_WHILE_FINDING, cb_Close);
      cb_Close.assertSuccess();
      gotSlow.run();
      cb_Get.assertFailure(786620);
      Assert.assertTrue(setup.delete.deleted.contains(KEY_SLOW_FIND_WHILE_FINDING));
    }
  }

  @Test
  public void flow() throws Exception {
    try (Setup setup = new Setup()) {
      Runnable firstArchive = setup.archive.latchLogAt(1);
      Runnable secondArchive = setup.archive.latchLogAt(3);
      Runnable secondArchiveExecAndDelete = setup.archive.latchLogAt(5);
      Runnable waitForDelete = setup.data.latchLogAt(7);
      Runnable firstRestore = setup.archive.latchLogAt(6);
      Runnable thirdBackup = setup.archive.latchLogAt(8);
      Runnable thirdBackupComplete = setup.archive.latchLogAt(9);
      Runnable waitForClose = setup.data.latchLogAt(13);
      Runnable cantRestoreGotBackuped = setup.archive.latchLogAt(11);

      Runnable waitForDeleteOfRestoreFailure = setup.data.latchLogAt(16);
      Runnable cantRestoreFailure = setup.archive.latchLogAt(13);
      Runnable restoreWorks = setup.archive.latchLogAt(15);
      Runnable retryBackup = setup.archive.latchLogAt(17);
      Runnable retryBackupAgain = setup.archive.latchLogAt(20);
      Runnable waitForRetryDelete = setup.data.latchLogAt(21);
      Runnable restoreAgain = setup.archive.latchLogAt(22);
      Runnable backupLoadedContent = setup.archive.latchLogAt(25);
      Runnable backupFinished = setup.archive.latchLogAt(26);

      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY_CANT_BIND, UPDATE_1, cb_Init);
        cb_Init.assertFailure(-1234);
      }
      Runnable waitInit = setup.data.latchLogAt(1);
      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY1, UPDATE_1, cb_Init);
        cb_Init.assertSuccess();
      }
      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY1, UPDATE_1, cb_Init);
        cb_Init.assertFailure(667658);
      }
      waitInit.run();
      {
        SimpleVoidCallback cb_Patch = new SimpleVoidCallback();
        setup.managed.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_2 }, cb_Patch);
        cb_Patch.assertSuccess();
      }
      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY1, cb_Get);
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":2,\"y\":4}", cb_Get.value);
      }
      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.compute(KEY1, ComputeMethod.HeadPatch, 1, cb_Get);
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":70000,\"__seq\":10000}", cb_Get.value);
      }
      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.compute(KEY1, ComputeMethod.Rewind, 1, cb_Get);
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":1000}", cb_Get.value);
      }
      {
        SimpleIntCallback cb_Snapshot = new SimpleIntCallback();
        setup.managed.snapshot(KEY1, new DocumentSnapshot(2, "{\"x\":1234}", 1, 1234L), cb_Snapshot);
        cb_Snapshot.assertSuccess(-1);
      }
      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY1, cb_Get);
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":2,\"y\":4}", cb_Get.value);
      }
      {
        SimpleVoidCallback cb_Close = new SimpleVoidCallback();
        setup.managed.close(KEY1, cb_Close);
        cb_Close.assertSuccess();
      }
      firstArchive.run();
      setup.archive.driveBackup();
      secondArchive.run();
      setup.archive.driveBackup();
      secondArchiveExecAndDelete.run();
      setup.archive.assertLogAt(0, "BACKUP:space/123");
      setup.archive.assertLogAt(1, "BACKUP-EXEC:space/123");
      setup.archive.assertLogAt(2, "BACKUP:space/123");
      setup.archive.assertLogAt(3, "BACKUP-EXEC:space/123");
      setup.archive.assertLogAt(4, "CLEAN:space/123");
      waitForDelete.run();
      setup.data.assertLogAt(0, "INIT:space/123:1->{\"x\":1,\"y\":4}");
      setup.data.assertLogAt(1, "PATCH:space/123:2-2->{\"x\":2}");
      setup.data.assertLogAt(2, "LOAD:space/123");
      setup.data.assertLogAt(3, "LOAD:space/123");
      setup.data.assertLogAt(4, "LOAD:space/123");
      setup.data.assertLogAt(5, "LOAD:space/123");
      setup.data.assertLogAt(6, "DELETE:space/123");

      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY1, cb_Get);
        firstRestore.run();
        setup.archive.driveRestore();
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":2,\"y\":4}", cb_Get.value);
      }

      {
        SimpleVoidCallback cb_Patch = new SimpleVoidCallback();
        setup.managed.patch(KEY1, new RemoteDocumentUpdate[] { UPDATE_3, UPDATE_4 }, cb_Patch);
        cb_Patch.assertSuccess();
      }

      thirdBackup.run();
      setup.archive.driveBackup();
      thirdBackupComplete.run();

      {
        SimpleVoidCallback cb_Close = new SimpleVoidCallback();
        setup.managed.close(KEY1, cb_Close);
        cb_Close.assertSuccess();
      }

      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY_CANT_FIND, UPDATE_1, cb_Init);
        cb_Init.assertFailure(-999);
      }

      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY_CANT_FIND, UPDATE_1, cb_Init);
        cb_Init.assertFailure(-999);
      }

      waitForClose.run();
      setup.data.assertLogAt(7, "INIT:space/123:2->{\"x\":2,\"y\":4}");
      setup.data.assertLogAt(8, "LOAD:space/123");
      setup.data.assertLogAt(9, "PATCH:space/123:3-3->{\"x\":3}");
      setup.data.assertLogAt(10, "PATCH:space/123:4-4->{\"x\":4}");
      setup.data.assertLogAt(11, "LOAD:space/123");
      setup.data.assertLogAt(12, "DELETE:space/123");

      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY_FAIL_RESTORE, UPDATE_1, cb_Init);
        cb_Init.assertSuccess();
        cantRestoreGotBackuped.run();
        SimpleVoidCallback cb_Close = new SimpleVoidCallback();
        setup.managed.close(KEY_FAIL_RESTORE, cb_Close);
        cb_Close.assertSuccess();
        setup.archive.driveBackup();
      }

      setup.archive.assertLogAt(5, "RESTORE-INIT:space/123");
      setup.archive.assertLogAt(6, "RESTORE-EXEC:space/123");
      setup.archive.assertLogAt(7, "BACKUP:space/123");
      setup.archive.assertLogAt(8, "BACKUP-EXEC:space/123");
      setup.archive.assertLogAt(9, "CLEAN:space/123");
      setup.archive.assertLogAt(10, "BACKUP:space/fail-restore");
      setup.archive.assertLogAt(11, "BACKUP-EXEC:space/fail-restore");

      waitForDeleteOfRestoreFailure.run();
      setup.data.assertLogAt(13, "INIT:space/fail-restore:1->{\"x\":1,\"y\":4}");
      setup.data.assertLogAt(14, "LOAD:space/fail-restore");
      setup.data.assertLogAt(15, "DELETE:space/fail-restore");

      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY_FAIL_RESTORE, cb_Get);
        cantRestoreFailure.run();
        setup.archive.driveRestore();
        cb_Get.assertFailure(-2000);
      }

      setup.archive.assertLogAt(12, "RESTORE-INIT:space/fail-restore");
      setup.archive.assertLogAt(13, "RESTORE-EXEC:space/fail-restore");

      {
        setup.finder.bindLocal(KEY_SLOW_FIND);
        Runnable gotSlow = setup.finder.latchOnSlowFind();
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY_SLOW_FIND, cb_Get);
        SimpleVoidCallback cb_Close = new SimpleVoidCallback();
        setup.managed.close(KEY_SLOW_FIND, cb_Close);
        cb_Close.assertSuccess();
        gotSlow.run();
        cb_Get.assertFailure(791691);
      }

      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY_CANT_BIND, UPDATE_1, cb_Init);
        cb_Init.assertFailure(-1234);
      }

      {
        setup.archive.forceArchive("myArchive", "{}", 1);
        setup.finder.bindArchive(KEY_CANT_BIND, "myArchive");
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY_CANT_BIND, cb_Get);
        restoreWorks.run();
        setup.archive.driveRestore();
        cb_Get.assertFailure(-1234);
      }

      {
        setup.finder.bindOtherMachine(KEY_OFFBOX);
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY_OFFBOX, cb_Get);
        cb_Get.assertFailure(735344);
      }

      setup.archive.assertLogAt(14, "RESTORE-INIT:space/cant-bind");
      setup.archive.assertLogAt(15, "RESTORE-EXEC:space/cant-bind");

      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.managed.initialize(KEY_RETRY_KEY, UPDATE_1, cb_Init);
        cb_Init.assertSuccess();
        retryBackup.run();
        setup.archive.driveBackup();
        retryBackupAgain.run();
        setup.archive.driveBackup();
        SimpleVoidCallback cb_Close = new SimpleVoidCallback();
        setup.managed.close(KEY_RETRY_KEY, cb_Close);
        cb_Close.assertSuccess();
      }

      waitForRetryDelete.run();

      setup.archive.assertLogAt(16, "BACKUP:space/retry-key");
      setup.archive.assertLogAt(17, "BACKUP-EXEC:space/retry-key");
      setup.archive.assertLogAt(18, "CLEAN:space/retry-key");
      setup.archive.assertLogAt(19, "BACKUP:space/retry-key");
      setup.archive.assertLogAt(20, "BACKUP-EXEC:space/retry-key");

      setup.data.assertLogAt(16, "INIT:space/cant-bind:1->{}");
      setup.data.assertLogAt(17, "INIT:space/retry-key:1->{\"x\":1,\"y\":4}");
      setup.data.assertLogAt(18, "LOAD:space/retry-key");
      setup.data.assertLogAt(19, "LOAD:space/retry-key");
      setup.data.assertLogAt(20, "DELETE:space/retry-key");

      {
        SimpleVoidCallback cb_Delete = new SimpleVoidCallback();
        setup.managed.delete(KEY_CANT_DELETE, cb_Delete);
        cb_Delete.assertFailure(-123456);
      }

      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY1, cb_Get);
        restoreAgain.run();
        setup.archive.driveRestore();
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":4,\"y\":4}", cb_Get.value);
      }

      setup.archive.assertLogAt(21, "RESTORE-INIT:space/123");

      {
        SimpleVoidCallback cb_Delete = new SimpleVoidCallback();
        setup.managed.delete(KEY1, cb_Delete);
        cb_Delete.assertSuccess();
      }

      Assert.assertTrue(setup.delete.deleted.contains(KEY1));

      {
        SimpleVoidCallback cb_Delete = new SimpleVoidCallback();
        setup.managed.delete(KEY1, cb_Delete);
        cb_Delete.assertSuccess();
      }

      {
        SimpleVoidCallback cb_Delete = new SimpleVoidCallback();
        setup.managed.delete(KEY_CANT_DELETE_LOCAL, cb_Delete);
        cb_Delete.assertFailure(-42);
      }

      {
        SimpleVoidCallback cb_Init = new SimpleVoidCallback();
        setup.data.initialize(KEY2, UPDATE_1, cb_Init);
        cb_Init.assertSuccess();
        setup.finder.bindLocal(KEY2);
      }

      {
        SimpleDataCallback cb_Get = new SimpleDataCallback();
        setup.managed.get(KEY2, cb_Get);
        cb_Get.assertSuccess();
        Assert.assertEquals("{\"x\":1,\"y\":4}", cb_Get.value);
        backupLoadedContent.run();
        setup.archive.driveBackup();
        backupFinished.run();
      }

      setup.archive.assertLogAt(22, "RESTORE-EXEC:space/123");
      setup.archive.assertLogAt(23, "CLEAN:space/123");
      setup.archive.assertLogAt(24, "BACKUP:space/456");
      setup.archive.assertLogAt(25, "BACKUP-EXEC:space/456");
    }
  }
}
