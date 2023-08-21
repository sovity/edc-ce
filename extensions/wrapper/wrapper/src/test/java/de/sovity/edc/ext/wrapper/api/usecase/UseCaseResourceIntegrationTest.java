package de.sovity.edc.ext.wrapper.api.usecase;

import de.sovity.edc.ext.wrapper.api.common.model.CriterionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.AssetEntryDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ConsumptionInputDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractDefinitionRequestDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.CreateOfferingDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.PolicyDefinitionRequestDto;
import jakarta.json.JsonArray;
import org.eclipse.edc.junit.extensions.EdcRuntimeExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.spi.types.domain.asset.Asset.PROPERTY_ID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class UseCaseResourceIntegrationTest {

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
                    "edc.participant.id", providerId
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
        // create new offer on provider
        var offerInput = createOfferingDto();
        given()
                .baseUri(providerManagementUrl.toString())
                .contentType(JSON)
                .body(offerInput)
                .when()
                .post("/wrapper/use-case-api/create-offer")
                .then()
                .statusCode(204);

        // start consumption process on consumer
        var consumptionInput = consumptionInputDto();
        var consumptionId = given()
                .baseUri(consumerManagementUrl.toString())
                .contentType(JSON)
                .body(consumptionInput)
                .when()
                .post("/wrapper/use-case-api/consume")
                .then()
                .statusCode(201)
                .contentType(JSON)
                .extract()
                .path("id");

        // wait until transfer has been requested
        await().atMost(45, TimeUnit.SECONDS).untilAsserted(() -> given()
                .baseUri(consumerManagementUrl.toString())
                .contentType(JSON)
                .when()
                .get("/wrapper/use-case-api/consumption/" + consumptionId)
                .then()
                .statusCode(200)
                .body("contractNegotiation", notNullValue())
                .body("transferProcess", notNullValue())
                .body("transferProcess.state", equalTo("REQUESTED")));

        // get the data request ID (= correlation ID)
        var consumerCorrelationId = given()
                .baseUri(consumerManagementUrl.toString())
                .contentType(JSON)
                .when()
                .get("/wrapper/use-case-api/consumption/" + consumptionId)
                .then()
                .statusCode(200)
                .extract().path("transferProcess.dataRequest.id");

        // query transfer processes on provider side to verify process creation
        var transferProcesses = given()
                .baseUri(providerManagementUrl.toString())
                .contentType(JSON)
                .when()
                .post("/v2/transferprocesses/request")
                .then()
                .statusCode(200)
                .extract()
                .as(JsonArray.class);

        assertThat(transferProcesses).hasSize(1);
        var transferProcess = transferProcesses.getJsonObject(0);

        // verify that processes on both sides have the same correlation ID
        var correlationId = transferProcess.getJsonString("edc:correlationId").getString();
        assertThat(correlationId).isEqualTo(consumerCorrelationId);
    }

    private CreateOfferingDto createOfferingDto() {
        return CreateOfferingDto.builder()
                .assetEntry(AssetEntryDto.builder()
                        .id(assetId)
                        .assetProperties(Map.of("name", "example asset"))
                        .dataAddressProperties(Map.of("type", "test"))
                        .build())
                .policyDefinitionRequest(PolicyDefinitionRequestDto.builder()
                        .id(policyId)
                        .policy(PolicyDto.builder()
                                .permission(PermissionDto.builder().build())
                                .build())
                        .build())
                .contractDefinitionRequest(ContractDefinitionRequestDto.builder()
                        .id(contractDefinitionId)
                        .accessPolicyId(policyId)
                        .contractPolicyId(policyId)
                        .assetsSelector(List.of(new CriterionDto(PROPERTY_ID, "=", assetId)))
                        .build())
                .build();
    }

    private ConsumptionInputDto consumptionInputDto() {
        return ConsumptionInputDto.builder()
                .connectorId(providerId)
                .connectorAddress(providerProtocolUrl.toString())
                .offerId(contractDefinitionId + ":" + assetId + ":" + randomUUID())
                .assetId(assetId)
                .policy(PolicyDto.builder()
                        .permission(PermissionDto.builder().build())
                        .build())
                .dataDestination(Map.of("type", "test"))
                .build();
    }
}
