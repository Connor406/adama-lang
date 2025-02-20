/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.natives.functions;

import org.adamalang.translator.tree.types.natives.TyNativeFunctional;

import java.util.ArrayList;

// this is.. SUPER strange, and this exists be life is strange
public class TyNativeFunctionInternalFieldReplacement extends TyNativeFunctional {
  public TyNativeFunctionInternalFieldReplacement(final String name, final ArrayList<FunctionOverloadInstance> overloads, final FunctionStyleJava style) {
    super(name, overloads, style);
  }
}
