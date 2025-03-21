/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.connector.remotes.management_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.controlplane.contract.spi.ContractOfferId;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.jsonld.util.JacksonJsonLd;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.result.Failure;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiationStates.FINALIZED;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.VOCAB;
import static org.eclipse.edc.jsonld.spi.PropertyAndTypeNames.DCAT_DATASET_ATTRIBUTE;
import static org.eclipse.edc.jsonld.spi.PropertyAndTypeNames.ODRL_POLICY_ATTRIBUTE;
import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;
import static org.eclipse.edc.spi.constants.CoreConstants.EDC_PREFIX;

@SuppressWarnings("java:S5960")
@RequiredArgsConstructor
public class ManagementApiConnectorRemote {

    @Getter
    private final ManagementApiConnectorRemoteConfig config;

    private final ObjectMapper objectMapper = JacksonJsonLd.createObjectMapper();
    public final Duration timeout = Duration.ofSeconds(60);
    private final JsonLd jsonLd = new TitaniumJsonLd(new ConsoleMonitor());

    public void createAsset(String assetId, Map<String, Object> dataAddressProperties) {
        var requestBody = createObjectBuilder()
            .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
            .add("@type", Json.createValue("https://w3id.org/edc/v0.0.1/ns/Asset"))
            .add("@id", Json.createValue(assetId))
            .add("properties", createObjectBuilder()
                .add("description", "description"))
            .add("dataAddress", createObjectBuilder(dataAddressProperties))
            .build();

        prepareManagementApiCall()
            .contentType(JSON)
            .body(requestBody)
            .when()
            .post("/v3/assets")
            .then()
            .statusCode(200)
            .contentType(JSON);
    }

    public String createPolicy(JsonObject policyJsonObject) {
        var requestBody =
            createObjectBuilder()
                .add(CONTEXT, createObjectBuilder()
                    .add("edc", EDC_NAMESPACE)
                    .build())
                .add(TYPE, "edc:PolicyDefinition")
                .add("policy", policyJsonObject)
                .build();

        return prepareManagementApiCall()
            .contentType(JSON)
            .body(requestBody)
            .when()
            .post("/v3/policydefinitions")
            .then()
            .statusCode(200)
            .contentType(JSON)
            .extract().jsonPath().getString(ID);
    }

    public void createContractDefinition(
        String assetId,
        String contractDefinitionId,
        String accessPolicyId,
        String contractPolicyId
    ) {
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
            .post("/v3/contractdefinitions")
            .then()
            .statusCode(200)
            .contentType(JSON);
    }

    public JsonArray getCatalogDatasets(String providerProtocolApiUrl) {
        var datasetReference = new AtomicReference<JsonArray>();
        var requestBody = createObjectBuilder()
            .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
            .add(TYPE, EDC_NAMESPACE + "CatalogRequest")
            .add(EDC_NAMESPACE + "counterPartyAddress", providerProtocolApiUrl)
            .add(EDC_NAMESPACE + "protocol", "dataspace-protocol-http")
            .build();

        await().atMost(timeout).untilAsserted(() -> {
            var response = prepareManagementApiCall()
                .contentType(JSON)
                .when()
                .body(requestBody)
                .post("/v3/catalog/request")
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

    public JsonObject getDatasetForAsset(String assetId, String providerProtocolApiUrl) {
        var datasets = getCatalogDatasets(providerProtocolApiUrl);
        return datasets.stream()
            .map(JsonValue::asJsonObject)
            .filter(it -> assetId.equals(getDatasetContractId(it).assetIdPart()))
            .findFirst()
            .orElseThrow(() -> new EdcException("No dataset for asset %s in the catalog".formatted(assetId)));
    }

    public String negotiateContract(
        String providerParticipantId,
        String providerProtocolApiUrl,
        String offerId,
        String assetId,
        JsonObject policy
    ) {
        var requestBody = createObjectBuilder()
            .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
            .add(TYPE, "ContractRequest")
            .add("counterPartyAddress", providerProtocolApiUrl)
            .add("providerId", providerParticipantId)
            .add("policy", policy)
            .add("protocol", "dataspace-protocol-http")
            .build();

        var negotiationId = prepareManagementApiCall()
            .contentType(JSON)
            .body(requestBody)
            .when()
            .post("/v3/contractnegotiations")
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
            .get("/v3/contractnegotiations/{id}", negotiationId)
            .then()
            .statusCode(200)
            .extract().body().jsonPath()
            .getString("contractAgreementId");
    }

    public String getContractNegotiationState(String id) {
        return prepareManagementApiCall()
            .contentType(JSON)
            .when()
            .get("/v3/contractnegotiations/{id}/state", id)
            .then()
            .statusCode(200)
            .extract().body().jsonPath().getString("state");
    }

    public String getParticipantId() {
        return config.getParticipantId();
    }

    public String initiateTransfer(
        String contractAgreementId,
        String assetId,
        String providerProtocolApiUrl,
        JsonObject destination
    ) {
        var requestBody = createObjectBuilder()
            .add(CONTEXT, createObjectBuilder()
                .add(VOCAB, EDC_NAMESPACE))
            .add(TYPE, "TransferRequest")
            .add("contractId", contractAgreementId)
            .add("counterPartyAddress", providerProtocolApiUrl)
            .add("protocol", "dataspace-protocol-http")
            .add("transferType", "HttpData-PUSH")
            .add("assetId", assetId)
            .add("dataDestination", destination)
            .add("contractId", contractAgreementId)
            .add("privateProperties", createObjectBuilder().build())
            .build();

        return prepareManagementApiCall()
            .contentType(JSON)
            .body(requestBody)
            .when()
            .post("/v3/transferprocesses")
            .then()
            .statusCode(200)
            .extract().body().jsonPath().getString(ID);
    }

    public String consumeOffer(
        String providerId,
        String providerProtocolApiUrl,
        String assetId,
        JsonObject destination
    ) {
        var dataset = getDatasetForAsset(assetId, providerProtocolApiUrl);
        var contractId = getDatasetContractId(dataset);
        var policy = dataset.getJsonArray(ODRL_POLICY_ATTRIBUTE).get(0).asJsonObject();

        val editedPolicy = Json.createObjectBuilder()
            .add(CONTEXT, "http://www.w3.org/ns/odrl.jsonld")
            .add(TYPE, "odrl:Offer")
            .add(ID, policy.get(ID))
            .add("assigner", providerId)
            .add("permission", policy.get("http://www.w3.org/ns/odrl/2/permission"))
            .add("prohibition", policy.get("http://www.w3.org/ns/odrl/2/prohibition"))
            .add("obligation", policy.get("http://www.w3.org/ns/odrl/2/obligation"))
            .add("target", assetId)
            .build();

        var contractAgreementId = negotiateContract(
            providerId,
            providerProtocolApiUrl,
            contractId.toString(),
            contractId.assetIdPart(),
            editedPolicy);

        var transferProcessId = initiateTransfer(
            contractAgreementId,
            assetId,
            providerProtocolApiUrl,
            destination);

        assertThat(transferProcessId).isNotNull();
        return transferProcessId;
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
            .add(CONTEXT, "http://www.w3.org/ns/odrl.jsonld")
            .add(TYPE, "Set")
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
        var apiUrl = config.getManagementApiUrl();
        var request = given().baseUri(apiUrl);

        var header = config.getManagementApiAuthHeaderFactory().get();
        if (header != null) {
            request = request.header(new Header(header.getName(), header.getValue()));
        }

        return request;
    }

    public ContractOfferId getDatasetContractId(JsonObject dataset) {
        var id = dataset.getJsonArray(ODRL_POLICY_ATTRIBUTE).get(0).asJsonObject().getString(ID);
        return ContractOfferId.parseId(id).orElseThrow(this::throwFailure);
    }

    private RuntimeException throwFailure(Failure failure) {
        return new IllegalStateException(failure.getFailureDetail());
    }
}
