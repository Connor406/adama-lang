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
import org.adamalang.runtime.natives.NtAsset;

/** an asset that will respect privacy and sends state to client only on changes */
public class DAsset implements DeltaNode {
  private NtAsset prior;

  public DAsset() {
    prior = null;
  }

  /** the asset is no longer visible (was made private) */
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

  /** the asset is visible, so show changes */
  public void show(final NtAsset value, final PrivateLazyDeltaWriter writer) {
    if (writer.assetIdEncoder != null) {
      if (!value.equals(prior)) {
        final var obj = writer.planObject();
        // note; we don't send the name as that may leak private information from the uploader
        obj.planField("id").writeString(writer.assetIdEncoder.encrypt(value.id));
        obj.planField("size").writeFastString("" + value.size);
        obj.planField("type").writeString(value.contentType);
        obj.planField("md5").writeString(value.md5);
        obj.planField("sha384").writeString(value.sha384);
        obj.end();
      }
    }
    prior = value;
  }
}
