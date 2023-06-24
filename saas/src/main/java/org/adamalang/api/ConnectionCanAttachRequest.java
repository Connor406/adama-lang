/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.web.io.*;

/** Ask whether the connection can have attachments attached. */
public class ConnectionCanAttachRequest {
  public final Long connection;

  public ConnectionCanAttachRequest(final Long connection) {
    this.connection = connection;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<ConnectionCanAttachRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 405505);
      nexus.executor.execute(new NamedRunnable("connectioncanattach-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new ConnectionCanAttachRequest(connection));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("connectioncanattach-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
  }
}
