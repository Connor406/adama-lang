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

/** a reactive 32-bit integer (int) */
public class RxInt32 extends RxBase implements Comparable<RxInt32>, CanGetAndSet<Integer>, Indexable {
  protected int backup;
  protected int value;

  public RxInt32(final RxParent parent, final int value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeInteger(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeInteger(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeInteger(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readInteger();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readInteger());
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
    return super.__memory() + 8;
  }

  public int bumpDownPost() {
    final var result = value--;
    __raiseDirty();
    return result;
  }

  public int bumpDownPre() {
    final var result = --value;
    __raiseDirty();
    return result;
  }

  public int bumpUpPost() {
    final var result = value++;
    __raiseDirty();
    return result;
  }

  // these make ZERO sense
  public int bumpUpPre() {
    final var result = ++value;
    __raiseDirty();
    return result;
  }

  @Override
  public int compareTo(final RxInt32 other) {
    return Integer.compare(value, other.value);
  }

  public void forceSet(final int id) {
    backup = id;
    value = id;
  }

  @Override
  public Integer get() {
    return value;
  }

  @Override
  public void set(final Integer value) {
    if (this.value != value) {
      this.value = value;
      __raiseDirty();
    }
  }

  @Override
  public int getIndexValue() {
    return value;
  }

  public int opAddTo(final int incoming) {
    value += incoming;
    __raiseDirty();
    return value;
  }

  public int opMultBy(final int x) {
    value *= x;
    __raiseDirty();
    return value;
  }
}
