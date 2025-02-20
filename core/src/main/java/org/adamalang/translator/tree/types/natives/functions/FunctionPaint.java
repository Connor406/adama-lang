/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.natives.functions;

import org.adamalang.translator.parser.token.Token;

import java.util.function.Consumer;

/** painting of a function with various properties */
public class FunctionPaint {
  private static final Token[] EMPTY_TOKENS = new Token[0];
  private final Token[] tokens;
  public final boolean pure;
  public final boolean castArgs;
  public final boolean aborts;
  public final boolean viewer;

  public FunctionPaint(final boolean pure, final boolean castArgs, final boolean aborts) {
    this.tokens = EMPTY_TOKENS;
    this.pure = pure;
    this.castArgs = castArgs;
    this.aborts = aborts;
    this.viewer = false;
  }

  public FunctionPaint(Token... tokens) {
    this.tokens = tokens;
    boolean _pure = false;
    boolean _aborts = false;
    boolean _viewer = false;
    for (Token token : tokens) {
      if (token.text.equals("readonly")) {
        _pure = true;
      }
      if (token.text.equals("aborts")) {
        _aborts = true;
      }
      if (token.text.equals("viewer")) {
        _viewer = true;
      }
    }
    this.pure = _pure;
    this.castArgs = false;
    this.aborts = _aborts;
    this.viewer = _viewer;
  }

  public void emit(Consumer<Token> yielder) {
    for (Token token : tokens) {
      yielder.accept(token);
    }
  }

  public static final FunctionPaint READONLY_NORMAL = new FunctionPaint(true, false, false);
  public static final FunctionPaint CAST_NORMAL = new FunctionPaint(false, true, false);
  public static final FunctionPaint NORMAL = new FunctionPaint(false, false, false);
}
