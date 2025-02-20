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
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDate;

/** A single date in the typical gregorian calendar */
public class RxDate extends RxBase implements Comparable<RxDate>, CanGetAndSet<NtDate> {
  private NtDate backup;
  private NtDate value;

  public RxDate(final RxParent parent, final NtDate value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeNtDate(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeNtDate(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeNtDate(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readNtDate();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readNtDate());
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
  public NtDate get() {
    return value;
  }

  @Override
  public void set(final NtDate value) {
    this.value = value;
    __raiseDirty();
  }

  @Override
  public int compareTo(RxDate o) {
    return value.compareTo(o.value);
  }
}
