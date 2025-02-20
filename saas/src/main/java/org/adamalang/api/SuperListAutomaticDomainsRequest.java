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
import org.adamalang.frontend.Session;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/** The super agent downloads all domains that are ready to be renewed. */
public class SuperListAutomaticDomainsRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final Long timestamp;

  public SuperListAutomaticDomainsRequest(final String identity, final AuthenticatedUser who, final Long timestamp) {
    this.identity = identity;
    this.who = who;
    this.timestamp = timestamp;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<SuperListAutomaticDomainsRequest> callback) {
    try {
      final BulkLatch<SuperListAutomaticDomainsRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final Long timestamp = request.getLong("timestamp", true, 439292);
      _latch.with(() -> new SuperListAutomaticDomainsRequest(identity, who.get(), timestamp));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("superlistautomaticdomains-error") {
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
