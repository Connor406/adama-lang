/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtMap;

import java.util.Map;
import java.util.TreeMap;

/** a web delete */
public class WebDelete implements WebItem {
  public final WebContext context;
  public final String uri;
  public final WebPath router;
  public final NtMap<String, String> headers;
  public final NtDynamic parameters;

  public WebDelete(WebContext context, String uri, TreeMap<String, String> headers, NtDynamic parameters) {
    this.context = context;
    this.uri = uri;
    this.router = new WebPath(uri);
    this.headers = new NtMap<>();
    this.headers.storage.putAll(headers);
    this.parameters = parameters;
  }

  public void injectWrite(JsonStreamWriter writer) {
    writer.writeObjectFieldIntro("delete");
    writer.beginObject();
    writer.writeObjectFieldIntro("uri");
    writer.writeString(uri);
    writer.writeObjectFieldIntro("headers");
    writer.beginObject();
    for (Map.Entry<String, String> entry : headers.entries()) {
      writer.writeObjectFieldIntro(entry.getKey());
      writer.writeString(entry.getValue());
    }
    writer.endObject();
    writer.writeObjectFieldIntro("parameters");
    writer.writeNtDynamic(parameters);
    writer.endObject();
  }

  @Override
  public void writeAsObject(JsonStreamWriter writer) {
    writer.beginObject();
    injectWrite(writer);
    writer.endObject();
  }
}
