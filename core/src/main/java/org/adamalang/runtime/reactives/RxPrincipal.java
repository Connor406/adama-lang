/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.Indexable;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;

/** a reactive client */
public class RxPrincipal extends RxBase implements Comparable<RxPrincipal>, CanGetAndSet<NtPrincipal>, Indexable {
  private NtPrincipal backup;
  private NtPrincipal value;

  public RxPrincipal(final RxParent parent, final NtPrincipal value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeNtPrincipal(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeNtPrincipal(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeNtPrincipal(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readNtPrincipal();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readNtPrincipal());
  }

  @Override
  public void __revert() {
    if (__isDirty()) {
      value = backup;
      __lowerDirtyRevert();
    }
  }

  @Override
  public long __memory() {
    return super.__memory() + backup.memory() + value.memory() + 16;
  }

  @Override
  public int compareTo(final RxPrincipal other) {
    return value.compareTo(other.value);
  }

  @Override
  public NtPrincipal get() {
    return value;
  }

  @Override
  public void set(final NtPrincipal value) {
    if (!this.value.equals(value)) {
      this.value = value;
      __raiseDirty();
    }
  }

  @Override
  public int getIndexValue() {
    return value.hashCode();
  }
}
