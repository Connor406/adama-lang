/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.transforms.DomainResolver;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.transforms.SpacePolicyLocator;
import org.adamalang.web.io.JsonLogger;;

public class RegionConnectionNexus {
  public final JsonLogger logger;
  public final RegionApiMetrics metrics;
  public final SimpleExecutor executor;
  public final DomainResolver domainService;
  public final PerSessionAuthenticator identityService;
  public final SpacePolicyLocator spaceService;

  public RegionConnectionNexus(JsonLogger logger, RegionApiMetrics metrics, SimpleExecutor executor, DomainResolver domainService, PerSessionAuthenticator identityService, SpacePolicyLocator spaceService) {
    this.logger = logger;
    this.metrics = metrics;
    this.executor = executor;
    this.domainService = domainService;
    this.identityService = identityService;
    this.spaceService = spaceService;
  }
}
