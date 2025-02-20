/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;

import java.util.function.Consumer;

/** skip the first elements of a list */
public class Offset extends LinqExpression {
  public final Token offsetToken;
  public final Expression offset;

  public Offset(final Expression sql, final Token offsetToken, final Expression offset) {
    super(sql);
    ingest(sql);
    this.offsetToken = offsetToken;
    this.offset = offset;
    ingest(offset);
  }


  @Override
  public void emit(final Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(offsetToken);
    offset.emit(yielder);
  }


  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var computeEnv = environment.scopeWithComputeContext(ComputeContext.Computation);
    final var typeSql = sql.typing(computeEnv, null);
    environment.rules.IsNativeListOfStructure(typeSql, false);
    environment.rules.IsInteger(offset.typing(computeEnv, null), false);
    return typeSql;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var computeEnv = environment.scopeWithComputeContext(ComputeContext.Computation);
    sql.writeJava(sb, environment);
    sb.append(".skip(").append(intermediateExpression ? "false, " : "true, ");
    offset.writeJava(sb, computeEnv);
    sb.append(")");
  }

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
    offset.free(environment);
  }
}
