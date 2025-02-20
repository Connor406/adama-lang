/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.common;

import org.junit.Assert;
import org.junit.Test;

public class StringBuilderWithTabsTests {
  @Test
  public void tabAndTabUpAndTabDownWithNewLines() {
    final var sb = new StringBuilderWithTabs();
    sb.tab().tabUp().writeNewline().tabDown().writeNewline();
    Assert.assertEquals("  \n  \n", sb.toString());
    sb.tabDown().tabDown().tabDown().tabDown().writeNewline();
    Assert.assertEquals("  \n  \n\n", sb.toString());
  }
}
