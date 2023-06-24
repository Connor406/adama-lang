/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;

import java.util.function.Consumer;

public abstract class InjectExpression extends Expression {
  public final TyType type;

  public InjectExpression(final TyType type) {
    this.type = type;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    return type;
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}
