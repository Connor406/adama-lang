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
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxKillable;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtMaybe;

import java.util.Comparator;
import java.util.function.Function;

/** a reactive maybe */
public class RxMaybe<Ty extends RxBase> extends RxBase implements RxParent, RxChild, RxKillable {
  private final Function<RxParent, Ty> maker;
  private Ty priorValue;
  private Ty value;

  public RxMaybe(final RxParent owner, final Function<RxParent, Ty> maker) {
    super(owner);
    this.value = null;
    this.maker = maker;
    this.priorValue = null;
  }

  @Override
  public boolean __isAlive() {
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  public void __link() {
    if (value != null && value instanceof RxRecordBase) {
      ((RxRecordBase<?>) value).__link();
    }
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      if (value != null) {
        if (priorValue == null) {
          value.__commit(name, forwardDelta, new JsonStreamWriter());
          reverseDelta.writeObjectFieldIntro(name);
          reverseDelta.writeNull();
        } else {
          value.__commit(name, forwardDelta, reverseDelta);
        }
      } else { // value is null
        forwardDelta.writeObjectFieldIntro(name);
        forwardDelta.writeNull();
        if (priorValue != null) {
          reverseDelta.writeObjectFieldIntro(name);
          priorValue.__dump(reverseDelta);
        }
        __cancelAllSubscriptions();
      }

      priorValue = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    if (value != null) {
      value.__dump(writer);
    } else {
      writer.writeNull();
    }
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    if (reader.testLackOfNull()) {
      if (value == null) {
        value = maker.apply(this);
        value.__subscribe(this);
      }
      priorValue = value;
      value.__insert(reader);
    } else {
      value = null;
      priorValue = null;
    }
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    if (reader.testLackOfNull()) {
      make().__patch(reader);
    } else {
      delete();
    }
  }

  @Override
  public void __revert() {
    if (__isDirty()) {
      value = priorValue;
      if (value != null) {
        value.__revert();
      } else { // value is null
        __cancelAllSubscriptions();
      }
      __lowerDirtyRevert();
    }
  }

  @Override
  public long __memory() {
    return super.__memory() + 24 + (value != null ? value.__memory() : 0) + (priorValue != null ? priorValue.__memory() : 0);
  }

  public Ty make() {
    if (value == null) {
      value = maker.apply(this);
      value.__subscribe(this);
      value.__raiseDirty();
    }
    return value;
  }

  public void delete() {
    if (value != null) {
      if (value instanceof RxKillable) {
        ((RxKillable) value).__kill();
      }
      value = null;
      __raiseDirty();
    }
  }

  @Override
  public void __kill() {
    if (value instanceof RxKillable) {
      ((RxKillable) value).__kill();
    }
  }

  @Override
  public boolean __raiseInvalid() {
    __invalidateSubscribers();
    return true;
  }

  public int compareValues(final RxMaybe<Ty> other, final Comparator<Ty> test) {
    if (value == null) {
      if (other.value == null) {
        return 0;
      } else {
        return 1;
      }
    } else {
      if (other.value == null) {
        return -1;
      } else {
        return test.compare(value, other.value);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public NtMaybe get() {
    if (value == null) {
      return new NtMaybe();
    } else {
      if (value instanceof CanGetAndSet) {
        return new NtMaybe(((CanGetAndSet) value).get()).withDeleteChain(() -> delete());
      } else {
        return new NtMaybe(value).withDeleteChain(() -> delete());
      }
    }
  }

  public boolean has() {
    return value != null;
  }

  @SuppressWarnings("unchecked")
  public void set(final NtMaybe other) {
    if (other.has()) {
      ((CanGetAndSet) this.make()).set(other.get());
    } else {
      delete();
    }
    __raiseDirty();
  }
}
