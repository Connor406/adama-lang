/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.transforms.results;

import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.web.io.ConnectionContext;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;

public class AuthenticatedUserTests {

  @Test
  public void flow() throws Exception {
    AuthenticatedUser user = new AuthenticatedUser(AuthenticatedUser.Source.Adama, 123, new NtPrincipal("jeff", "adama"), new ConnectionContext("origin", "ip", "agent", "asset-key"), false);
    Assert.assertFalse(user.internal);
    KeyPair pair = PerSessionAuthenticator.inventHostKey();
    String identity = user.asIdentity(42, pair.getPrivate());
    PerSessionAuthenticator.ParsedToken parsedToken = new PerSessionAuthenticator.ParsedToken(identity);
    Assert.assertEquals(AuthenticatedUser.Source.Adama, parsedToken.proxy_source);
    Assert.assertEquals(42, parsedToken.key_id);
    Assert.assertEquals(123, parsedToken.proxy_user_id);
    Assert.assertEquals("jeff", parsedToken.sub);
    Assert.assertEquals("host", parsedToken.iss);
    Assert.assertEquals("adama", parsedToken.proxy_authority);
    Assert.assertEquals("origin", parsedToken.proxy_origin);
    Assert.assertEquals("ip", parsedToken.proxy_ip);
    Assert.assertEquals("agent", parsedToken.proxy_useragent);
    Assert.assertEquals("asset-key", parsedToken.proxy_asset_key);
  }
}
