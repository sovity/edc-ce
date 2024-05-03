<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-broker-server-extension">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">Broker Server API Java Client Library</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-broker-server-extension/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-broker-server-extension/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>


## About this component

Java API Client Library to be imported and used in arbitrary applications like use-case backends.

An example project using this client can be found [here](../client-example).

## Installation

```xml
<!-- Requires the GitHub Maven Registry -->
<dependency>
  <groupId>de.sovity.broker</groupId>
  <artifactId>client</artifactId>
  <version>${sovity-edc-broker-server-extension.version}</version>
</dependency>
```

## Usage Example

```java
import de.sovity.edc.ext.brokerserver.client.BrokerServerClient;
import de.sovity.edc.ext.brokerserver.client.gen.model.CatalogPageQuery;
import de.sovity.edc.ext.brokerserver.client.gen.model.CatalogPageResult;

/**
 * Example using the Broker Server API Java Client Library
 */
public class BrokerServerClientExample {

    public static final String BROKER_MANAGEMENT_API_URL = "http://localhost:11002/api/v1/management";
    public static final String BROKER_MANAGEMENT_API_KEY = "...";

    public static void main(String[] args) {
        // Configure Client
        BrokerServerClient client = BrokerServerClient.builder()
                .managementApiUrl(BROKER_MANAGEMENT_API_URL)
                .managementApiKey(BROKER_MANAGEMENT_API_KEY)
                .build();

        // EDC API Wrapper APIs are now available for use
        CatalogPageQuery catalogPageQuery = new CatalogPageQuery();
        CatalogPageResult catalogPageResult = client.brokerServerApi().catalogPage(catalogPageQuery);
        System.out.println(catalogPageResult.getDataOffers());
    }
}
```

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
