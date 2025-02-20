/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.adamalang.common.Json;
import org.adamalang.web.io.ConnectionContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class AdamaWebRequest {
  public final String uri;
  public final TreeMap<String, String> headers;
  public final String parameters;
  public final String body;

  public AdamaWebRequest(final FullHttpRequest req, ChannelHandlerContext ctx) {
    headers = new TreeMap<>();
    for (Map.Entry<String, String> entry : req.headers()) {
      String headerName = entry.getKey().toLowerCase(Locale.ROOT);
      if (headerName.equals("cookie")) {
        continue;
      }
      headers.put(headerName, entry.getValue());
    }
    ConnectionContext context = ConnectionContextFactory.of(ctx, req.headers());
    headers.put("origin", context.origin + "");
    headers.put("remote-ip", context.remoteIp + "");
    QueryStringDecoder qsd = new QueryStringDecoder(req.uri());
    this.uri = qsd.path();
    {
      ObjectNode parametersJson = Json.newJsonObject();
      for (Map.Entry<String, List<String>> param : qsd.parameters().entrySet()) {
        String key = param.getKey();
        List<String> values = param.getValue();
        if (values.size() == 0) {
          parametersJson.put(key, "");
        } else {
          parametersJson.put(key, values.get(0));
          if (values.size() > 1) {
            ArrayNode options = parametersJson.putArray(key + "*");
            for (String val : values) {
              options.add(val);
            }
          }
        }
      }
      this.parameters = parametersJson.toString();
    }

    if (req.method() == HttpMethod.POST || req.method() == HttpMethod.PUT) {
      byte[] memory = new byte[req.content().readableBytes()];
      req.content().readBytes(memory);
      if (memory.length == 0) {
        this.body = "{}";
      } else {
        String bodyString = new String(memory, StandardCharsets.UTF_8);
        String bodyTest = detectBodyAsQueryString(bodyString);
        if (bodyTest != null) {
          this.body = bodyTest;
        } else {
          this.body = Json.parseJsonObject(bodyString).toString();
        }
      }
    } else {
      this.body = null;
    }
  }

  public static String detectBodyAsQueryString(String body) {
    try {
      if (body.startsWith("{") || body.startsWith("[")) {
        return null;
      }
      QueryStringDecoder test = new QueryStringDecoder("/?" + body);
      ObjectNode bodyJson = Json.newJsonObject();
      for (Map.Entry<String, List<String>> param : test.parameters().entrySet()) {
        if (param.getValue().size() == 1) {
          bodyJson.put(param.getKey(), param.getValue().get(0));
        }
      }
      return bodyJson.toString();
    } catch (Exception ex) {
      return null;
    }
  }
}
