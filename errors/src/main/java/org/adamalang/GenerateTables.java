/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang;

import java.lang.reflect.Field;

public class GenerateTables {
  public static String generate() throws Exception {
    StringBuilder sb = new StringBuilder();
    sb.append("package org.adamalang;\n\n");
    sb.append("import java.util.HashMap;\n");
    sb.append("import java.util.HashSet;\n");
    sb.append("\n");
    sb.append("public class ErrorTable {\n");
    sb.append("  public static final ErrorTable INSTANCE = new ErrorTable();\n");
    sb.append("  public final HashMap<Integer, String> names;\n");
    sb.append("  public final HashMap<Integer, String> descriptions;\n");
    sb.append("  private final HashSet<Integer> userspace;\n");
    sb.append("  private final HashSet<Integer> retry;\n");
    sb.append("\n");
    sb.append("  public boolean shouldRetry(int code) {\n");
    sb.append("    return retry.contains(code);\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public boolean isUserProblem(int code) {\n");
    sb.append("    return userspace.contains(code);\n");
    sb.append("  }\n");
    sb.append("\n");
    sb.append("  public ErrorTable() {\n");
    sb.append("    names = new HashMap<>();\n");
    sb.append("    descriptions = new HashMap<>();\n");
    sb.append("    userspace = new HashSet<>();\n");
    sb.append("    retry = new HashSet<>();\n");
    for (Field f : ErrorCodes.class.getFields()) {
      sb.append("    names.put(").append(f.getInt(null)).append(", \"").append(f.getName()).append("\");\n");
      Description description = f.getAnnotation(Description.class);
      if (description != null) {
        sb.append("    descriptions.put(").append(f.getInt(null)).append(", \"").append(description.value()).append("\");\n");
      } else {
        sb.append("    descriptions.put(").append(f.getInt(null)).append(", \"no description of error (yet)\");\n");
      }
      if (f.getAnnotation(User.class) != null) {
        sb.append("    userspace.add(").append(f.getInt(null)).append(");\n");
      }
      if (f.getAnnotation(RetryInternally.class) != null) {
        sb.append("    retry.add(").append(f.getInt(null)).append(");\n");
      }
    }
    sb.append("  }\n");
    sb.append("}\n");
    return sb.toString();
  }
}
