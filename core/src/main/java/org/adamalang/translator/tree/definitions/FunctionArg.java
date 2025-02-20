/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;

/** argument pair for the tuple (type, name) */
public class FunctionArg {
  public final Token argNameToken;
  public final Token commaToken;
  public String argName;
  public TyType type;

  public FunctionArg(final Token commaToken, final TyType type, final Token argNameToken) {
    this.commaToken = commaToken;
    this.type = type;
    this.argNameToken = argNameToken;
    argName = argNameToken.text;
  }

  public void typing(final Environment environment) {
    type = environment.rules.Resolve(type, false);
    if (type != null) {
      type.typing(environment);
    }
  }
}
