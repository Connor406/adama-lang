/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;

/** responsible for parts of the document which are common */
public class CodeGenDocument {
  public static void writePrelude(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("import org.adamalang.runtime.async.*;").writeNewline();
    sb.append("import org.adamalang.runtime.contracts.*;").writeNewline();
    sb.append("import org.adamalang.runtime.delta.*;").writeNewline();
    sb.append("import org.adamalang.runtime.delta.secure.*;").writeNewline();
    sb.append("import org.adamalang.runtime.exceptions.*;").writeNewline();
    sb.append("import org.adamalang.runtime.index.*;").writeNewline();
    sb.append("import org.adamalang.runtime.json.*;").writeNewline();
    sb.append("import org.adamalang.runtime.natives.*;").writeNewline();
    sb.append("import org.adamalang.runtime.natives.algo.*;").writeNewline();
    sb.append("import org.adamalang.runtime.natives.lists.*;").writeNewline();
    sb.append("import org.adamalang.runtime.ops.*;").writeNewline();
    sb.append("import org.adamalang.runtime.reactives.*;").writeNewline();
    sb.append("import org.adamalang.runtime.remote.*;").writeNewline();
    sb.append("import org.adamalang.runtime.stdlib.*;").writeNewline();
    sb.append("import org.adamalang.runtime.sys.*;").writeNewline();
    sb.append("import org.adamalang.runtime.sys.web.*;").writeNewline();
    sb.append("import org.adamalang.runtime.text.*;").writeNewline();
    sb.append("import java.time.*;").writeNewline();
    sb.append("import java.util.function.Consumer;").writeNewline();
    sb.append("import java.util.function.Function;").writeNewline();
    sb.append("import java.util.ArrayList;").writeNewline();
    sb.append("import java.util.Comparator;").writeNewline();
    sb.append("import java.util.HashMap;").writeNewline();
    sb.append("import java.util.HashSet;").writeNewline();
    sb.append("import java.util.Map;").writeNewline();
    sb.append("import java.util.Set;").writeNewline();
    for (final String imp : environment.state.globals.imports()) {
      if (imp.startsWith("org.adamalang.runtime.stdlib")) {
        continue;
      }
      sb.append("import ").append(imp).append(";").writeNewline();
    }
  }
}
