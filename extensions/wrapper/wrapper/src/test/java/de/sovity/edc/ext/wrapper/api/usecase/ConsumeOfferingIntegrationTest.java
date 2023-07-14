package de.sovity.edc.ext.wrapper.api.usecase;

import de.sovity.edc.ext.wrapper.api.usecase.model.ConsumeInputDto;
import jakarta.json.Json;
import org.eclipse.edc.junit.extensions.EdcRuntimeExtension;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static jakarta.json.Json.createArrayBuilder;
import static jakarta.json.Json.createObjectBuilder;
import static java.util.UUID.randomUUID;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.eclipse.edc.policy.model.OdrlNamespace.ODRL_SCHEMA;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.eclipse.edc.spi.CoreConstants.EDC_PREFIX;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class ConsumeOfferingIntegrationTest {

    private static final String defaultApiPath = "/api";
    private static final String managementApiPath = "/api/management";
    private static final String protocolApiPath = "/api/protocol";

    private static final URI providerDefaultUrl = URI.create("http://localhost:8080" + defaultApiPath);
    private static final URI providerManagementUrl = URI.create("http://localhost:8081" + managementApiPath);
    private static final URI providerProtocolUrl = URI.create("http://localhost:8082" + protocolApiPath);
    private static final URI consumerDefaultUrl = URI.create("http://localhost:9090" + defaultApiPath);
    private static final URI consumerManagementUrl = URI.create("http://localhost:9091" + managementApiPath);
    private static final URI consumerProtocolUrl = URI.create("http://localhost:9092" + protocolApiPath);

    private static final String assetId = "assetId";
    private static final String policyId = "policyId";
    private static final String contractDefinitionId = "contractDefinitionId";
    private static final String policyActionType = "use";
    private static final String providerId = "provider";
    private static final String consumerId = "consumer";

    @RegisterExtension
    static EdcRuntimeExtension provider = new EdcRuntimeExtension(
            ":extensions:wrapper:wrapper",
            "provider",
            Map.of(
                    "web.http.port", String.valueOf(providerDefaultUrl.getPort()),
                    "web.http.path", defaultApiPath,
                    "web.http.management.port", String.valueOf(providerManagementUrl.getPort()),
                    "web.http.management.path", managementApiPath,
                    "web.http.protocol.port", String.valueOf(providerProtocolUrl.getPort()),
                    "web.http.protocol.path", protocolApiPath,
                    "edc.dsp.callback.address", providerProtocolUrl.toString(),
                    "edc.connector.name", providerId,
                    "edc.participant.id", providerId,
                    "edc.jsonld.http.enabled", "true"
            )
    );

    @RegisterExtension
    static EdcRuntimeExtension consumer = new EdcRuntimeExtension(
            ":extensions:wrapper:wrapper",
            "consumer",
            Map.of(
                    "web.http.port", String.valueOf(consumerDefaultUrl.getPort()),
                    "web.http.path", defaultApiPath,
                    "web.http.management.port", String.valueOf(consumerManagementUrl.getPort()),
                    "web.http.management.path", managementApiPath,
                    "web.http.protocol.port", String.valueOf(consumerProtocolUrl.getPort()),
                    "web.http.protocol.path", protocolApiPath,
                    "edc.dsp.callback.address", consumerProtocolUrl.toString(),
                    "edc.connector.name", consumerId,
                    "edc.participant.id", consumerId
            )
    );

    @Test
    void consumeOffering() {
        createAsset();
        createPolicyDefinition();
        createContractDefinition();

        var input = ConsumeInputDto.builder()
                .connectorId(providerId)
                .connectorAddress(providerProtocolUrl.toString())
                .offerId(contractDefinitionId + ":" + assetId + ":" + randomUUID())
                .assetId(assetId)
                .policy(Policy.Builder.newInstance()
                        .permission(Permission.Builder.newInstance()
                                .action(Action.Builder.newInstance()
                                        .type(ODRL_SCHEMA + policyActionType)
                                        .build())
                                .target(assetId)
                                .build())
                        .target(assetId)
                        .build())
                .dataDestination(DataAddress.Builder.newInstance()
                        .type("test")
                        .build())
                .build();

        var consumptionId = given()
                .baseUri(consumerManagementUrl.toString())
                .contentType(JSON)
                .body(input)
                .when()
                .post("/wrapper/use-case-api/consume")
                .then()
                .statusCode(201)
                .contentType(JSON)
                .extract()
                .path("id");

        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> given()
                .baseUri(consumerManagementUrl.toString())
                .contentType(JSON)
                .when()
                .get("/wrapper/use-case-api/consumption/" + consumptionId)
                .then()
                .statusCode(200)
                .body("contractNegotiation", notNullValue())
                .body("transferProcess", notNullValue())
                .body("transferProcess.state", equalTo("TERMINATED"))); // terminated by provider as unsupported data address type
    }

    private void createAsset() {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder()
                        .add(EDC_PREFIX, EDC_NAMESPACE))
                .add("asset", createObjectBuilder()
                        .add(ID, assetId)
                        .add("properties", createObjectBuilder()
                                .add("description", "test asset")))
                .add("dataAddress", createObjectBuilder(Map.of("type", "test")))
                .build();

        given()
                .baseUri(providerManagementUrl.toString())
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/assets")
                .then()
                .statusCode(200)
                .contentType(JSON);
    }

    private void createPolicyDefinition() {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder()
                        .add(EDC_PREFIX, EDC_NAMESPACE))
                .add(TYPE, "PolicyDefinitionDto")
                .add(ID, policyId)
                .add("policy", createObjectBuilder()
                        .add(CONTEXT, "http://www.w3.org/ns/odrl.jsonld")
                        .add("permission", createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("action", policyActionType))))
                .build();

        given()
                .baseUri(providerManagementUrl.toString())
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/policydefinitions")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract().jsonPath().getString(ID);
    }

    private void createContractDefinition() {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(ID, contractDefinitionId)
                .add(TYPE, EDC_NAMESPACE + "ContractDefinition")
                .add(EDC_NAMESPACE + "accessPolicyId", policyId)
                .add(EDC_NAMESPACE + "contractPolicyId", policyId)
                .add(EDC_NAMESPACE + "assetsSelector", Json.createArrayBuilder()
                        .add(createObjectBuilder()
                                .add(TYPE, "CriterionDto")
                                .add(EDC_NAMESPACE + "operandLeft", EDC_NAMESPACE + "id")
                                .add(EDC_NAMESPACE + "operator", "=")
                                .add(EDC_NAMESPACE + "operandRight", assetId)
                                .build())
                        .build())
                .build();

        given()
                .baseUri(providerManagementUrl.toString())
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/contractdefinitions")
                .then()
                .statusCode(200)
                .contentType(JSON);
    }
}
