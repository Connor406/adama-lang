/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.TestFrontEnd;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class GeneratedMissingParameterTest {
  @Test
  public void missing() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      ObjectNode node;
      String _identity = fe.setupDevIdentity();
      //InitSetupAccount
      node = Json.newJsonObject();
      node.put("id", 1);
      node.put("method", "init/setup-account");
      Iterator<String> c2 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:473103", c2.next());
      node.put("email", "x@x.com");
      //InitConvertGoogleUser
      node = Json.newJsonObject();
      node.put("id", 2);
      node.put("method", "init/convert-google-user");
      Iterator<String> c3 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:407544", c3.next());
      node.put("access-token", "xzya");
      //InitCompleteAccount
      node = Json.newJsonObject();
      node.put("id", 3);
      node.put("method", "init/complete-account");
      Iterator<String> c4 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:473103", c4.next());
      node.put("email", "x@x.com");
      Iterator<String> c5 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:455681", c5.next());
      node.put("code", "xzya");
      //AccountSetPassword
      node = Json.newJsonObject();
      node.put("id", 5);
      node.put("method", "account/set-password");
      Iterator<String> c6 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c6.next());
      node.put("identity", _identity);
      Iterator<String> c7 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c7.next());
      node.put("password", "xzya");
      //AccountGetPaymentPlan
      node = Json.newJsonObject();
      node.put("id", 7);
      node.put("method", "account/get-payment-plan");
      Iterator<String> c8 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c8.next());
      node.put("identity", _identity);
      //AccountLogin
      node = Json.newJsonObject();
      node.put("id", 8);
      node.put("method", "account/login");
      Iterator<String> c9 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:473103", c9.next());
      node.put("email", "x@x.com");
      Iterator<String> c10 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c10.next());
      node.put("password", "xzya");
      //Probe
      node = Json.newJsonObject();
      node.put("id", 10);
      node.put("method", "probe");
      Iterator<String> c11 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c11.next());
      node.put("identity", _identity);
      //AuthorityCreate
      node = Json.newJsonObject();
      node.put("id", 11);
      node.put("method", "authority/create");
      Iterator<String> c12 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c12.next());
      node.put("identity", _identity);
      //AuthoritySet
      node = Json.newJsonObject();
      node.put("id", 12);
      node.put("method", "authority/set");
      Iterator<String> c13 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c13.next());
      node.put("identity", _identity);
      Iterator<String> c14 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:430095", c14.next());
      node.put("authority", "xzya");
      Iterator<String> c15 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:457743", c15.next());
      node.put("key-store", Json.newJsonObject());
      //AuthorityGet
      node = Json.newJsonObject();
      node.put("id", 15);
      node.put("method", "authority/get");
      Iterator<String> c16 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c16.next());
      node.put("identity", _identity);
      Iterator<String> c17 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:430095", c17.next());
      node.put("authority", "xzya");
      //AuthorityList
      node = Json.newJsonObject();
      node.put("id", 17);
      node.put("method", "authority/list");
      Iterator<String> c18 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c18.next());
      node.put("identity", _identity);
      //AuthorityDestroy
      node = Json.newJsonObject();
      node.put("id", 18);
      node.put("method", "authority/destroy");
      Iterator<String> c19 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c19.next());
      node.put("identity", _identity);
      Iterator<String> c20 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:430095", c20.next());
      node.put("authority", "xzya");
      //SpaceCreate
      node = Json.newJsonObject();
      node.put("id", 20);
      node.put("method", "space/create");
      Iterator<String> c21 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c21.next());
      node.put("identity", _identity);
      Iterator<String> c22 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c22.next());
      node.put("space", "xzya");
      //SpaceGenerateKey
      node = Json.newJsonObject();
      node.put("id", 22);
      node.put("method", "space/generate-key");
      Iterator<String> c23 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c23.next());
      node.put("identity", _identity);
      Iterator<String> c24 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c24.next());
      node.put("space", "xzya");
      //SpaceUsage
      node = Json.newJsonObject();
      node.put("id", 24);
      node.put("method", "space/usage");
      Iterator<String> c25 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c25.next());
      node.put("identity", _identity);
      Iterator<String> c26 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c26.next());
      node.put("space", "xzya");
      //SpaceGet
      node = Json.newJsonObject();
      node.put("id", 26);
      node.put("method", "space/get");
      Iterator<String> c27 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c27.next());
      node.put("identity", _identity);
      Iterator<String> c28 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c28.next());
      node.put("space", "xzya");
      //SpaceSet
      node = Json.newJsonObject();
      node.put("id", 28);
      node.put("method", "space/set");
      Iterator<String> c29 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c29.next());
      node.put("identity", _identity);
      Iterator<String> c30 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c30.next());
      node.put("space", "xzya");
      Iterator<String> c31 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425999", c31.next());
      node.put("plan", Json.newJsonObject());
      //SpaceRedeployKick
      node = Json.newJsonObject();
      node.put("id", 31);
      node.put("method", "space/redeploy-kick");
      Iterator<String> c32 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c32.next());
      node.put("identity", _identity);
      Iterator<String> c33 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c33.next());
      node.put("space", "xzya");
      //SpaceSetRxhtml
      node = Json.newJsonObject();
      node.put("id", 33);
      node.put("method", "space/set-rxhtml");
      Iterator<String> c34 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c34.next());
      node.put("identity", _identity);
      Iterator<String> c35 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c35.next());
      node.put("space", "xzya");
      Iterator<String> c36 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:402428", c36.next());
      node.put("rxhtml", "xzya");
      //SpaceGetRxhtml
      node = Json.newJsonObject();
      node.put("id", 36);
      node.put("method", "space/get-rxhtml");
      Iterator<String> c37 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c37.next());
      node.put("identity", _identity);
      Iterator<String> c38 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c38.next());
      node.put("space", "xzya");
      //SpaceDelete
      node = Json.newJsonObject();
      node.put("id", 38);
      node.put("method", "space/delete");
      Iterator<String> c39 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c39.next());
      node.put("identity", _identity);
      Iterator<String> c40 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c40.next());
      node.put("space", "xzya");
      //SpaceSetRole
      node = Json.newJsonObject();
      node.put("id", 40);
      node.put("method", "space/set-role");
      Iterator<String> c41 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c41.next());
      node.put("identity", _identity);
      Iterator<String> c42 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c42.next());
      node.put("space", "xzya");
      Iterator<String> c43 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:473103", c43.next());
      node.put("email", "x@x.com");
      Iterator<String> c44 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:456716", c44.next());
      node.put("role", "xzya");
      //SpaceListDevelopers
      node = Json.newJsonObject();
      node.put("id", 44);
      node.put("method", "space/list-developers");
      Iterator<String> c45 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c45.next());
      node.put("identity", _identity);
      Iterator<String> c46 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c46.next());
      node.put("space", "xzya");
      //SpaceReflect
      node = Json.newJsonObject();
      node.put("id", 46);
      node.put("method", "space/reflect");
      Iterator<String> c47 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c47.next());
      node.put("identity", _identity);
      Iterator<String> c48 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c48.next());
      node.put("space", "xzya");
      Iterator<String> c49 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c49.next());
      node.put("key", "xzya");
      //SpaceList
      node = Json.newJsonObject();
      node.put("id", 49);
      node.put("method", "space/list");
      Iterator<String> c50 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c50.next());
      node.put("identity", _identity);
      //DomainMap
      node = Json.newJsonObject();
      node.put("id", 50);
      node.put("method", "domain/map");
      Iterator<String> c51 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c51.next());
      node.put("identity", _identity);
      Iterator<String> c52 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c52.next());
      node.put("domain", "xzya");
      Iterator<String> c53 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c53.next());
      node.put("space", "xzya");
      //DomainMapDocument
      node = Json.newJsonObject();
      node.put("id", 53);
      node.put("method", "domain/map-document");
      Iterator<String> c54 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c54.next());
      node.put("identity", _identity);
      Iterator<String> c55 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c55.next());
      node.put("domain", "xzya");
      Iterator<String> c56 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c56.next());
      node.put("space", "xzya");
      Iterator<String> c57 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c57.next());
      node.put("key", "xzya");
      //DomainList
      node = Json.newJsonObject();
      node.put("id", 57);
      node.put("method", "domain/list");
      Iterator<String> c58 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c58.next());
      node.put("identity", _identity);
      //DomainUnmap
      node = Json.newJsonObject();
      node.put("id", 58);
      node.put("method", "domain/unmap");
      Iterator<String> c59 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c59.next());
      node.put("identity", _identity);
      Iterator<String> c60 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c60.next());
      node.put("domain", "xzya");
      //DomainGet
      node = Json.newJsonObject();
      node.put("id", 60);
      node.put("method", "domain/get");
      Iterator<String> c61 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c61.next());
      node.put("identity", _identity);
      Iterator<String> c62 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c62.next());
      node.put("domain", "xzya");
      //DocumentAuthorize
      node = Json.newJsonObject();
      node.put("id", 62);
      node.put("method", "document/authorize");
      Iterator<String> c63 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c63.next());
      node.put("space", "xzya");
      Iterator<String> c64 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c64.next());
      node.put("key", "xzya");
      Iterator<String> c65 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458737", c65.next());
      node.put("username", "xzya");
      Iterator<String> c66 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c66.next());
      node.put("password", "xzya");
      //DocumentAuthorizeDomain
      node = Json.newJsonObject();
      node.put("id", 66);
      node.put("method", "document/authorize-domain");
      Iterator<String> c67 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c67.next());
      node.put("domain", "xzya");
      Iterator<String> c68 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458737", c68.next());
      node.put("username", "xzya");
      Iterator<String> c69 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c69.next());
      node.put("password", "xzya");
      //DocumentCreate
      node = Json.newJsonObject();
      node.put("id", 69);
      node.put("method", "document/create");
      Iterator<String> c70 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c70.next());
      node.put("identity", _identity);
      Iterator<String> c71 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c71.next());
      node.put("space", "xzya");
      Iterator<String> c72 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c72.next());
      node.put("key", "xzya");
      Iterator<String> c73 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461826", c73.next());
      node.put("arg", Json.newJsonObject());
      //DocumentDelete
      node = Json.newJsonObject();
      node.put("id", 73);
      node.put("method", "document/delete");
      Iterator<String> c74 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c74.next());
      node.put("identity", _identity);
      Iterator<String> c75 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c75.next());
      node.put("space", "xzya");
      Iterator<String> c76 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c76.next());
      node.put("key", "xzya");
      //DocumentList
      node = Json.newJsonObject();
      node.put("id", 76);
      node.put("method", "document/list");
      Iterator<String> c77 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c77.next());
      node.put("identity", _identity);
      Iterator<String> c78 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c78.next());
      node.put("space", "xzya");
      //MessageDirectSend
      node = Json.newJsonObject();
      node.put("id", 78);
      node.put("method", "message/direct-send");
      Iterator<String> c79 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c79.next());
      node.put("identity", _identity);
      Iterator<String> c80 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c80.next());
      node.put("space", "xzya");
      Iterator<String> c81 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c81.next());
      node.put("key", "xzya");
      Iterator<String> c82 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:454659", c82.next());
      node.put("channel", "xzya");
      Iterator<String> c83 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c83.next());
      node.put("message", Json.newJsonObject());
      //MessageDirectSendOnce
      node = Json.newJsonObject();
      node.put("id", 83);
      node.put("method", "message/direct-send-once");
      Iterator<String> c84 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c84.next());
      node.put("identity", _identity);
      Iterator<String> c85 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c85.next());
      node.put("space", "xzya");
      Iterator<String> c86 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c86.next());
      node.put("key", "xzya");
      Iterator<String> c87 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:454659", c87.next());
      node.put("channel", "xzya");
      Iterator<String> c88 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c88.next());
      node.put("message", Json.newJsonObject());
      //ConnectionCreate
      node = Json.newJsonObject();
      node.put("id", 88);
      node.put("method", "connection/create");
      Iterator<String> c89 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c89.next());
      node.put("identity", _identity);
      Iterator<String> c90 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c90.next());
      node.put("space", "xzya");
      Iterator<String> c91 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c91.next());
      node.put("key", "xzya");
      //ConnectionCreateViaDomain
      node = Json.newJsonObject();
      node.put("id", 91);
      node.put("method", "connection/create-via-domain");
      Iterator<String> c92 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c92.next());
      node.put("identity", _identity);
      Iterator<String> c93 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c93.next());
      node.put("domain", "xzya");
      //ConnectionSend
      node = Json.newJsonObject();
      node.put("id", 93);
      node.put("method", "connection/send");
      Iterator<String> c94 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c94.next());
      node.put("connection", 100L);
      Iterator<String> c95 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:454659", c95.next());
      node.put("channel", "xzya");
      Iterator<String> c96 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c96.next());
      node.put("message", Json.newJsonObject());
      //ConnectionPassword
      node = Json.newJsonObject();
      node.put("id", 96);
      node.put("method", "connection/password");
      Iterator<String> c97 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c97.next());
      node.put("connection", 100L);
      Iterator<String> c98 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458737", c98.next());
      node.put("username", "xzya");
      Iterator<String> c99 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c99.next());
      node.put("password", "xzya");
      Iterator<String> c100 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466931", c100.next());
      node.put("new_password", "xzya");
      //ConnectionSendOnce
      node = Json.newJsonObject();
      node.put("id", 100);
      node.put("method", "connection/send-once");
      Iterator<String> c101 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c101.next());
      node.put("connection", 100L);
      Iterator<String> c102 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:454659", c102.next());
      node.put("channel", "xzya");
      Iterator<String> c103 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c103.next());
      node.put("message", Json.newJsonObject());
      //ConnectionCanAttach
      node = Json.newJsonObject();
      node.put("id", 103);
      node.put("method", "connection/can-attach");
      Iterator<String> c104 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c104.next());
      node.put("connection", 100L);
      //ConnectionAttach
      node = Json.newJsonObject();
      node.put("id", 104);
      node.put("method", "connection/attach");
      Iterator<String> c105 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c105.next());
      node.put("connection", 100L);
      Iterator<String> c106 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:476156", c106.next());
      node.put("asset-id", "xzya");
      Iterator<String> c107 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:470028", c107.next());
      node.put("filename", "xzya");
      Iterator<String> c108 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:455691", c108.next());
      node.put("content-type", "xzya");
      Iterator<String> c109 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:477179", c109.next());
      node.put("size", 100L);
      Iterator<String> c110 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:445437", c110.next());
      node.put("digest-md5", "xzya");
      Iterator<String> c111 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:406525", c111.next());
      node.put("digest-sha384", "xzya");
      //ConnectionUpdate
      node = Json.newJsonObject();
      node.put("id", 111);
      node.put("method", "connection/update");
      Iterator<String> c112 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c112.next());
      node.put("connection", 100L);
      //ConnectionEnd
      node = Json.newJsonObject();
      node.put("id", 112);
      node.put("method", "connection/end");
      Iterator<String> c113 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c113.next());
      node.put("connection", 100L);
      //DocumentsHashPassword
      node = Json.newJsonObject();
      node.put("id", 113);
      node.put("method", "documents/hash-password");
      Iterator<String> c114 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c114.next());
      node.put("password", "xzya");
      //ConfigureMakeOrGetAssetKey
      node = Json.newJsonObject();
      node.put("id", 114);
      node.put("method", "configure/make-or-get-asset-key");
      //AttachmentStart
      node = Json.newJsonObject();
      node.put("id", 114);
      node.put("method", "attachment/start");
      Iterator<String> c115 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c115.next());
      node.put("identity", _identity);
      Iterator<String> c116 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c116.next());
      node.put("space", "xzya");
      Iterator<String> c117 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c117.next());
      node.put("key", "xzya");
      Iterator<String> c118 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:470028", c118.next());
      node.put("filename", "xzya");
      Iterator<String> c119 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:455691", c119.next());
      node.put("content-type", "xzya");
      //AttachmentAppend
      node = Json.newJsonObject();
      node.put("id", 119);
      node.put("method", "attachment/append");
      Iterator<String> c120 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:409609", c120.next());
      node.put("upload", 100L);
      Iterator<String> c121 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:462859", c121.next());
      node.put("chunk-md5", "xzya");
      Iterator<String> c122 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:409608", c122.next());
      node.put("base64-bytes", "xzya");
      //AttachmentFinish
      node = Json.newJsonObject();
      node.put("id", 122);
      node.put("method", "attachment/finish");
      Iterator<String> c123 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:409609", c123.next());
      node.put("upload", 100L);
      //SuperCheckIn
      node = Json.newJsonObject();
      node.put("id", 123);
      node.put("method", "super/check-in");
      Iterator<String> c124 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c124.next());
      node.put("identity", _identity);
      //SuperListAutomaticDomains
      node = Json.newJsonObject();
      node.put("id", 124);
      node.put("method", "super/list-automatic-domains");
      Iterator<String> c125 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c125.next());
      node.put("identity", _identity);
      Iterator<String> c126 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:439292", c126.next());
      node.put("timestamp", 100L);
      //SuperSetDomainCertificate
      node = Json.newJsonObject();
      node.put("id", 126);
      node.put("method", "super/set-domain-certificate");
      Iterator<String> c127 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c127.next());
      node.put("identity", _identity);
      Iterator<String> c128 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c128.next());
      node.put("domain", "xzya");
      Iterator<String> c129 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:439292", c129.next());
      node.put("timestamp", 100L);
    }
  }
}
