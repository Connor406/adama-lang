/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client.sm;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.grpc.TestBed;
import org.adamalang.grpc.client.ClientMetrics;
import org.adamalang.grpc.client.InstanceClientFinder;
import org.adamalang.grpc.client.routing.MockSpaceTrackingEvents;
import org.adamalang.grpc.client.routing.RoutingEngine;
import org.adamalang.grpc.mocks.LatchedSeqCallback;
import org.adamalang.grpc.mocks.MockSimpleEvents;
import org.adamalang.grpc.mocks.SlowSingleThreadedExecutorFactory;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ConnectionFailOverTests {
  @Test
  public void validateFailsOverToNewHost() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SimpleExecutor fauxExector = SimpleExecutor.create("routing");
    SlowSingleThreadedExecutorFactory finderExecutor =
        new SlowSingleThreadedExecutorFactory("finder");
    SlowSingleThreadedExecutorFactory directExector =
        new SlowSingleThreadedExecutorFactory("direct");
    SlowSingleThreadedExecutorFactory connectionExecutor =
        new SlowSingleThreadedExecutorFactory("connection");
    ExceptionLogger logger = (t, c) -> {};
    try {
      for (int k = 0; k < servers.length; k++) {
        servers[k] =
            new TestBed(
                17005 + k,
                "@can_create(who) { return true; } @connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }");

        CountDownLatch latchMade = new CountDownLatch(1);
        servers[k].coreService.create(
            NtClient.NO_ONE,
            new Key("space", "key"),
            "{}",
            null,
            new Callback<Void>() {
              @Override
              public void success(Void value) {
                latchMade.countDown();
              }

              @Override
              public void failure(ErrorCodeException ex) {}
            });
        Assert.assertTrue(latchMade.await(1000, TimeUnit.MILLISECONDS));
        servers[k].startServer();
      }
      // The faux engine absorbs the workload from the finder
      RoutingEngine fauxEngine =
          new RoutingEngine(metrics, fauxExector, new MockSpaceTrackingEvents(), 50, 25);
      // we use the direct engine to control the connection... directly
      RoutingEngine engineDirect =
          new RoutingEngine(metrics, directExector, new MockSpaceTrackingEvents(), 50, 25);
      InstanceClientFinder finder =
          new InstanceClientFinder(metrics, null, servers[0].identity, finderExecutor, 2, fauxEngine, logger);
      boolean finished = false;
      try {
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable eventsConnected = events.latchAt(1);
        Runnable eventsProducedData = events.latchAt(2);
        Runnable eventsGotUpdate = events.latchAt(3);
        Runnable eventsGotRollback = events.latchAt(4);

        Runnable ranStart = connectionExecutor.latchAtAndDrain(1, 1);
        Runnable subscribed = directExector.latchAtAndDrain(1, 1);
        Runnable gotNullTargetAndCancel = connectionExecutor.latchAtAndDrain(3, 2);
        Runnable gotNewTarget = directExector.latchAtAndDrain(2, 2);
        Runnable newTargetBroadcastQueued = directExector.latchAtAndDrain(4, 1);
        Runnable gotFirstTarget = connectionExecutor.latchAtAndDrain(4, 1);
        Runnable gotFindRequest = finderExecutor.latchAtAndDrain(1, 1);
        Runnable clientSetup = finderExecutor.latchAtAndDrain(2, 1);
        Runnable executeFound = finderExecutor.latchAtAndDrain(3, 1);
        Runnable clientConnected = finderExecutor.latchAtAndDrain(4, 1);
        Runnable clientFound = connectionExecutor.latchAtAndDrain(5, 1);
        Runnable clientReconnecting = finderExecutor.latchAtAndDrain(5, 1);
        Runnable clientGotEstablished = finderExecutor.latchAtAndDrain(6, 1);
        Runnable clientMade = finderExecutor.latchAtAndDrain(7, 1);
        Runnable connectionMade = connectionExecutor.latchAtAndDrain(6, 1);
        Runnable executeSend = connectionExecutor.latchAtAndDrain(7, 1);
        Runnable forwardSend = finderExecutor.latchAtAndDrain(8, 1);
        Runnable clientDataForward = finderExecutor.latchAtAndDrain(10, 2);
        Runnable gotDisconnect = finderExecutor.latchAtAndDrain(11, 1);
        Runnable connectionBroke = connectionExecutor.latchAtAndDrain(8, 1);
        Runnable connectionRetry = connectionExecutor.latchAtAndDrain(9, 1);
        Runnable finderRetry = finderExecutor.latchAtAndDrain(14, 3);
        Runnable finderFailedAgain = finderExecutor.latchAtAndDrain(15, 1);
        Runnable executorRemoves = directExector.latchAtAndDrain(4, 1);
        Runnable finderSyncs = finderExecutor.latchAtAndDrain(18, 3);
        Runnable finderCleansUp = finderExecutor.latchAtAndDrain(21, 3);
        Runnable broadcastNewTarget = directExector.latchAtAndDrain(5, 1);
        Runnable completeClients = finderExecutor.latchAtAndDrain(23, -1);
        Runnable connectionFoundTarget = connectionExecutor.latchAtAndDrain(11, 2);
        Runnable newConnectionSignal = connectionExecutor.latchAtAndDrain(12, 1);
        Runnable foundConnection = connectionExecutor.latchAtAndDrain(13, 1);
        Runnable connectionComplete = connectionExecutor.latchAtAndDrain(14, 1);

        ConnectionBase base = new ConnectionBase(metrics, engineDirect, finder, connectionExecutor);
        Connection connection = new Connection(base, "who", "dev", "space", "key", events);
        Assert.assertEquals("state=NotConnected", connection.toString());
        connection.open();
        ranStart.run();
        eventsConnected.run();
        events.assertWrite(0, "CONNECTED");
        subscribed.run();
        gotNullTargetAndCancel.run();
        engineDirect.integrate("127.0.0.1:17005", Collections.singleton("space"));
        engineDirect.integrate("127.0.0.1:17006", Collections.singleton("space"));
        gotNewTarget.run();
        newTargetBroadcastQueued.run();
        gotFirstTarget.run();
        gotFindRequest.run();
        clientSetup.run();
        executeFound.run();
        clientConnected.run();
        clientFound.run();
        clientReconnecting.run();
        Assert.assertEquals("state=FoundClientConnectingWait", connection.toString());
        clientGotEstablished.run();
        clientMade.run();
        eventsProducedData.run();
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        Assert.assertEquals("state=FoundClientConnectingWait", connection.toString());
        connectionMade.run();
        Assert.assertEquals("state=Connected", connection.toString());
        LatchedSeqCallback cb1 = new LatchedSeqCallback();
        connection.send("foo", null, "{\"z\":100}", cb1);
        executeSend.run();
        forwardSend.run();
        clientDataForward.run();
        events.assertWrite(2, "DELTA:{\"data\":{\"x\":223},\"seq\":6}");
        eventsGotUpdate.run();
        cb1.assertSuccess(6);
        servers[0].stopServer();
        gotDisconnect.run();
        Assert.assertEquals("state=Connected", connection.toString());
        connectionBroke.run();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        connectionRetry.run();
        finderRetry.run();
        finderFailedAgain.run();
        engineDirect.remove("127.0.0.1:17005");
        finder.sync(new TreeSet<>(Collections.singleton("127.0.0.1:17006")));
        executorRemoves.run();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        finderSyncs.run();
        finderCleansUp.run();
        broadcastNewTarget.run();
        completeClients.run();
        finderExecutor.goFast();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        connectionFoundTarget.run();
        Assert.assertEquals("state=FindingClientCancelTryNewTarget", connection.toString());
        newConnectionSignal.run();
        Assert.assertEquals("state=FindingClientWait", connection.toString());
        foundConnection.run();
        eventsGotRollback.run();
        connectionComplete.run();
        events.assertWrite(3, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        Assert.assertEquals("state=Connected", connection.toString());
        finished = true;
      } finally {
        System.err.println("FIN\n");
        if (!finished) {
          System.err.println("Not finished\n");
          Thread.sleep(500);
          directExector.survey();
          finderExecutor.survey();
          connectionExecutor.survey();
          System.err.println("survey revealed issued on a queue\n");
        }
        finder.shutdown();
      }
    } finally {
      for (int k = 0; k < servers.length; k++) {
        if (servers[k] != null) {
          servers[k].close();
        }
      }
      fauxExector.shutdown();
    }
  }
}
