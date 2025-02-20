/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.DateTimeConstant;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.natives.functions.TyNativeFunctionInternalFieldReplacement;
import org.adamalang.translator.tree.types.traits.IsNativeValue;
import org.adamalang.translator.tree.types.traits.IsOrderable;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.function.Consumer;

/** Type for a native date and a time with the time zone in the typical gregorian calendar */
public class TyNativeDateTime extends TySimpleNative implements //
    IsNativeValue, //
    IsOrderable, //
    DetailHasDeltaType, //
    DetailTypeHasMethods, //
    AssignmentViaNative {
  public final Token readonlyToken;
  public final Token token;

  public TyNativeDateTime(final TypeBehavior behavior, final Token readonlyToken, final Token token) {
    super(behavior, "NtDateTime", "NtDateTime");
    this.readonlyToken = readonlyToken;
    this.token = token;
    ingest(token);
  }

  @Override
  public void emitInternal(Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(token);
  }

  @Override
  public String getAdamaType() {
    return "datetime";
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior) {
    return new TyNativeDateTime(newBehavior, readonlyToken, token).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_value");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    writer.writeString("datetime");
    writer.endObject();
  }

  @Override
  public String getDeltaType(Environment environment) {
    return "DDateTime";
  }

  @Override
  public Expression inventDefaultValueExpression(DocumentPosition forWhatExpression) {
    return new DateTimeConstant(ZonedDateTime.parse("1900-01-01T00:00:00-00:00"), token);
  }


  @Override
  public TyNativeFunctional lookupMethod(String name, Environment environment) {
    return environment.state.globals.findExtension(this, name);
  }
}
