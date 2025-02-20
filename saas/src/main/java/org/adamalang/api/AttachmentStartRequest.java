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
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.validators.ValidateKey;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Start an upload for the given document with the given filename and content type. */
public class AttachmentStartRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final String key;
  public final String filename;
  public final String contentType;

  public AttachmentStartRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final String key, final String filename, final String contentType) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.key = key;
    this.filename = filename;
    this.contentType = contentType;
  }

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<AttachmentStartRequest> callback) {
    try {
      final BulkLatch<AttachmentStartRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String key = request.getString("key", true, 466947);
      ValidateKey.validate(key);
      final String filename = request.getString("filename", true, 470028);
      final String contentType = request.getString("content-type", true, 455691);
      _latch.with(() -> new AttachmentStartRequest(identity, who.get(), space, policy.get(), key, filename, contentType));
      nexus.identityService.execute(session, identity, who);
      nexus.spaceService.execute(session, space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("attachmentstart-error") {
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
    _node.put("filename", filename);
    _node.put("content-type", contentType);
  }
}
