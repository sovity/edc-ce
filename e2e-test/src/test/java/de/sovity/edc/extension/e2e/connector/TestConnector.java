/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.extension.e2e.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.e2e.connector.config.DatasourceConfig;
import de.sovity.edc.extension.e2e.connector.config.EdcApiGroup;
import de.sovity.edc.extension.e2e.connector.config.EdcApiGroupConfig;
import de.sovity.edc.extension.e2e.connector.config.EdcConfig;
import de.sovity.edc.extension.e2e.connector.config.SimpleConfig;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import lombok.Builder;
import lombok.Singular;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.jsonld.util.JacksonJsonLd;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static de.sovity.edc.extension.e2e.connector.config.EdcApiGroup.MANAGEMENT;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static jakarta.json.Json.createObjectBuilder;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.FINALIZED;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.eclipse.edc.jsonld.spi.PropertyAndTypeNames.DCAT_DATASET_ATTRIBUTE;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.eclipse.edc.spi.CoreConstants.EDC_PREFIX;

@Builder
public class TestConnector implements Connector {

    private final ObjectMapper objectMapper = JacksonJsonLd.createObjectMapper();
    private final Duration timeout = Duration.ofSeconds(60);
    private final JsonLd jsonLd = new TitaniumJsonLd(new ConsoleMonitor());
    private final String participantId;

    @Singular
    private final List<DatasourceConfig> datasourceConfigs;

    @Singular
    private final List<SimpleConfig> simpleConfigs;

    private final String baseUrl;
    private final Map<EdcApiGroup, EdcApiGroupConfig> apiGroupConfigMap;

    @Override
    public void createAsset(String assetId, Map<String, Object> dataAddressProperties) {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add("asset", createObjectBuilder()
                        .add(ID, assetId)
                        .add("properties", createObjectBuilder()
                                .add("description", "description")))
                .add("dataAddress", createObjectBuilder(dataAddressProperties))
                .build();

        given()
                .baseUri(getUriForApi(MANAGEMENT).toString())
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/assets")
                .then()
                .statusCode(200)
                .contentType(JSON);
    }

    @Override
    public List<String> getAssetIds() {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(TYPE, "QuerySpecDto")
                .build();
        return given()
                .baseUri(getUriForApi(MANAGEMENT).toString())
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/assets/request")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract().jsonPath().getList("@id");
    }

    @Override
    public String createPolicy(JsonObject policyJsonObject) {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(TYPE, "PolicyDefinitionDto")
                .add("policy", policyJsonObject)
                .build();

        return given()
                .baseUri(getUriForApi(MANAGEMENT).toString())
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/policydefinitions")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract().jsonPath().getString(ID);
    }

    @Override
    public void createContractDefinition(
            String assetId,
            String contractDefinitionId,
            String accessPolicyId,
            String contractPolicyId) {
        var requestBody = createObjectBuilder()
                .add(ID, contractDefinitionId)
                .add(TYPE, EDC_NAMESPACE + "ContractDefinition")
                .add(EDC_NAMESPACE + "accessPolicyId", accessPolicyId)
                .add(EDC_NAMESPACE + "contractPolicyId", contractPolicyId)
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
                .baseUri(getUriForApi(MANAGEMENT).toString())
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/contractdefinitions")
                .then()
                .statusCode(200)
                .contentType(JSON);
    }

    public JsonArray getCatalogDatasets(URI providerProtocolEndpoint) {
        var datasetReference = new AtomicReference<JsonArray>();
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(TYPE, "CatalogRequest")
                .add("providerUrl", providerProtocolEndpoint.toString())
                .add("protocol", "dataspace-protocol-http")
                .build();

        await().atMost(timeout).untilAsserted(() -> {
            var response = given()
                    .baseUri(getUriForApi(MANAGEMENT).toString())
                    .contentType(JSON)
                    .when()
                    .body(requestBody)
                    .post("/v2/catalog/request")
                    .then()
                    .statusCode(200)
                    .extract().body().asString();

            var responseBody = objectMapper.readValue(response, JsonObject.class);

            var catalog =
                    jsonLd.expand(responseBody).orElseThrow(f -> new EdcException(f.getFailureDetail()));

            var datasets = catalog.getJsonArray(DCAT_DATASET_ATTRIBUTE);
            assertThat(datasets).hasSizeGreaterThan(0);

            datasetReference.set(datasets);
        });

        return datasetReference.get();
    }

    @Override
    public JsonObject getDatasetForAsset(String assetId, URI providerProtocolEndpoint) {
        var datasets = getCatalogDatasets(providerProtocolEndpoint);
        return datasets.stream()
                .map(JsonValue::asJsonObject)
                .filter(it -> assetId.equals(JsonLdConnectorUtil.getContractId(it).assetIdPart()))
                .findFirst()
                .orElseThrow(() -> new EdcException(format("No dataset for asset %s in the " +
                        "catalog", assetId)));
    }

    @Override
    public String negotiateContract(
            String providerParticipantId,
            URI providerProtocolEndpoint,
            String offerId,
            String assetId,
            JsonObject policy) {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(TYPE, "NegotiationInitiateRequestDto")
                .add("connectorId", providerParticipantId)
                .add("consumerId", participantId)
                .add("providerId", providerParticipantId)
                .add("connectorAddress", providerProtocolEndpoint.toString())
                .add("protocol", "dataspace-protocol-http")
                .add("offer", createObjectBuilder()
                        .add("offerId", offerId)
                        .add("assetId", assetId)
                        .add("policy", jsonLd.compact(policy).getContent())
                )
                .build();

        var negotiationId = given()
                .baseUri(getUriForApi(MANAGEMENT).toString())
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/contractnegotiations")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getString(ID);

        await().atMost(timeout).untilAsserted(() -> {
            var state = getContractNegotiationState(negotiationId);
            assertThat(state).isEqualTo(FINALIZED.name());
        });

        return getContractAgreementId(negotiationId);
    }

    public String getContractAgreementId(String negotiationId) {
        var contractAgreementId = new AtomicReference<String>();

        await().atMost(timeout).untilAsserted(() -> {
            var agreementId = getContractNegotiationField(negotiationId, "contractAgreementId");
            assertThat(agreementId).isNotNull().isInstanceOf(String.class);

            contractAgreementId.set(agreementId);
        });

        var id = contractAgreementId.get();
        assertThat(id).isNotEmpty();
        return id;
    }

    private String getContractNegotiationField(String negotiationId, String fieldName) {
        return given()
                .baseUri(getUriForApi(MANAGEMENT).toString())
                .contentType(JSON)
                .when()
                .get("/v2/contractnegotiations/{id}", negotiationId)
                .then()
                .statusCode(200)
                .extract().body().jsonPath()
                .getString(format("'edc:%s'", fieldName));
    }

    public String getContractNegotiationState(String id) {
        return given()
                .baseUri(getUriForApi(MANAGEMENT).toString())
                .contentType(JSON)
                .when()
                .get("/v2/contractnegotiations/{id}/state", id)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getString("'edc:state'");
    }


    @Override
    public URI getUriForApi(EdcApiGroup edcApiGroup) {
        return apiGroupConfigMap.get(edcApiGroup).getUri();
    }

    @Override
    public String getParticipantId() {
        return participantId;
    }

    @Override
    public String initiateTransfer(
            String contractAgreementId,
            String assetId,
            URI providerProtocolApi,
            JsonObject destination) {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(TYPE, "TransferRequestDto")
                .add("dataDestination", destination)
                .add("protocol", "dataspace-protocol-http")
                .add("managedResources", false)
                .add("assetId", assetId)
                .add("contractId", contractAgreementId)
                .add("connectorAddress", providerProtocolApi.toString())
                .add("privateProperties", Json.createObjectBuilder().build())
                .add("connectorId", participantId)
                .build();

        return given()
                .baseUri(getUriForApi(MANAGEMENT).toString())
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/transferprocesses")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getString(ID);
    }

    @Override
    public String getTransferProcessState(String id) {
        return given()
                .baseUri(getUriForApi(MANAGEMENT).toString())
                .contentType(JSON)
                .when()
                .get("/v2/transferprocesses/{id}/state", id)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getString("'edc:state'");
    }

    @Override
    public Map<String, String> getConfig() {
        var configStream = Stream.of(
                apiGroupConfigMap.values().stream().toList(),
                datasourceConfigs,
                simpleConfigs);
        return new HashMap<>() {
            {
                configStream.flatMap(List::stream)
                        .map(EdcConfig::toMap)
                        .forEach(this::putAll);
            }
        };
    }

    public static class TestConnectorBuilder {
        public TestConnectorBuilder configProperty(String key, String value) {
            this.simpleConfig(new SimpleConfig(key, value));
            return this;
        }
    }

}
