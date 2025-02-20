/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.lsp;

import org.junit.Test;

import javax.net.SocketFactory;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LanguageServerTests {
  public Socket connect() throws Exception {
    return connect(5, 50);
  }

  public Socket connect(int attemptsLeft, int backoff) throws Exception {
    try {
      Socket socket = SocketFactory.getDefault().createSocket();
      socket.connect(new InetSocketAddress("localhost", 2423));
      return socket;
    } catch (Exception ex) {
      Thread.sleep(backoff);
      if (attemptsLeft > 0) {
        return connect(attemptsLeft - 1, backoff * 2);
      }
      throw ex;
    }
  }

  @Test
  public void dumpCloseCoverage() {
    LanguageServer.forceClose(null);
  }

  @Test
  public void testCrash() throws Exception {
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch stopped = new CountDownLatch(1);
    Thread t =
        new Thread(
            () -> {
              try {
                started.countDown();
                LanguageServer.singleThread(2423);
              } catch (Exception ex) {
                ex.printStackTrace();
              }
              stopped.countDown();
            });
    t.start();
    started.await(
        1000,
        TimeUnit.MILLISECONDS); // THERE IS A MINOR race condition when server actually is available

    try {
      Socket socket = connect();
      OutputStream output = socket.getOutputStream();
      output.write(AdamaLanguageServerProtocol.encode(JsonHelp.createObjectNode()));
      output.flush();
      stopped.await(1000, TimeUnit.MILLISECONDS);
    } finally {
      t.interrupt();
    }
  }

  @Test
  public void testComeAndGo() throws Exception {
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch stopped = new CountDownLatch(1);
    Thread t =
        new Thread(
            () -> {
              try {
                started.countDown();
                LanguageServer.singleThread(2423);
              } catch (Exception ex) {
                ex.printStackTrace();
              }
              stopped.countDown();
            });
    t.start();
    started.await(
        1000,
        TimeUnit.MILLISECONDS); // THERE IS A MINOR race condition when server actually is available

    try {
      Socket socket = connect();
      socket.close();
      stopped.await(1000, TimeUnit.MILLISECONDS);
    } finally {
      t.interrupt();
    }
  }
}
