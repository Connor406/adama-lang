/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.server;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.ScheduledFuture;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.common.metrics.StreamMonitor;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.CoreStream;
import org.adamalang.runtime.sys.metering.MeterReading;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Handler implements ByteStream, ClientCodec.HandlerServer, Streamback {
  private static final ServerMessage.CreateResponse SHARED_CREATE_RESPONSE_EMPTY = new ServerMessage.CreateResponse();
  private final ServerNexus nexus;
  private final ByteStream upstream;
  private final AtomicBoolean alive;
  private CoreStream stream;
  private ScheduledFuture<?> futureHeat;
  private StreamMonitor.StreamMonitorInstance monitorStreamback;

  public Handler(ServerNexus nexus, ByteStream upstream) {
    this.nexus = nexus;
    this.upstream = upstream;
    this.futureHeat = null;
    this.alive = new AtomicBoolean(true);
    nexus.metrics.server_handlers_active.up();
    this.monitorStreamback = null;
  }

  @Override
  public void request(int bytes) {
    // proxy to the appropriate thing; if stream, then send to the core stream
    if (stream != null) {
      // TODO: proxy flow control to the stream
    }
  }

  // IGNORE
  @Override
  public ByteBuf create(int bestGuessForSize) {
    return null;
  }

  @Override
  public void next(ByteBuf buf) {
    ClientCodec.route(buf, this);
  }

  @Override
  public void completed() {
    nexus.metrics.server_handlers_active.down();
    alive.set(false);
    if (stream != null) {
      stream.disconnect();
      stream = null;
    }
    if (upstream != null) {
      upstream.completed();
    }
    if (futureHeat != null) {
      futureHeat.cancel(false);
      futureHeat = null;
    }
  }

  @Override
  public void error(int errorCode) {
    nexus.metrics.server_channel_error.run();
    completed();
  }

  @Override
  public void handle(ClientMessage.RequestInventoryHeartbeat payload) {
    nexus.meteringPubSub.subscribe((bills) -> {
      if (alive.get()) {
        ArrayList<String> spaces = new ArrayList<>();
        for (MeterReading meterReading : bills) {
          spaces.add(meterReading.space);
        }
        ServerMessage.InventoryHeartbeat inventoryHeartbeat = new ServerMessage.InventoryHeartbeat();
        inventoryHeartbeat.spaces = spaces.toArray(new String[spaces.size()]);
        ByteBuf buf = upstream.create(24);
        ServerCodec.write(buf, inventoryHeartbeat);
        upstream.next(buf);
      }
      return alive.get();
    });
  }

  @Override
  public void handle(ClientMessage.RequestHeat payload) {
    futureHeat = nexus.base.workerGroup.scheduleAtFixedRate(() -> {
      ServerMessage.HeatPayload heat = new ServerMessage.HeatPayload();
      heat.cpu = MachineHeat.cpu();
      heat.mem = MachineHeat.memory();
      ByteBuf buf = upstream.create(24);
      ServerCodec.write(buf, heat);
      upstream.next(buf);
    }, 25, 250, TimeUnit.MILLISECONDS);
  }

  @Override
  public void handle(ClientMessage.StreamAttach payload) {
    if (stream != null) {
      stream.attach(new NtAsset(payload.id, payload.filename, payload.contentType, payload.size, payload.md5, payload.sha384), nexus.metrics.server_stream_attach.wrap(new Callback<Integer>() {
        @Override
        public void success(Integer seq) {
          ServerMessage.StreamSeqResponse response = new ServerMessage.StreamSeqResponse();
          response.op = payload.op;
          response.seq = seq;
          ByteBuf buf = upstream.create(8);
          ServerCodec.write(buf, response);
          upstream.next(buf);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ServerMessage.StreamError error = new ServerMessage.StreamError();
          error.op = payload.op;
          error.code = ex.code;
          ByteBuf errorBuf = upstream.create(8);
          ServerCodec.write(errorBuf, error);
          upstream.next(errorBuf);
        }
      }));
    }
  }

  @Override
  public void handle(ClientMessage.StreamAskAttachmentRequest payload) {
    if (stream != null) {
      stream.canAttach(nexus.metrics.server_stream_ask.wrap(new Callback<Boolean>() {
        @Override
        public void success(Boolean value) {
          ServerMessage.StreamAskAttachmentResponse response = new ServerMessage.StreamAskAttachmentResponse();
          response.op = payload.op;
          response.allowed = value;
          ByteBuf buf = upstream.create(8);
          ServerCodec.write(buf, response);
          upstream.next(buf);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ServerMessage.StreamError error = new ServerMessage.StreamError();
          error.op = payload.op;
          error.code = ex.code;
          ByteBuf errorBuf = upstream.create(8);
          ServerCodec.write(errorBuf, error);
          upstream.next(errorBuf);
        }
      }));
    }
  }

  @Override
  public void handle(ClientMessage.StreamDisconnect payload) {
    if (stream != null) {
      nexus.metrics.server_stream_disconnect.run();
      stream.disconnect();
      stream = null;
    }
  }

  @Override
  public void handle(ClientMessage.StreamUpdate payload) {
    if (stream != null) {
      nexus.metrics.server_stream_update.run();
      stream.updateView(new JsonStreamReader(payload.viewerState));
    }
  }

  @Override
  public void handle(ClientMessage.StreamSend payload) {
    if (stream != null) {
      stream.send(payload.channel, payload.marker, payload.message, nexus.metrics.server_stream_send.wrap(new Callback<>() {
        @Override
        public void success(Integer value) {
          ServerMessage.StreamSeqResponse response = new ServerMessage.StreamSeqResponse();
          response.op = payload.op;
          response.seq = value;
          ByteBuf buf = upstream.create(8);
          ServerCodec.write(buf, response);
          upstream.next(buf);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ServerMessage.StreamError error = new ServerMessage.StreamError();
          error.op = payload.op;
          error.code = ex.code;
          ByteBuf errorBuf = upstream.create(8);
          ServerCodec.write(errorBuf, error);
          upstream.next(errorBuf);
        }
      }));
    }
  }

  @Override
  public void handle(ClientMessage.StreamConnect payload) {
    monitorStreamback = nexus.metrics.server_stream.start();
    nexus.service.connect(new NtClient(payload.agent, payload.authority), new Key(payload.space, payload.key), payload.viewerState, this);
  }

  @Override
  public void handle(ClientMessage.MeteringDeleteBatch payload) {
    try {
      nexus.metrics.server_metering_delete_batch.run();
      nexus.meteringBatchMaker.deleteBatch(payload.id);
      ByteBuf toWrite = upstream.create(4);
      ServerCodec.write(toWrite, new ServerMessage.MeteringBatchRemoved());
      upstream.next(toWrite);
    } catch (Exception ex) {
      upstream.error(-1);
    }
  }

  @Override
  public void handle(ClientMessage.MeteringBegin payload) {
    try {
      String id = nexus.meteringBatchMaker.getNextAvailableBatchId();
      nexus.metrics.server_metering_begin.run();
      if (id != null) {
        String batch = nexus.meteringBatchMaker.getBatch(id);
        ServerMessage.MeteringBatchFound found = new ServerMessage.MeteringBatchFound();
        found.id = id;
        found.batch = batch;
        ByteBuf toWrite = upstream.create(id.length() + batch.length() + 8);
        ServerCodec.write(toWrite, found);
        upstream.next(toWrite);
      } else {
        upstream.completed();
      }
    } catch (Exception ex) {
      upstream.error(-1);
    }
  }

  @Override
  public void handle(ClientMessage.ScanDeployment payload) {
    try {
      nexus.metrics.server_scan_deployment.run();
      nexus.scanForDeployments.accept(payload.space);
      ByteBuf buf = upstream.create(4);
      ServerCodec.write(buf, new ServerMessage.ScanDeploymentResponse());
      upstream.next(buf);
      upstream.completed();
    } catch (Exception ex) {
      upstream.error(ErrorCodes.GRPC_HANDLER_SCAN_EXCEPTION);
    }
  }

  @Override
  public void handle(ClientMessage.ReflectRequest payload) {
    nexus.service.reflect(new Key(payload.space, payload.key), nexus.metrics.server_reflect.wrap(new Callback<>() {
      @Override
      public void success(String value) {
        ServerMessage.ReflectResponse response = new ServerMessage.ReflectResponse();
        response.schema = value;
        ByteBuf buf = upstream.create(8 + value.length());
        ServerCodec.write(buf, response);
        upstream.next(buf);
        upstream.completed();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    }));
  }

  @Override
  public void handle(ClientMessage.CreateRequest payload) {
    nexus.service.create(new NtClient(payload.agent, payload.authority), new Key(payload.space, payload.key), payload.arg, payload.entropy, nexus.metrics.server_create.wrap(new Callback<Void>() {
      @Override
      public void success(Void value) {
        ByteBuf buf = upstream.create(8);
        ServerCodec.write(buf, SHARED_CREATE_RESPONSE_EMPTY);
        upstream.next(buf);
        upstream.completed();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        upstream.error(ex.code);
      }
    }));
  }

  @Override
  public void handle(ClientMessage.PingRequest payload) {
    ByteBuf buf = upstream.create(8);
    ServerCodec.write(buf, new ServerMessage.PingResponse());
    upstream.next(buf);
    upstream.completed();
  }

  @Override
  public void onSetupComplete(CoreStream stream) {
    this.stream = stream;
  }

  @Override
  public void status(StreamStatus status) {
    ByteBuf buffer = upstream.create(16);
    ServerMessage.StreamStatus statusToUse = new ServerMessage.StreamStatus();
    statusToUse.code = status == StreamStatus.Connected ? 1 : 0;
    ServerCodec.write(buffer, statusToUse);
    upstream.next(buffer);
    if (status == StreamStatus.Disconnected) {
      monitorStreamback.finish();
      upstream.completed();
    }
  }

  @Override
  public void next(String data) {
    monitorStreamback.progress();
    ByteBuf buffer = upstream.create(16 + data.length());
    ServerMessage.StreamData dataToUse = new ServerMessage.StreamData();
    dataToUse.delta = data;
    ServerCodec.write(buffer, dataToUse);
    upstream.next(buffer);
  }

  @Override
  public void failure(ErrorCodeException exception) {
    monitorStreamback.failure(exception.code);
    upstream.error(exception.code);
    completed();
  }
}
