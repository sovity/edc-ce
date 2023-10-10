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
import de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfig;
import de.sovity.edc.extension.e2e.connector.config.api.auth.NoneAuthProvider;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.ContractId;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.jsonld.util.JacksonJsonLd;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.result.Failure;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.FINALIZED;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.eclipse.edc.jsonld.spi.PropertyAndTypeNames.DCAT_DATASET_ATTRIBUTE;
import static org.eclipse.edc.jsonld.spi.PropertyAndTypeNames.ODRL_POLICY_ATTRIBUTE;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.eclipse.edc.spi.CoreConstants.EDC_PREFIX;

@SuppressWarnings("java:S5960")
@RequiredArgsConstructor
public class ConnectorRemote {
    @Getter

    private final ConnectorRemoteConfig config;

    private final ObjectMapper objectMapper = JacksonJsonLd.createObjectMapper();
    public final Duration timeout = Duration.ofSeconds(60);
    private final JsonLd jsonLd = new TitaniumJsonLd(new ConsoleMonitor());

    public void createAsset(String assetId, Map<String, Object> dataAddressProperties) {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add("asset", createObjectBuilder()
                        .add(ID, assetId)
                        .add("properties", createObjectBuilder()
                                .add("description", "description")))
                .add("dataAddress", createObjectBuilder(dataAddressProperties))
                .build();

        prepareManagementApiCall()
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/assets")
                .then()
                .statusCode(200)
                .contentType(JSON);
    }

    public List<String> getAssetIds() {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(TYPE, EDC_NAMESPACE + "QuerySpec")
                .build();
        return prepareManagementApiCall()
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/assets/request")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract().jsonPath().getList("@id");
    }

    public String createPolicy(JsonObject policyJsonObject) {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(TYPE, EDC_NAMESPACE + "PolicyDefinition")
                .add(EDC_NAMESPACE + "policy", policyJsonObject)
                .build();

        return prepareManagementApiCall()
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/policydefinitions")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract().jsonPath().getString(ID);
    }

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

        prepareManagementApiCall()
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
                .add(TYPE, EDC_NAMESPACE + "CatalogRequest")
                .add(EDC_NAMESPACE + "counterPartyAddress", providerProtocolEndpoint.toString())
                .add(EDC_NAMESPACE + "protocol", "dataspace-protocol-http")
                .build();

        await().atMost(timeout).untilAsserted(() -> {
            var response = prepareManagementApiCall()
                    .contentType(JSON)
                    .when()
                    .body(requestBody)
                    .post("/v2/catalog/request")
                    .then()
                    .statusCode(200)
                    .extract().body().asString();

            var responseBody = objectMapper.readValue(response, JsonObject.class);

            var catalog = jsonLd.expand(responseBody).orElseThrow(this::throwFailure);

            var datasets = catalog.getJsonArray(DCAT_DATASET_ATTRIBUTE);
            assertThat(datasets).isNotEmpty();

            datasetReference.set(datasets);
        });

        return datasetReference.get();
    }

    public JsonObject getDatasetForAsset(String assetId, URI providerProtocolEndpoint) {
        var datasets = getCatalogDatasets(providerProtocolEndpoint);
        return datasets.stream()
                .map(JsonValue::asJsonObject)
                .filter(it -> assetId.equals(getDatasetContractId(it).assetIdPart()))
                .findFirst()
                .orElseThrow(() -> new EdcException("No dataset for asset %s in the catalog".formatted(assetId)));
    }

    public String negotiateContract(
            String providerParticipantId,
            URI providerProtocolEndpoint,
            String offerId,
            String assetId,
            JsonObject policy) {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(TYPE, EDC_NAMESPACE + "ContractRequest")
                .add(EDC_NAMESPACE + "consumerId", config.getParticipantId())
                .add(EDC_NAMESPACE + "providerId", providerParticipantId)
                .add(EDC_NAMESPACE + "connectorAddress", providerProtocolEndpoint.toString())
                .add(EDC_NAMESPACE + "protocol", "dataspace-protocol-http")
                .add(EDC_NAMESPACE + "offer", createObjectBuilder()
                        .add(EDC_NAMESPACE + "offerId", offerId)
                        .add(EDC_NAMESPACE + "assetId", assetId)
                        .add(EDC_NAMESPACE + "policy", jsonLd.compact(policy).orElseThrow(this::throwFailure))
                )
                .build();

        var negotiationId = prepareManagementApiCall()
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
            var agreementId = getContractNegotiationField(negotiationId);
            assertThat(agreementId).isNotNull().isInstanceOf(String.class);

            contractAgreementId.set(agreementId);
        });

        var id = contractAgreementId.get();
        assertThat(id).isNotEmpty();
        return id;
    }

    private String getContractNegotiationField(String negotiationId) {
        return prepareManagementApiCall()
                .contentType(JSON)
                .when()
                .get("/v2/contractnegotiations/{id}", negotiationId)
                .then()
                .statusCode(200)
                .extract().body().jsonPath()
                .getString("'edc:contractAgreementId'");
    }

    public String getContractNegotiationState(String id) {
        return prepareManagementApiCall()
                .contentType(JSON)
                .when()
                .get("/v2/contractnegotiations/{id}/state", id)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getString("'edc:state'");
    }

    public String getParticipantId() {
        return config.getParticipantId();
    }

    public String initiateTransfer(
            String contractAgreementId,
            String assetId,
            URI providerProtocolApi,
            JsonObject destination) {
        var requestBody = createObjectBuilder()
                .add(TYPE, EDC_NAMESPACE + "TransferRequest")
                .add(EDC_NAMESPACE + "protocol", "dataspace-protocol-http")
                .add(EDC_NAMESPACE + "connectorAddress", providerProtocolApi.toString())
                .add(EDC_NAMESPACE + "connectorId", config.getParticipantId())
                .add(EDC_NAMESPACE + "assetId", assetId)
                .add(EDC_NAMESPACE + "dataDestination", destination)
                .add(EDC_NAMESPACE + "contractId", contractAgreementId)
                .add(EDC_NAMESPACE + "privateProperties", Json.createObjectBuilder().build())
                .add(EDC_NAMESPACE + "managedResources", false)
                .build();

        return prepareManagementApiCall()
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/transferprocesses")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getString(ID);
    }

    public String consumeOffer(
            String providerId,
            URI providerProtocolApi,
            String assetId,
            JsonObject destination) {
        var dataset = getDatasetForAsset(assetId, providerProtocolApi);
        var contractId = getDatasetContractId(dataset);
        var policy = dataset.getJsonArray(ODRL_POLICY_ATTRIBUTE).get(0).asJsonObject();

        var contractAgreementId = negotiateContract(
                providerId,
                providerProtocolApi,
                contractId.toString(),
                contractId.assetIdPart(),
                policy);

        var transferProcessId = initiateTransfer(
                contractAgreementId,
                assetId,
                providerProtocolApi,
                destination);

        assertThat(transferProcessId).isNotNull();
        return transferProcessId;
    }

    public String getTransferProcessState(String id) {
        return prepareManagementApiCall()
                .contentType(JSON)
                .when()
                .get("/v2/transferprocesses/{id}/state", id)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getString("'edc:state'");
    }

    public void createDataOffer(
            String assetId,
            String targetUrl
    ) {
        Map<String, Object> dataSource = Map.of(
                EDC_NAMESPACE + "type", "HttpData",
                EDC_NAMESPACE + "baseUrl", targetUrl,
                EDC_NAMESPACE + "proxyQueryParams", "true"
        );

        var policy = createObjectBuilder()
                .add(TYPE, "use")
                .build();

        var contractDefinitionId = UUID.randomUUID().toString();
        createAsset(assetId, dataSource);
        var noConstraintPolicyId = createPolicy(policy);
        createContractDefinition(
                assetId,
                contractDefinitionId,
                noConstraintPolicyId,
                noConstraintPolicyId);
    }

    public RequestSpecification prepareManagementApiCall() {
        var managementConfig = config.getManagementEndpoint();
        var managementBaseUri = managementConfig.getUri().toString();
        if (managementConfig.authProvider() instanceof NoneAuthProvider) {
            return given().baseUri(managementBaseUri);
        }
        return given()
                .baseUri(managementBaseUri)
                .header(getAuthHeader());
    }

    private Header getAuthHeader() {
        var authProvider = config.getManagementEndpoint().authProvider();
        if ("".equals(authProvider.getAuthorizationHeader())) {
            return null;
        }
        return new Header(
                authProvider.getAuthorizationHeader(),
                authProvider.getAuthorizationHeaderValue());
    }


    public ContractId getDatasetContractId(JsonObject dataset) {
        var id = dataset.getJsonArray(ODRL_POLICY_ATTRIBUTE).get(0).asJsonObject().getString(ID);
        return ContractId.parseId(id).orElseThrow(this::throwFailure);
    }

    private RuntimeException throwFailure(Failure failure) {
        return new IllegalStateException(failure.getFailureDetail());
    }
}
