<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />API Wrapper &amp; API Clients:<br />Java API Client</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this component

Java API Client Library to be imported and used in arbitrary applications like use-case backends.

An example project using this client can be found [here](../java-client-example).

## Installation

```xml
<!-- Requires the GitHub Maven Registry -->
<dependency>
  <groupId>de.sovity.edc</groupId>
  <artifactId>client</artifactId>
  <version>${sovity-edc-extensions.version}</version>
</dependency>
```

## Usage

### Example Consuming and Providing a Data Offer

A full example providing and consuming a data offer using the API Wrapper Client Library can be found
in [ApiWrapperDemoTest.java](../../../../tests/src/test/java/de/sovity/edc/e2e/ApiWrapperDemoTest.java).

### Example Using API Key Auth

```java
import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.KpiResult;

/**
 * Example using a sovity Community Edition EDC Connector
 */
public class WrapperClientExample {

    public static final String CONNECTOR_ENDPOINT = "http://localhost:11002/api/management/v2";
    public static final String CONNECTOR_API_KEY = "...";

    public static void main(String[] args) {
        // Configure Client
        EdcClient client = EdcClient.builder()
                .managementApiUrl(CONNECTOR_ENDPOINT)
                .managementApiKey(CONNECTOR_API_KEY)
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
import de.sovity.edc.client.oauth2.OAuth2ClientCredentials;
import de.sovity.edc.client.oauth2.SovityKeycloakUrl;

/**
 * Example using a productive Connector-as-a-Service (CaaS) EDC Connector
 */
public class WrapperClientExample {

    public static final String CONNECTOR_ENDPOINT =
            "https://{{your-connector}}.prod-sovity.azure.sovity.io/control/data";
    public static final String CLIENT_ID = "{{your-connector}}-app";
    public static final String CLIENT_SECRET = "...";

    public static void main(String[] args) {
        // Configure Client
        EdcClient client = EdcClient.builder()
                .managementApiUrl(CONNECTOR_ENDPOINT)
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

| Task                                                 | Java-Client method                                                      |
|------------------------------------------------------|-------------------------------------------------------------------------|
| Create Policy - uiAPI                   | `EdcClient.uiApi().createPolicyDefinition(policyDefinition)`            |
| Create Policy - useCaseApi (allows AND/OR/XOR operators) | `EdcClient.useCaseApi().createPolicyDefinitionUseCase(createRequest)`            |
| Create asset (Asset Creation after activate)         | `EdcClient.uiApi().createAsset(uiAssetRequest)`                         |
| Create contract definition                           | `EdcClient.uiApi().createContractDefinition(contractDefinition)`        |
| Create Offer on consumer dashboard (Catalog Browser) | `EdcClient.uiApi().getCatalogPageDataOffers(PROTOCOL_ENDPOINT)`         |
| Accept contract (Contract Negotiation)               | `EdcClient.uiApi().initiateContractNegotiation(negotiationRequest)`     |
| Transfer Data (Initiate Transfer)                    | `EdcClient.uiApi().initiateTransfer(negotiation)`                       |

These methods facilitate various operations such as creating policies, assets, contract definitions, browsing offers, accepting contracts, and initiating data transfers.

### Example Creating a Catena-Policy using operators (AND/OR/XOR)

The following example demonstrates how to create a Catena-Policy with linked conditions using the Java-client.

```java
var policyId = UUID.randomUUID().toString();
var membershipElement = buildAtomicElement("Membership", OperatorDto.EQ, "active");
var purposeElement = buildAtomicElement("PURPOSE", OperatorDto.EQ, "ID 3.1 Trace");
var andElement = new Expression()
        .expressionType(ExpressionTypeDto.AND)
        .expressions(List.of(membershipElement, purposeElement));
var permissionDto = new PermissionDto(andElement);
var createRequest = new PolicyCreateRequest(policyId, permissionDto);

var response = client.useCaseApi().createPolicyDefinitionUseCase(createRequest);

private Expression buildAtomicElement(
        String left,
        OperatorDto operator,
        String right) {
    var atomicConstraint = new AtomicConstraintDto()
            .leftExpression(left)
            .operator(operator)
            .rightExpression(right);
    return new Expression()
            .expressionType(ExpressionTypeDto.ATOMIC_CONSTRAINT)
            .atomicConstraint(atomicConstraint);
}
```

The complete example can be seen in [this test](https://github.com/sovity/edc-extensions/blob/main/extensions/wrapper/wrapper/src/test/java/de/sovity/edc/ext/wrapper/api/usecase/PolicyDefinitionApiServiceTest.java).

## License

Apache License 2.0 - see [LICENSE](../../../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
