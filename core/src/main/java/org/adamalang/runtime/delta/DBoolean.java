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

/** a boolean that will respect privacy and sends state to client only on changes */
public class DBoolean implements DeltaNode {
  private Boolean prior;

  public DBoolean() {
    prior = null;
  }

  /** the boolean is no longer visible (was made private) */
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
    return 40;
  }

  /** the boolean is visible, so show changes */
  public void show(final boolean value, final PrivateLazyDeltaWriter writer) {
    if (prior == null || value != prior.booleanValue()) {
      writer.writeBool(value);
    }
    prior = value;
  }
}
