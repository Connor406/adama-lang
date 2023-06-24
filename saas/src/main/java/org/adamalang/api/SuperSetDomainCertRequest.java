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
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/** The super agent will set a domain */
public class SuperSetDomainCertRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String domain;
  public final String certificate;
  public final Long timestamp;

  public SuperSetDomainCertRequest(final String identity, final AuthenticatedUser who, final String domain, final String certificate, final Long timestamp) {
    this.identity = identity;
    this.who = who;
    this.domain = domain;
    this.certificate = certificate;
    this.timestamp = timestamp;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<SuperSetDomainCertRequest> callback) {
    try {
      final BulkLatch<SuperSetDomainCertRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String domain = request.getString("domain", true, 488444);
      final String certificate = request.getString("certificate", false, 0);
      final Long timestamp = request.getLong("timestamp", true, 439292);
      _latch.with(() -> new SuperSetDomainCertRequest(identity, who.get(), domain, certificate, timestamp));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("supersetdomaincert-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("timestamp", timestamp);
  }
}
