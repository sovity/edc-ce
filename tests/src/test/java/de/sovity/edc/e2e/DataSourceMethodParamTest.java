/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.TransferHistoryEntry;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiContractOffer;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataOffer;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.val;
import okhttp3.HttpUrl;
import org.awaitility.Awaitility;
import org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import static de.sovity.edc.client.gen.model.TransferProcessSimplifiedState.OK;
import static de.sovity.edc.client.gen.model.TransferProcessSimplifiedState.RUNNING;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.mockserver.matchers.Times.once;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.stop.Stop.stopQuietly;

class DataSourceMethodParamTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    @RegisterExtension
    static final TestDatabase PROVIDER_DATABASE = TestDatabaseFactory.getTestDatabase(1);
    @RegisterExtension
    static final TestDatabase CONSUMER_DATABASE = TestDatabaseFactory.getTestDatabase(2);

    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;

    private EdcClient providerClient;
    private EdcClient consumerClient;
    private final String dataOfferId = "my-data-offer-2023-11";

    private final int port = getFreePort();
    private final String SOURCE_PATH = "/source/some/path/";
    private final String DESTINATION_PATH = "/destination/some/path/";
    private String SOURCE_URL = "http://localhost:" + port + SOURCE_PATH;
    private String DESTINATION_URL = "http://localhost:" + port + DESTINATION_PATH;
    // TODO: remove the test backend dependency?
    private ClientAndServer mockServer;

    record TestCase(
            String name,
            String method,
            @Nullable String body,
            @Nullable String mediaType,
            @Nullable String path,
            Map<String, List<String>> queryParams
    ) {
        @Override
        public String toString() {
            return name;
        }
    }

    @BeforeEach
    public void startServer() {
        mockServer = ClientAndServer.startClientAndServer(port);
    }

    @AfterEach
    public void stopServer() {
        stopQuietly(mockServer);
    }

    @BeforeEach
    void setup() {
        // set up provider EDC + Client
        var providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, 21000, PROVIDER_DATABASE);
        providerEdcContext.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

        providerClient = EdcClient.builder()
                .managementApiUrl(providerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(providerConfig.getProperties().get("edc.api.auth.key"))
                .build();

        // set up consumer EDC + Client
        var consumerConfig = forTestDatabase(CONSUMER_PARTICIPANT_ID, 23000, CONSUMER_DATABASE);
        consumerEdcContext.setConfiguration(consumerConfig.getProperties());
        consumerConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));

        consumerClient = EdcClient.builder()
                .managementApiUrl(consumerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
                .build();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("source")
    void canTransferMethodParameterizedAsset(TestCase testCase) {
        // arrange
        final var received = prepareDataTransferBackends(testCase);

        createPolicy();
        val assetId = createAssetWithParamedMethod(testCase);
        createContractDefinition();

        // act
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(getProtocolEndpoint(providerConnector));
        var negotiation = initiateNegotiation(dataOffers.get(0), dataOffers.get(0).getContractOffers().get(0));
        negotiation = awaitNegotiationDone(negotiation.getContractNegotiationId());
        val transferId = initiateTransferWithParameters(negotiation, testCase);

        Awaitility.await().atMost(consumerConnector.timeout).until(
                () -> consumerClient.uiApi()
                        .getTransferHistoryPage()
                        .getTransferEntries()
                        .stream()
                        .filter(it -> it.getTransferProcessId().equals(transferId))
                        .findFirst()
                        .map(it -> it.getState().getSimplifiedState()),
                it -> it.orElse(RUNNING) != RUNNING
        );

        // assert
        TransferHistoryEntry actual = consumerClient.uiApi().getTransferHistoryPage().getTransferEntries().get(0);
        assertThat(actual.getAssetId()).isEqualTo(assetId);
        assertThat(actual.getTransferProcessId()).isEqualTo(transferId);
        assertThat(actual.getState().getSimplifiedState()).isEqualTo(OK);

        assertThat(received.get()).isTrue();
    }

    private static Stream<TestCase> source() {
        val httpMethods = List.of(
                HttpMethod.POST,
//                HttpMethod.HEAD,
                HttpMethod.GET,
                HttpMethod.DELETE,
                HttpMethod.PUT,
                HttpMethod.PATCH,
                HttpMethod.OPTIONS
        );

        val paths = Arrays.asList(null, "different/path/segment");
        val queryParameters = List.of(
                Map.<String, List<String>>of(),
                Map.of(
                        "limit", List.of("10"),
                        "filter", List.of("a", "b", "c")
                )
        );

        return httpMethods.stream().flatMap(method ->
                getBodyOptionsFor(method).stream().flatMap(body ->
                        paths.stream().flatMap(usePath ->
                                queryParameters.stream().map(params ->
                                        new TestCase(
                                                method + " body:" + body + " path:" + usePath + " params=" + params,
                                                method,
                                                body,
                                                body == null ? null : "application/json",
                                                usePath,
                                                params
                                        )))
                ));
    }

    @NotNull
    private static List<String> getBodyOptionsFor(String method) {
        final List<String> useBodyChoices;
        val payload = "{ \"somePayload\" : \"" + method + "\" }";

        if (okhttp3.internal.http.HttpMethod.requiresRequestBody(method)) {
            useBodyChoices = List.of(payload);
        } else if (!okhttp3.internal.http.HttpMethod.permitsRequestBody(method)) {
            useBodyChoices = Collections.singletonList(null);
        } else {
            useBodyChoices = Arrays.asList(payload, null);
        }
        return useBodyChoices;
    }

    @NotNull
    private AtomicBoolean prepareDataTransferBackends(TestCase testCase) {
        String payload = generateRandomPayload();

        val requestDefinition = request(SOURCE_PATH).withMethod(testCase.method);
        if (testCase.body != null) {
            requestDefinition.withBody(testCase.body);
        }
        if (testCase.path != null) {
            requestDefinition.withPath(SOURCE_PATH + testCase.path);
        }
        if (testCase.mediaType != null) {
            requestDefinition.withHeader(HttpHeaders.CONTENT_TYPE, testCase.mediaType);
        }
        if (testCase.queryParams != null) {
            requestDefinition.withQueryStringParameters(testCase.queryParams);
        }


        mockServer.when(requestDefinition, once())
                .respond(new HttpResponse()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withBody(payload, StandardCharsets.UTF_8));

        val received = new AtomicBoolean(false);
        mockServer.when(request(DESTINATION_PATH).withMethod(HttpMethod.PUT))
                .respond((HttpRequest httpRequest) -> {
                    if (new String(httpRequest.getBodyAsRawBytes()).equals(payload)) {
                        received.set(true);
                    }
                    return new HttpResponse().withStatusCode(200);
                });

        return received;
    }

    private static String generateRandomPayload() {
        byte[] data = new byte[10];
        new Random().nextBytes(data);
        String payload = Base64.getEncoder().encodeToString(data);
        return payload;
    }

    private String createAssetWithParamedMethod(TestCase testCase) {
        /*
            "https://w3id.org/edc/v0.0.1/ns/proxyPath": "true",
            "https://w3id.org/edc/v0.0.1/ns/proxyBody": "true",
            "https://w3id.org/edc/v0.0.1/ns/proxyMethod": "true",
            "https://w3id.org/edc/v0.0.1/ns/proxyQueryParams": "true"
         */
        val proxyProperties = new HashMap<>(Map.of(
                Prop.Edc.TYPE, "HttpData",
                Prop.Edc.BASE_URL, SOURCE_URL
        ));
        if (testCase.path != null) {
            proxyProperties.put("https://w3id.org/edc/v0.0.1/ns/proxyPath", "true");
        }
        if (testCase.body != null) {
            proxyProperties.put("https://w3id.org/edc/v0.0.1/ns/proxyBody", "true");
        }
        if (testCase.method != null) {
            proxyProperties.put("https://w3id.org/edc/v0.0.1/ns/proxyMethod", "true");
        }
        if (testCase.queryParams != null) {
            proxyProperties.put("https://w3id.org/edc/v0.0.1/ns/proxyQueryParams", "true");
        }

        var asset = UiAssetCreateRequest.builder()
                .id(dataOfferId)
                .title("My Data Offer")
                .dataAddressProperties(proxyProperties)
                .build();

        return providerClient.uiApi().createAsset(asset).getId();
    }

    // TODO: extract to common
    private void createPolicy() {
        var policyDefinition = PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId(dataOfferId)
                .policy(UiPolicyCreateRequest.builder()
                        .constraints(List.of())
                        .build())
                .build();

        providerClient.uiApi().createPolicyDefinition(policyDefinition);
    }

    private void createContractDefinition() {
        var contractDefinition = ContractDefinitionRequest.builder()
                .contractDefinitionId(dataOfferId)
                .accessPolicyId(dataOfferId)
                .contractPolicyId(dataOfferId)
                .assetSelector(List.of(UiCriterion.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(UiCriterionOperator.EQ)
                        .operandRight(UiCriterionLiteral.builder()
                                .type(UiCriterionLiteralType.VALUE)
                                .value(dataOfferId)
                                .build())
                        .build()))
                .build();

        providerClient.uiApi().createContractDefinition(contractDefinition);
    }

    private UiContractNegotiation initiateNegotiation(UiDataOffer dataOffer, UiContractOffer contractOffer) {
        var negotiationRequest = ContractNegotiationRequest.builder()
                .counterPartyAddress(dataOffer.getEndpoint())
                .counterPartyParticipantId(dataOffer.getParticipantId())
                .assetId(dataOffer.getAsset().getAssetId())
                .contractOfferId(contractOffer.getContractOfferId())
                .policyJsonLd(contractOffer.getPolicy().getPolicyJsonLd())
                .build();

        return consumerClient.uiApi().initiateContractNegotiation(negotiationRequest);
    }

    private UiContractNegotiation awaitNegotiationDone(String negotiationId) {
        var negotiation = Awaitility.await().atMost(consumerConnector.timeout).until(
                () -> consumerClient.uiApi().getContractNegotiation(negotiationId),
                it -> it.getState().getSimplifiedState() != ContractNegotiationSimplifiedState.IN_PROGRESS
        );

        assertThat(negotiation.getState().getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.AGREED);
        return negotiation;
    }

    private String initiateTransferWithParameters(
            UiContractNegotiation negotiation,
            TestCase testCase) {
        /*
        {
          "@type": "https://w3id.org/edc/v0.0.1/ns/TransferRequest",
          "https://w3id.org/edc/v0.0.1/ns/assetId": "{{ASSET_ID}}",
          "https://w3id.org/edc/v0.0.1/ns/contractId": "{{CONTRACT_ID}}",
          "https://w3id.org/edc/v0.0.1/ns/connectorAddress": "https://{{PROVIDER_EDC_FQDN}}/api/dsp",
          "https://w3id.org/edc/v0.0.1/ns/connectorId": "{{PROVIDER_EDC_PARTICIPANT_ID}}",
          "https://w3id.org/edc/v0.0.1/ns/dataDestination": {
            "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
            "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}"
          },
          "https://w3id.org/edc/v0.0.1/ns/privateProperties": {
            "https://w3id.org/edc/v0.0.1/ns/pathSegments": "my-endpoint",
            "https://w3id.org/edc/v0.0.1/ns/method": "POST",
            "https://w3id.org/edc/v0.0.1/ns/queryParams": "filter=abc&limit=10",
            "https://w3id.org/edc/v0.0.1/ns/contentType": "application/json",
            "https://w3id.org/edc/v0.0.1/ns/body": "{\"myBody\": \"myValue\"}"
          },
          "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
          "https://w3id.org/edc/v0.0.1/ns/managedResources": false
        }
         */
        var contractAgreementId = negotiation.getContractAgreementId();
        Map<String, String> dataSinkProperties = new HashMap<>();
        dataSinkProperties.put(EDC_NAMESPACE + "baseUrl", DESTINATION_URL);
        dataSinkProperties.put(EDC_NAMESPACE + "method", HttpMethod.PUT);
        dataSinkProperties.put(EDC_NAMESPACE + "type", "HttpData"); // TODO: http proxy
        dataSinkProperties.put("https://sovity.de/method", testCase.method);

        Map<String, String> transferProcessProperties = new HashMap<>(Map.of(
                "https://w3id.org/edc/v0.0.1/ns/contentType", "application/json",
                "https://w3id.org/edc/v0.0.1/ns/method", testCase.method
        ));

        if (testCase.body != null) {
            dataSinkProperties.put("https://w3id.org/edc/v0.0.1/ns/body", testCase.body);
            dataSinkProperties.put("https://sovity.de/body", testCase.body);
            dataSinkProperties.put("https://sovity.de/mediaType", testCase.mediaType);

            transferProcessProperties.put("https://w3id.org/edc/v0.0.1/ns/body", testCase.body);
            transferProcessProperties.put("https://w3id.org/edc/v0.0.1/ns/contentType", testCase.mediaType);
        }

        if (testCase.path != null) {
            dataSinkProperties.put("https://sovity.de/pathSegments", testCase.path);
        }

        if (!testCase.queryParams.isEmpty()) {
            // TODO: test this encoding
            HttpUrl.Builder builder = new HttpUrl.Builder()
                    .scheme("http")
                    .host("example.com");

            for (val multiValueParam : testCase.queryParams.entrySet()) {
                for(val singleValue: multiValueParam.getValue()){
                    builder.addQueryParameter(multiValueParam.getKey(), singleValue);
                }
            }

            val allQueryParams = builder.build().encodedQuery();

            dataSinkProperties.put("https://sovity.de/" + DataFlowRequestSchema.QUERY_PARAMS, allQueryParams);
        }

        var transferRequest = InitiateTransferRequest.builder()
                .contractAgreementId(contractAgreementId)
                .dataSinkProperties(dataSinkProperties)
                .transferProcessProperties(transferProcessProperties)
                .build();
        return consumerClient.uiApi().initiateTransfer(transferRequest).getId();
    }

    private String getProtocolEndpoint(ConnectorRemote connector) {
        return connector.getConfig().getProtocolEndpoint().getUri().toString();
    }
}
