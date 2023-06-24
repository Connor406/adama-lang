/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.validators.ValidateChannel;
import org.adamalang.validators.ValidateKey;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Send a message to a document without a connection */
public class MessageDirectSendOnceRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final String key;
  public final String dedupe;
  public final String channel;
  public final JsonNode message;

  public MessageDirectSendOnceRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final String key, final String dedupe, final String channel, final JsonNode message) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.key = key;
    this.dedupe = dedupe;
    this.channel = channel;
    this.message = message;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<MessageDirectSendOnceRequest> callback) {
    try {
      final BulkLatch<MessageDirectSendOnceRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String key = request.getString("key", true, 466947);
      ValidateKey.validate(key);
      final String dedupe = request.getString("dedupe", false, 0);
      final String channel = request.getString("channel", true, 454659);
      ValidateChannel.validate(channel);
      final JsonNode message = request.getJsonNode("message", true, 425987);
      _latch.with(() -> new MessageDirectSendOnceRequest(identity, who.get(), space, policy.get(), key, dedupe, channel, message));
      nexus.identityService.execute(session, identity, who);
      nexus.spaceService.execute(session, space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("messagedirectsendonce-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("space", space);
    org.adamalang.transforms.SpacePolicyLocator.logInto(policy, _node);
    _node.put("key", key);
    _node.put("dedupe", dedupe);
    _node.put("channel", channel);
  }
}
