/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import org.adamalang.frontend.Session;

public interface RootGlobalHandler {
  public void handle(Session session, InitSetupAccountRequest request, SimpleResponder responder);

  public void handle(Session session, InitConvertGoogleUserRequest request, InitiationResponder responder);

  public void handle(Session session, InitCompleteAccountRequest request, InitiationResponder responder);

  public void handle(Session session, AccountSetPasswordRequest request, SimpleResponder responder);

  public void handle(Session session, AccountGetPaymentPlanRequest request, PaymentResponder responder);

  public void handle(Session session, AccountLoginRequest request, InitiationResponder responder);

  public void handle(Session session, ProbeRequest request, SimpleResponder responder);

  public void handle(Session session, AuthorityCreateRequest request, ClaimResultResponder responder);

  public void handle(Session session, AuthoritySetRequest request, SimpleResponder responder);

  public void handle(Session session, AuthorityGetRequest request, KeystoreResponder responder);

  public void handle(Session session, AuthorityListRequest request, AuthorityListingResponder responder);

  public void handle(Session session, AuthorityDestroyRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceCreateRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceGenerateKeyRequest request, KeyPairResponder responder);

  public void handle(Session session, SpaceUsageRequest request, BillingUsageResponder responder);

  public void handle(Session session, SpaceGetRequest request, PlanResponder responder);

  public void handle(Session session, SpaceSetRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceRedeployKickRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceSetRxhtmlRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceGetRxhtmlRequest request, RxhtmlResponder responder);

  public void handle(Session session, SpaceDeleteRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceSetRoleRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceListDevelopersRequest request, DeveloperResponder responder);

  public void handle(Session session, SpaceReflectRequest request, ReflectionResponder responder);

  public void handle(Session session, SpaceListRequest request, SpaceListingResponder responder);

  public void handle(Session session, DomainMapRequest request, SimpleResponder responder);

  public void handle(Session session, DomainMapDocumentRequest request, SimpleResponder responder);

  public void handle(Session session, DomainListRequest request, DomainListingResponder responder);

  public void handle(Session session, DomainUnmapRequest request, SimpleResponder responder);

  public void handle(Session session, DomainGetRequest request, DomainPolicyResponder responder);

  public void handle(Session session, DocumentListRequest request, KeyListingResponder responder);

  public void handle(Session session, ConfigureMakeOrGetAssetKeyRequest request, AssetKeyResponder responder);

  public void handle(Session session, SuperCheckInRequest request, SimpleResponder responder);

  public void handle(Session session, SuperListAutomaticDomainsRequest request, AutomaticDomainListingResponder responder);

  public void handle(Session session, SuperSetDomainCertificateRequest request, SimpleResponder responder);

  public void disconnect();

  public static boolean test(String method) {
    switch (method) {
      case "init/setup-account":
      case "init/convert-google-user":
      case "init/complete-account":
      case "account/set-password":
      case "account/get-payment-plan":
      case "account/login":
      case "probe":
      case "authority/create":
      case "authority/set":
      case "authority/get":
      case "authority/list":
      case "authority/destroy":
      case "space/create":
      case "space/generate-key":
      case "space/usage":
      case "space/get":
      case "space/set":
      case "space/redeploy-kick":
      case "space/set-rxhtml":
      case "space/get-rxhtml":
      case "space/delete":
      case "space/set-role":
      case "space/list-developers":
      case "space/reflect":
      case "space/list":
      case "domain/map":
      case "domain/map-document":
      case "domain/list":
      case "domain/unmap":
      case "domain/get":
      case "document/list":
      case "configure/make-or-get-asset-key":
      case "super/check-in":
      case "super/list-automatic-domains":
      case "super/set-domain-certificate":
        return true;
      default:
        return false;
    }
  }
}
