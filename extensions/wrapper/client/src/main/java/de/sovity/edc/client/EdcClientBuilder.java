package de.sovity.edc.client;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class EdcClientBuilder {
    private String dataManagementUrl;
    private String dataManagementApiKey = "ApiKeyDefaultValue";

    public EdcClient build() {
        return EdcClientFactory.newClient(this);
    }
}
