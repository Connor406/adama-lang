/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types;

public enum TypeBehavior {
  ReadOnlyGetNativeValue(true), // the type is native, and can be used natively
  ReadOnlyNativeValue(true), //
  ReadWriteNative(false), // the value is native, and can only be read from
  ReadWriteWithSetGet(false); // the value is native, and can only be read from

  public final boolean isReadOnly;

  private TypeBehavior(boolean isReadOnly) {
    this.isReadOnly = isReadOnly;
  }
}
