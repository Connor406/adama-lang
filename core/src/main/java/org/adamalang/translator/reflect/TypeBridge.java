/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.reflect;

import org.adamalang.runtime.natives.*;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.*;

/** convert a known java type into an Adama type */
public class TypeBridge {

  public static TyType getAdamaSubType(String core, final Class<?>[] hiddenTypes) {
    if (hiddenTypes == null || hiddenTypes.length == 0) {
      throw new RuntimeException(core + " requires @HiddenType/@HiddenTypes annotation because Java sucks");
    }
    Class<?> head = hiddenTypes[0];
    Class<?>[] tail = new Class[hiddenTypes.length - 1];
    for (int k = 0; k < tail.length; k++) {
      tail[k] = hiddenTypes[k + 1];
    }
    return getAdamaType(head, tail);
  }

  public static TyType getAdamaType(final Class<?> x, final Class<?>[] hiddenTypes) {
    if (int.class == x || Integer.class == x) {
      return new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (Long.class == x || long.class == x) {
      return new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (Double.class == x || double.class == x) {
      return new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (Boolean.class == x || boolean.class == x) {
      return new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (String.class == x) {
      return new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (String[].class == x) {
      return new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, null), null);
    } else if (NtPrincipal.class == x) {
      return new TyNativePrincipal(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (NtComplex.class == x) {
      return new TyNativeComplex(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (NtDate.class == x) {
      return new TyNativeDate(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (NtDateTime.class == x) {
      return new TyNativeDateTime(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (NtTime.class == x) {
      return new TyNativeTime(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (NtTimeSpan.class == x) {
      return new TyNativeTimeSpan(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (Void.class == x || void.class == x) {
      return null;
    } else if (NtDynamic.class == x) {
      return new TyNativeDynamic(TypeBehavior.ReadWriteNative, null, null);
    } else if (NtComplex.class == x) {
      return new TyNativeComplex(TypeBehavior.ReadWriteNative, null, null);
    } else if (NtList.class == x) {
      TyType subType = getAdamaSubType("NtList<>", hiddenTypes);
      return new TyNativeList(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(subType));
    } else if (NtMaybe.class == x) {
      TyType subType = getAdamaSubType("NtMaybe<>", hiddenTypes);
      return new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(subType));
    }
    throw new RuntimeException("can't find:" + x.toString());
  }
}
