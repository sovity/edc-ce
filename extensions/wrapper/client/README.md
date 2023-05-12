<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />API Wrapper &amp; API Client:<br />Java API Client</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this component

Java API Client Library to be imported and used in arbitrary applications like use-case backends.

An example project using this client can be found [here](../client-example).

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

### Example Using API Key Auth

```java
import de.sovity.edc.client.EdcClient;

// Example using our Community Edition EDC
EdcClient client = EdcClient.builder()
        .managementApiUrl("http://localhost:11002/api/v1/management")
        .managementApiKey("ApiKeyDefaultValue")
        .build();

KpiResult kpiResult = client.useCaseApi().kpiEndpoint();
```

### Example Using OAuth2 Client Credentials

```java
import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.KpiResult;
import de.sovity.edc.client.oauth2.OAuth2ClientCredentials;
import de.sovity.edc.client.oauth2.SovityKeycloakUrl;

public class Wrapper {

    public static final String CONNECTOR_ENDPOINT =
            "https://{{your-connector}}.prod-sovity.azure.sovity.io/control/data";
    public static final String CLIENT_ID = "my-edc-app";
    public static final String CLIENT_SECRET = "...";

    public Wrapper() {
        // Example using a productive Connector-as-a-Service (CaaS) EDC Connector
        EdcClient client = EdcClient.builder()
                .managementApiUrl(CONNECTOR_ENDPOINT)
                .oauth2ClientCredentials(OAuth2ClientCredentials.builder()
                        .tokenUrl(SovityKeycloakUrl.PRODUCTION)
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .build())
                .build();
        KpiResult kpiResult = client.useCaseApi().kpiEndpoint();
        System.out.println(kpiResult);
    }
}
```

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
