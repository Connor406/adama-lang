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
import org.adamalang.runtime.text.RxText;
import org.adamalang.runtime.text.SeqString;

/** a document synchronized by deltas using CodeMirror's format */
public class DText implements DeltaNode {
  private boolean initialized;
  private int seq;
  private int gen;

  public DText() {
    initialized = false;
    seq = 0;
    gen = 0;
  }

  /** the string is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (initialized) {
      writer.writeNull();
      initialized = false;
      seq = 0;
      gen = 0;
    }
  }

  @Override
  public void clear() {
    initialized = false;
    seq = 0;
    gen = 0;
  }

  /** memory usage */
  @Override
  public long __memory() {
    return 64;
  }

  public void show(final RxText value, final PrivateLazyDeltaWriter writer) {
    if (value.current().gen != gen) {
      this.initialized = false;
      this.seq = 0;
      this.gen = value.current().gen;
    }
    PrivateLazyDeltaWriter obj = writer.planObject();
    if (initialized) {
      int start = seq;
      String change;
      while ((change = value.current().changes.get(seq)) != null) {
        obj.planField("" + seq).writeString(change);
        seq++;
      }
      while ((change = value.current().uncommitedChanges.get(seq)) != null) {
        obj.planField("" + seq).writeString(change);
        seq++;
      }
      if (start != seq) {
        obj.planField("$s").writeInt(seq);
      }
    } else {
      initialized = true;
      SeqString val = value.current().get();
      obj.planField("$g").writeInt(gen);
      obj.planField("$i").writeString(val.value);
      obj.planField("$s").writeInt(val.seq);
      seq = val.seq;
    }
    obj.end();
  }
}
