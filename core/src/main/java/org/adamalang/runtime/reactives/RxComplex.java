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
import org.adamalang.runtime.natives.NtComplex;

/** a reactive complex number */
public class RxComplex extends RxBase implements CanGetAndSet<NtComplex> {
  private NtComplex backup;
  private NtComplex value;

  public RxComplex(final RxParent parent, final NtComplex value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeNtComplex(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeNtComplex(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeNtComplex(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readNtComplex();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readNtComplex());
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
  public NtComplex get() {
    return value;
  }

  @Override
  public void set(final NtComplex value) {
    this.value = value;
    __raiseDirty();
  }

  public void set(final double value) {
    this.value = new NtComplex(value, 0.0);
    __raiseDirty();
  }

  public void opAddTo(int x) {
    this.value = new NtComplex(value.real + x, value.imaginary);
    __raiseDirty();
  }

  public void opAddTo(long x) {
    this.value = new NtComplex(value.real + x, value.imaginary);
    __raiseDirty();
  }

  public void opAddTo(double x) {
    this.value = new NtComplex(value.real + x, value.imaginary);
    __raiseDirty();
  }

  public void opAddTo(NtComplex x) {
    this.value = new NtComplex(value.real + x.real, value.imaginary + x.imaginary);
    __raiseDirty();
  }

  public void opMultBy(double x) {
    this.value = new NtComplex(value.real * x, value.imaginary * x);
    __raiseDirty();
  }

  public void opMultBy(NtComplex x) {
    this.value = new NtComplex(value.real * x.real - value.imaginary * x.imaginary, value.imaginary * x.real + value.real * x.imaginary);
    __raiseDirty();
  }

  public void opSubFrom(NtComplex x) {
    this.value = new NtComplex(value.real - x.real, value.imaginary - x.imaginary);
    __raiseDirty();
  }
}
