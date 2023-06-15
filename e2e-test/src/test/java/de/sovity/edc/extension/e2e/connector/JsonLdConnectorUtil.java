package de.sovity.edc.extension.e2e.connector;

import jakarta.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.contract.spi.ContractId;

import static jakarta.json.Json.createObjectBuilder;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.eclipse.edc.jsonld.spi.PropertyAndTypeNames.ODRL_POLICY_ATTRIBUTE;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonLdConnectorUtil {

    public static ContractId getContractId(JsonObject dataset) {
        var id = dataset.getJsonArray(ODRL_POLICY_ATTRIBUTE).get(0).asJsonObject().getString(ID);
        return ContractId.parse(id);
    }


    public static JsonObject httpDataAddress(String baseUrl) {
        return createObjectBuilder()
                .add(TYPE, EDC_NAMESPACE + "DataAddress")
                .add(EDC_NAMESPACE + "type", "HttpData")
                .add(EDC_NAMESPACE + "properties", createObjectBuilder()
                        .add(EDC_NAMESPACE + "baseUrl", baseUrl)
                        .build())
                .build();
    }
}
