/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeTimeSpan;

import java.util.function.Consumer;

/** a time span measured in seconds as a constant within source */
public class TimeSpanConstant extends Expression {
  public final Token[] tokens;
  public final double seconds;

  public TimeSpanConstant(double seconds, Token... tokens) {
    this.seconds = seconds;
    this.tokens = tokens;
    for (Token token : tokens) {
      ingest(token);
    }
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    for (Token token : tokens) {
      yielder.accept(token);
    }
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    environment.mustBeComputeContext(this);
    return new TyNativeTimeSpan(TypeBehavior.ReadOnlyNativeValue, null, tokens[0]).withPosition(this);
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("new NtTimeSpan(").append(seconds).append(")");
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}
