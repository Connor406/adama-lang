/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.extern.aws;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.aws.SignatureV4;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.extern.SignalControl;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;
import org.adamalang.web.client.StringCallbackHttpResponder;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;

/** simple queue service */
public class SQS implements SignalControl {
  private final Logger LOGGER = LoggerFactory.getLogger(SQS.class);
  private final WebClientBase base;
  private final AWSConfig config;
  private final AWSMetrics metrics;
  private final String queue;

  public SQS(WebClientBase base, AWSConfig config, AWSMetrics metrics) {
    this.base = base;
    this.config = config;
    this.metrics = metrics;
    this.queue = config.queue;
  }

  public void queue(String message, Callback<Void> callback) {
    // https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_SendMessage.html
    SignatureV4 v4 = new SignatureV4(config.credential, config.region, "sqs", "GET", "sqs." + config.region + ".amazonaws.com", "/" + config.queue);
    v4.withParameter("Action", "SendMessage");
    v4.withParameter("MessageBody", message);
    v4.withParameter("Version", "2012-11-05");
    String url = "https://sqs." + config.region + ".amazonaws.com/" + config.queue + v4.signedQuery();
    SimpleHttpRequest request = new SimpleHttpRequest("GET", url, new TreeMap<>(), SimpleHttpRequestBody.EMPTY);
    base.execute(request, new StringCallbackHttpResponder(LOGGER, metrics.enqueue.start(), new Callback<String>() {
      @Override
      public void success(String value) {
        callback.success(null);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    }));
  }

  @Override
  public void raiseAutomaticDomain(String domain) {
    ObjectNode message = Json.newJsonObject();
    message.put("command", "domain");
    message.put("domain", domain);
    queue(message.toString(), metrics.signal_control_domain.wrap(Callback.DONT_CARE_VOID));
  }
}
