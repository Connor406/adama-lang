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

import org.adamalang.ErrorCodes;
import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.contracts.*;
import org.adamalang.runtime.contracts.Key;

import java.util.ArrayList;

/** a single connection for a document; given the number of outstanding events that can influence the connection; this is s sizeable state machine. */
public class Connection {
  // these can be put under a base
  private final ConnectionBase base;
  // these are critical to the request (i.e they are the request)
  private final String agent;
  private final String authority;
  private final Key key;
  private final SimpleEvents events;

  // the state machine registers itself with the routing table which will tell it where to go
  Runnable unsubscribeFromRouting;
  boolean routingAlive;

  // the state machine is complicated because of the two stages.
  // first, we have to find a client
  // second, then using that client we have to connect
  private Label state;

  // the target provided by the routing table
  private String target;
  // the client found via the finder
  private InstanceClient foundClient;
  // the buffer of actions to execute once we have a remote
  private ArrayList<QueueAction<Remote>> buffer;
  // the remote to talk to the remote peer's connection
  private Remote foundRemote;
  // how long to wait on a failure to find an instance
  private int backoffFindInstance;
  // how long to wait on a failure to connect to a remote peer
  private int backoffConnectPeer;

  public Connection(
      ConnectionBase base,
      String agent,
      String authority,
      String space,
      String key,
      SimpleEvents events) {
    this.base = base;
    this.agent = agent;
    this.authority = authority;
    this.key = new Key(space, key);
    this.events = events;
    this.routingAlive = false;
    this.unsubscribeFromRouting = null;
    this.state = Label.NotConnected;
    this.target = null;
    this.foundClient = null;
    this.buffer = new ArrayList<>();
    this.foundRemote = null;
    this.backoffFindInstance = 1;
    this.backoffConnectPeer = 1;
  }

  /** api: open the connection */
  public void open() {
    base.executor.execute(
        () -> {
          if (!routingAlive) {
            routingAlive = true;
            events.connected();
            base.engine.subscribe(
                key,
                (newTarget) -> {
                  base.executor.execute(
                      () -> {
                        if (newTarget == null) {
                          if (target != null) {
                            target = null;
                            handle_onKillRoutingTarget();
                          }
                        } else {
                          if (!newTarget.equals(target)) {
                            target = newTarget;
                            handle_onNewRoutingTarget();
                          }
                        }
                      });
                },
                (cancel) -> {
                  base.executor.execute(
                      () -> {
                        if (routingAlive) {
                          unsubscribeFromRouting = cancel;
                        } else {
                          cancel.run();
                        }
                    });
                });
          }
        });
  }

  /** send the remote peer a message */
  public void send(String channel, String marker, String message, SeqCallback callback) {
    base.executor.execute(
        () -> {
          bufferOrExecute(
              new QueueAction<>(ErrorCodes.API_SEND_TIMEOUT, ErrorCodes.API_SEND_REJECTED) {
                @Override
                protected void executeNow(Remote remote) {
                  remote.send(channel, marker, message, callback);
                }

                @Override
                protected void failure(int code) {
                  callback.error(code);
                }
              });
        });
  }

  public void canAttach(AskAttachmentCallback callback) {
    base.executor.execute(() -> {
      bufferOrExecute(new QueueAction<Remote>(ErrorCodes.API_CAN_ATTACH_TIMEOUT, ErrorCodes.API_CAN_ATTACH_REJECTED) {
        @Override
        protected void executeNow(Remote item) {
          item.canAttach(callback);
        }

        @Override
        protected void failure(int code) {
          callback.error(code);
        }
      });
    });
  }

  public void attach(String id, String name, String contentType, long size, String md5, String sha384, SeqCallback callback) {
    base.executor.execute(() -> {
      bufferOrExecute(new QueueAction<Remote>(ErrorCodes.API_CAN_ATTACH_TIMEOUT, ErrorCodes.API_CAN_ATTACH_REJECTED) {
        @Override
        protected void executeNow(Remote item) {
          item.attach(id, name, contentType, size, md5, sha384, callback);
        }

        @Override
        protected void failure(int code) {
          callback.error(code);
        }
      });
    });
  }

  public void close() {
    base.executor.execute(
        () -> {
          if (routingAlive) {
            events.disconnected();
            switch (state) {
              case Connected:
                state = Label.WaitingForDisconnect;
                foundRemote.disconnect();
                break;
              default:
                state = Label.NotConnected;
            }

            // this will trigger a target change to null which will take care of a great deal of
            // things
            if (unsubscribeFromRouting != null) {
              unsubscribeFromRouting.run();
              unsubscribeFromRouting = null;
            }
            routingAlive = false;
          }
        });
  }

  private void bufferOrExecute(QueueAction<Remote> action) {
    if (state == Label.Connected) {
      action.execute(foundRemote);
    } else {
      if (buffer == null) {
        buffer = new ArrayList<>();
      }
      buffer.add(action);
    }
  }

  private void handle_onFoundRemote() {
    backoffConnectPeer = 1;
    switch (state) {
      case FoundClientConnectingWait:
        state = Label.Connected;
        return;
      case FoundClientConnectingStop:
      case FoundClientConnectingTryNewTarget:
        foundRemote.disconnect();
        return;
    }
  }

  private void handle_onError(int code) {
    if (routingAlive) {
      routingAlive = false;
      if (unsubscribeFromRouting != null) {
        unsubscribeFromRouting.run();
      }
    }
    // TODO: what other clean up is there
    state = Label.Failed;
    events.error(code);
  }

  private void handle_onDisconnected() {
    switch (state) {
      case FoundClientConnectingWait:
      case FoundClientConnectingTryNewTarget:
      case Connected:
        if (backoffConnectPeer < 2000) {
          base.executor.schedule(
              () -> {
                if (state == Label.Connected) {
                  fireFindClient();
                }
              },
              backoffConnectPeer);
        } else {
          handle_onError(ErrorCodes.STATE_MACHINE_UNABLE_TO_RECONNECT);
        }
        return;
      case FoundClientConnectingStop:
      case WaitingForDisconnect:
        state = Label.NotConnected;
        return;
    }
  }

  private void handle_onFoundClient() {
    backoffFindInstance = 0;
    switch (state) {
      case FindingClientWait:
        state = Label.FoundClientConnectingWait;
        fireConnectRemote();
        return;
      case FindingClientCancelStop:
        state = Label.FindingClientCancelTryNewTarget;
        return;
      case FindingClientCancelTryNewTarget:
        fireFindClient();
      default:
        // nothing to do
    }
  }

  private void handle_onFailedFindingClient() {
    switch (state) {
      case FindingClientWait:
      case FindingClientCancelTryNewTarget:
        state = Label.FindingClientWait;
        if (backoffFindInstance < 1000) {
          base.executor.schedule(
              () -> {
                fireFindClient();
              },
              backoffFindInstance);
          backoffFindInstance =
              (int) (backoffFindInstance + Math.random() * backoffFindInstance + 1);
        } else {
          handle_onError(ErrorCodes.STATE_MACHINE_TOO_MANY_FAILURES);
        }
        return;
      case FindingClientCancelStop:
        state = Label.NotConnected;
        return;
      default:
        // INVALID
    }
  }

  private void fireConnectRemote() {
    foundClient.connect(
        agent,
        authority,
        key.space,
        key.key,
        new Events() {
          @Override
          public void connected(Remote remote) {
            base.executor.execute(
                () -> {
                  foundRemote = remote;
                  handle_onFoundRemote();
                });
          }

          @Override
          public void delta(String data) {
            events.delta(data);
          }

          @Override
          public void error(int code) {
            base.executor.execute(
                () -> {
                  handle_onError(code);
                });
          }

          @Override
          public void disconnected() {
            base.executor.execute(
                () -> {
                  handle_onDisconnected();
                });
          }
        });
  }

  private void fireFindClient() {
    state = Label.FindingClientWait;
    base.mesh.find(
        target,
        new QueueAction<>(ErrorCodes.INSTANCE_FINDER_TIMEOUT, ErrorCodes.INSTANCE_FINDER_REJECTED) {
          @Override
          protected void executeNow(InstanceClient client) {
            base.executor.execute(
                () -> {
                  foundClient = client;
                  handle_onFoundClient();
                });
          }

          @Override
          protected void failure(int code) {
            base.executor.execute(
                () -> {
                  handle_onFailedFindingClient();
                });
          }
        });
  }

  private void handle_onKillRoutingTarget() {
    switch (state) {
      case NotConnected:
      case FindingClientCancelStop:
      case FoundClientConnectingStop:
        return; // NO-OP
      case FindingClientWait:
      case FindingClientCancelTryNewTarget:
        state = Label.FindingClientCancelStop;
        return;
      case FoundClientConnectingWait:
      case FoundClientConnectingTryNewTarget:
        state = Label.FoundClientConnectingStop;
        return;
      case Connected:
        // TODO: disconnect trigger disconnect cascade
        state = Label.WaitingForDisconnect;
        return;
      case WaitingForDisconnect:
        // NO-OP
        return;
    }
  }

  private void handle_onNewRoutingTarget() {
    switch (state) {
      case NotConnected:
        fireFindClient();
        return;
      case FindingClientWait:
      case FindingClientCancelTryNewTarget:
      case FindingClientCancelStop:
        state = Label.FindingClientCancelTryNewTarget;
        return;
      case FoundClientConnectingWait:
      case FoundClientConnectingStop:
        state = Label.FoundClientConnectingTryNewTarget;
        return;
      case FoundClientConnectingTryNewTarget:
        return; // NO-OP, just a change in the target parameter
      case Connected:
        // this will retry a retry
        foundRemote.disconnect();
        return;
      case WaitingForDisconnect:
        // just ignore it
        return;
    }
  }
}
