<!--
    Copyright 2025 sovity GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    SPDX-License-Identifier: Apache-2.0

    Contributors:
        sovity - init and continued development
-->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ce">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />API Wrapper &amp; API Clients:<br />Java API Client</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-ce/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ce/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this component

Java API Client Library to be imported and used in arbitrary applications like use-case backends.

## Installation

```xml
<!-- Requires the GitHub Maven Registry -->
<dependency>
  <groupId>de.sovity.edc</groupId>
  <artifactId>client</artifactId>
  <version>${sovity-edc-ce.version}</version>
</dependency>
```

## Usage

### Example Consuming and Providing a Data Offer

A full example providing and consuming a data offer using the API Wrapper Client Library can be found
in [ApiWrapperDemoTest.java](../../../../ce/connector-ce/src/test/kotlin/de/sovity/edc/ce/ApiWrapperDemoTestBase.kt).

### Example Using API Key Auth

```java
import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.KpiResult;

/**
 * Example using a sovity Community Edition EDC Connector
 */
public class WrapperClientExample {

  public static final String MANAGEMENT_API_URL = "http://localhost:11002/api/management";
  public static final String MANAGEMENT_API_KEY = "...";

  public static void main(String[] args) {
    // Configure Client
    EdcClient client = EdcClient.builder()
        .managementApiUrl(MANAGEMENT_API_URL)
        .managementApiKey(MANAGEMENT_API_KEY)
        .build();

    // EDC API Wrapper APIs are now available for use
    KpiResult kpiResult = client.useCaseApi().getKpis();
    System.out.println(kpiResult);
  }
}

```

### Example Using OAuth2 Client Credentials

```java
import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.KpiResult;
import de.sovity.edc.client.oauth2.Oauth2ClientCredentials;
import de.sovity.edc.client.oauth2.SovityKeycloakUrl;

/**
 * Example using a productive Connector-as-a-Service (CaaS) EDC Connector
 */
public class WrapperClientExample {

  public static final String MANAGEMENT_API_URL =
      "https://{{your-connector}}.prod-sovity.azure.sovity.io/control/api/management";
  public static final String CLIENT_ID = "{{your-connector}}-app";
  public static final String CLIENT_SECRET = "...";

  public static void main(String[] args) {
    // Configure Client
    EdcClient client = EdcClient.builder()
        .managementApiUrl(MANAGEMENT_API_URL)
        .oauth2ClientCredentials(OAuth2ClientCredentials.builder()
            .tokenUrl(SovityKeycloakUrl.PRODUCTION)
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .build())
        .build();

    // EDC API Wrapper APIs are now available for use
    KpiResult kpiResult = client.useCaseApi().getKpis();
    System.out.println(kpiResult);
  }
}
```

### Further Examples

Below are the examples of various tasks and the corresponding methods to be used from the Java-client.

| Task                                                 | Java-Client method                                                  |
|------------------------------------------------------|---------------------------------------------------------------------|
| Create Policy                                        | `EdcClient.uiApi().createPolicyDefinitionV2(policyDefinition)`      |
| Create asset (Asset Creation after activate)         | `EdcClient.uiApi().createAsset(uiAssetRequest)`                     |
| Create contract definition                           | `EdcClient.uiApi().createContractDefinition(contractDefinition)`    |
| Create Offer on consumer dashboard (Catalog Browser) | `EdcClient.uiApi().getCatalogPageDataOffers(PROTOCOL_ENDPOINT)`     |
| Accept contract (Contract Negotiation)               | `EdcClient.uiApi().initiateContractNegotiation(negotiationRequest)` |
| Transfer Data (Initiate Transfer)                    | `EdcClient.uiApi().initiateTransfer(negotiation)`                   |

These methods facilitate various operations such as creating policies, assets, contract definitions, browsing offers, accepting contracts, and initiating data transfers.

### Example Creating a Catena-Policy using operators (AND/OR/XONE)

The following example demonstrates how to create a Catena-Policy with linked conditions using the Java-client.

```java
public String createCatenaXPolicy() {
  var policyId = UUID.randomUUID().toString();

  var expression = buildAnd(
      buildConstraint("Membership", OperatorDto.EQ, "active"),
      buildConstraint("PURPOSE", OperatorDto.EQ, "ID 3.1 Trace")
  );

  var policyCreateRequest = PolicyDefinitionCreateDto.builder()
      .policyDefinitionId(policyId)
      .expression(expression)
      .build();

  client.uiApi().createPolicyDefinition(policyCreateRequest);

  return policyId;
}

private UiPolicyExpression buildAnd(UiPolicyExpression... expressions) {
  return UiPolicyExpression.builder()
      .type(UiPolicyExpressionType.AND)
      .expressions(Arrays.asList(expressions))
      .build();
}

private UiPolicyExpression buildConstraint(
    String left,
    OperatorDto operator,
    String right
) {
  return UiPolicyExpression.builder()
      .type(UiPolicyExpressionType.CONSTRAINT)
      .constraint(UiPolicyConstraint.builder()
          .left(left)
          .operator(operator)
          .right(UiPolicyLiteral.builder()
              .type(UiPolicyLiteralType.STRING)
              .value(right)
              .build())
          .build())
      .build();
}
```

## License

Apache License 2.0 - see [LICENSE](https://github.com/sovity/edc-ce/blob/main/LICENSE)

## Contact

sovity GmbH - contact@sovity.de
