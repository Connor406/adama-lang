/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.support.testgen;

public class PhaseTemplate {
  public static String inventEmpty() {
    final var templateBuilder = new StringBuilder();
    templateBuilder.append("\n");
    templateBuilder.append("@construct {\n");
    templateBuilder.append("}\n");
    templateBuilder.append("\n");
    templateBuilder.append("test PrimaryTest {\n");
    templateBuilder.append("  assert false;\n");
    templateBuilder.append("}\n");
    return templateBuilder.toString();
  }
}
