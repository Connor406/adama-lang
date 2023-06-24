/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateStyleAggrTests extends BaseRxHtmlTest {
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("JavaScript:(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.E('button');");
    gold.append("\n    $.onT(c,'click',$.pV(a),'open');");
    gold.append("\n    c.append($.T('Toggle'));");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('button');");
    gold.append("\n    $.onT(c,'mouseenter',$.pV(a),'enter');");
    gold.append("\n    c.append($.T('Toggle'));");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:common style here also common also common");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><link rel=\"stylesheet\" href=\"/template.css\"><script src=\"/template.js\"></script></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <style>");
    source.append("\n        common style here");
    source.append("\n    </style>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <style>");
    source.append("\n            also common");
    source.append("\n        </style>");
    source.append("\n        <button rx:click=\"toggle:open\">Toggle</button>");
    source.append("\n        <button rx:mouseenter=\"toggle:enter\">Toggle</button>");
    source.append("\n        <style>");
    source.append("\n            also common");
    source.append("\n        </style>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
