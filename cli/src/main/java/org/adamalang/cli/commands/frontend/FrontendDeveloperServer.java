/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands.frontend;

import org.adamalang.cli.Config;
import org.adamalang.common.Callback;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.web.UriMatcher;
import org.adamalang.rxhtml.Feedback;
import org.adamalang.rxhtml.RxHtmlToAdama;
import org.adamalang.rxhtml.RxHtmlTool;
import org.adamalang.translator.tree.definitions.web.Uri;
import org.adamalang.web.contracts.AssetDownloader;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.service.ContentType;
import org.adamalang.web.service.ServiceRunnable;
import org.adamalang.web.service.WebConfig;
import org.adamalang.web.service.WebMetrics;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FrontendDeveloperServer {
  /** sync the watcher with directories as they are mutated; cache them */
  private static void syncWatcher(WatchService watcher, HashMap<String, WatchKey> cache, String at) throws Exception {
    File rootAt = new File(at);
    if (!cache.containsKey(at)) {
      WatchKey rootWK = rootAt.toPath().register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
      cache.put(at, rootWK);
    }
    for (File child : rootAt.listFiles()) {
      if (child.isDirectory()) {
        syncWatcher(watcher, cache, at + "/" + child.getName());
      }
    }
  }

  /** rebuild the files that are .rx.html */
  private static void scanRxHtml(File root, ArrayList<File> files) {
    for (File file : root.listFiles()) {
      if (file.isDirectory()) {
        scanRxHtml(file, files);
      }
      if (file.getName().endsWith(".rx.html")) {
        files.add(file);
      }
    }
  }

  private static ArrayList<File> rxhtml(File root) {
    ArrayList<File> files = new ArrayList<>();
    scanRxHtml(root, files);
    return files;
  }

  public static void go(Config config, String[] args) throws Exception {
    AtomicBoolean alive = new AtomicBoolean(true);
    HashMap<String, WatchKey> cache = new HashMap<>();
    Thread scanner = null;
    AtomicReference<ArrayList<UriMatcher>> templateMatchers = new AtomicReference<>(new ArrayList<>());
    AtomicReference<String> templateForest = new AtomicReference<>(RxHtmlTool.convertFilesToTemplateForest(rxhtml(new File(".")), templateMatchers.get(), Feedback.NoOp));
    Files.writeString(new File("./template.js").toPath(), templateForest.get());

    try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
      syncWatcher(watcher, cache, ".");

      // Scan for cahnges
      scanner = new Thread(new Runnable() {
        @Override
        public void run() {
          while (alive.get()) {
            try {
              boolean rescanRx = false;
              boolean rescanDir = false;
              WatchKey wk = watcher.take();
              for (WatchEvent<?> event : wk.pollEvents()) {
                final Path changed = (Path) event.context();
                String filename = changed.toFile().getName();
                if (changed.toFile().isDirectory()) {
                  rescanDir = true;
                }
                if (filename.contains(".rx.html")) {
                  rescanRx = true;
                }
              }
              if (rescanDir) {
                syncWatcher(watcher, cache, ".");
              }
              if (rescanRx) {
                ArrayList<UriMatcher> matchers = new ArrayList<>();
                templateForest.set(RxHtmlTool.convertFilesToTemplateForest(rxhtml(new File(".")), matchers, Feedback.NoOp));
                templateMatchers.set(matchers);
                Files.writeString(new File("./template.js").toPath(), templateForest.get());
              }
              wk.reset();
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });
      scanner.start();

      WebConfig webConfig = new WebConfig(new ConfigObject(config.get_or_create_child("web")));
      ServiceBase base = new ServiceBase() {
        @Override
        public ServiceConnection establish(ConnectionContext context) {
          // TODO: decide if we support a proxy mode for Adama?
          return null;
        }

        @Override
        public HttpHandler http() {
          return new HttpHandler() {
            @Override
            public void handleOptions(String uri, Callback<Boolean> callback) {
              callback.failure(new ErrorCodeException(0));
            }

            @Override
            public void handleGet(String uriOriginal, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
              String uri = uriOriginal;
              if ("/template.js".endsWith(uri)) {
                callback.success(new HttpResult("text/javascript", templateForest.get().getBytes(), false));
                return;
              }
              // lame version for now, need to build a routable tree with type biases if this ever becomes a mainline
              for (UriMatcher matcher : templateMatchers.get()) {
                boolean result = matcher.matches(uri);
                if (result) {
                  uri = "/200.html";
                  break;
                }
              }
              if (uri.endsWith("/")) {
                uri += "index.html";
              }
              File file = new File(uri.substring(1));
              try {
                if (file.exists()) {
                  byte[] bytes = Files.readAllBytes(file.toPath());
                  callback.success(new HttpResult(ContentType.of(uri), bytes, true));
                } else {
                  callback.failure(new ErrorCodeException(404));
                }
              } catch (Exception ex) {
                callback.failure(new ErrorCodeException(500));
              }
            }

            @Override
            public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
              callback.failure(new ErrorCodeException(0));
            }
          };
        }

        @Override
        public AssetDownloader downloader() {
          // TODO: decide if we support this here or not
          return null;
        }
      };
      ServiceRunnable webServer = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, () -> {
      });
      webServer.run();
    } finally {
      alive.set(false);
      if (scanner != null) {
        scanner.interrupt();
      }
    }
  }
}
