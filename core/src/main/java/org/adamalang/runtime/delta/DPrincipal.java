/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.contracts.DeltaNode;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtPrincipal;

/** a client that will respect privacy and sends state to client only on changes */
public class DPrincipal implements DeltaNode {
  private NtPrincipal prior;

  public DPrincipal() {
    prior = null;
  }

  /** the client is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (prior != null) {
      writer.writeNull();
      prior = null;
    }
  }

  @Override
  public void clear() {
    prior = null;
  }

  /** memory usage */
  @Override
  public long __memory() {
    return (prior != null ? prior.memory() : 0) + 32;
  }

  /** the client is visible, so show changes */
  public void show(final NtPrincipal value, final PrivateLazyDeltaWriter writer) {
    if (!value.equals(prior)) {
      final var obj = writer.planObject();
      obj.planField("@t").writeInt(1);
      obj.planField("agent").writeFastString(value.agent);
      obj.planField("authority").writeFastString(value.authority);
      obj.end();
    }
    prior = value;
  }
}
