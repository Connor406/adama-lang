/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateViewsyncTests extends BaseRxHtmlTest {
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
    gold.append("\n    var c=$.RX([]);");
    gold.append("\n    c.name='myname';");
    gold.append("\n    c.identity=true;");
    gold.append("\n    c.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,c);");
    gold.append("\n    c.__();");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='myname';");
    gold.append("\n    $.P(b,a,d,function(b,e) {");
    gold.append("\n      $.VSy(b,e,function(b,f) {");
    gold.append("\n        b.append($.T(' Show '));");
    gold.append("\n      },function(b,f) {");
    gold.append("\n        var g = $.E('div');");
    gold.append("\n        g.append($.T(' Not yet... '));");
    gold.append("\n        b.append(g);");
    gold.append("\n      });");
    gold.append("\n    },function(b,e) {");
    gold.append("\n    });");
    gold.append("\n    d.__();");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.RX([]);");
    gold.append("\n    c.name='myname';");
    gold.append("\n    c.identity=true;");
    gold.append("\n    c.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,c);");
    gold.append("\n    c.__();");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='myname';");
    gold.append("\n    $.P(b,a,d,function(b,e) {");
    gold.append("\n      $.VSy(b,e,function(b,f) {");
    gold.append("\n        b.append($.T(' Show '));");
    gold.append("\n      },function(b,f) {");
    gold.append("\n        var g = $.E('div');");
    gold.append("\n        g.append($.T(' Not yet... '));");
    gold.append("\n        b.append(g);");
    gold.append("\n      });");
    gold.append("\n    },function(b,e) {");
    gold.append("\n    });");
    gold.append("\n    d.__();");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</script><style>");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</style></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <connection name=\"myname\" use-domain>");
    source.append("\n            <viewsync>");
    source.append("\n                Show");
    source.append("\n                <div rx:disconnected>");
    source.append("\n                    Not yet...");
    source.append("\n                </div>");
    source.append("\n            </viewsync>");
    source.append("\n        </connection>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
