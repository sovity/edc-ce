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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.InitiateCustomTransferRequest;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateDto;
import de.sovity.edc.client.gen.model.TransferHistoryEntry;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiContractOffer;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataOffer;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.client.gen.model.UiPolicyExpression;
import de.sovity.edc.client.gen.model.UiPolicyExpressionType;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.extension.Consumer;
import de.sovity.edc.extension.e2e.extension.E2eScenario;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.val;
import okhttp3.HttpUrl;
import org.awaitility.Awaitility;
import org.eclipse.edc.protocol.dsp.spi.types.HttpMessageProtocol;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import static de.sovity.edc.client.gen.model.TransferProcessSimplifiedState.OK;
import static de.sovity.edc.extension.e2e.extension.Helpers.defaultE2eTestExtension;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.BODY;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.MEDIA_TYPE;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.METHOD;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.PATH;
import static org.eclipse.edc.connector.dataplane.spi.schema.DataFlowRequestSchema.QUERY_PARAMS;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.mockserver.matchers.Times.once;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.stop.Stop.stopQuietly;


class DataSourceParameterizationTest {

    @RegisterExtension
    private static E2eTestExtension e2eTestExtension = defaultE2eTestExtension();

    private final int port = getFreePort();
    private final String sourcePath = "/source/some/path/";
    private final String destinationPath = "/destination/some/path/";
    private ClientAndServer mockServer;

    private static final AtomicInteger DATA_OFFER_INDEX = new AtomicInteger(0);

    record TestCase(
        String name,
        String id,
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

    @Test
    void canUseTheWorkaroundInCustomTransferRequest(
        E2eScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider ConnectorConfig providerConfig,
        @Provider EdcClient providerClient
    ) {
        // arrange
        val testCase = new TestCase(
            "",
            "data-offer-" + DATA_OFFER_INDEX.getAndIncrement(),
            HttpMethod.PATCH,
            "[]",
            "application/json",
            "my-endpoint",
            Map.of("filter", List.of("a", "b", "c"))
        );
        val received = new AtomicBoolean(false);
        withMockServer((mockServer, context) -> {
            prepareDataTransferBackends(testCase, () -> received.set(true), mockServer);

            createData(providerClient, testCase, context);

            // act
            val providerEndpoint = providerConfig.getProtocolEndpoint().getUri().toString();
            val dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(providerEndpoint);
            val startNegotiation = initiateNegotiation(consumerClient, dataOffers.get(0), dataOffers.get(0).getContractOffers().get(0));
            val negotiation = awaitNegotiationDone(consumerClient, startNegotiation.getContractNegotiationId());

            val standardBase = "https://w3id.org/edc/v0.0.1/ns/";
            val workaroundBase = "https://sovity.de/workaround/proxy/param/";
            var transferRequestJsonLd = Json.createObjectBuilder()
                .add(
                    Prop.Edc.DATA_DESTINATION,
                    Json.createObjectBuilder(Map.of(
                        standardBase + "type", "HttpData",
                        standardBase + "baseUrl", context.destinationUrl,
                        standardBase + "method", "PUT",
                        workaroundBase + "pathSegments", testCase.path,
                        workaroundBase + "method", testCase.method,
                        workaroundBase + "queryParams", "filter=a&filter=b&filter=c",
                        workaroundBase + "mediaType", testCase.mediaType,
                        workaroundBase + "body", testCase.body
                    )).build()
                )
                .add(Prop.Edc.CTX + "transferType", Json.createObjectBuilder()
                    .add(Prop.Edc.CTX + "contentType", "application/octet-stream")
                    .add(Prop.Edc.CTX + "isFinite", true)
                )
                .add(Prop.Edc.CTX + "protocol", HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
                .add(Prop.Edc.CTX + "managedResources", false)
                .build();
            var transferRequest = InitiateCustomTransferRequest.builder()
                .contractAgreementId(negotiation.getContractAgreementId())
                .transferProcessRequestJsonLd(JsonUtils.toJson(transferRequestJsonLd))
                .build();

            val transferId = scenario.transferAndAwait(transferRequest);

            // assert
            val actual = consumerClient.uiApi().getTransferHistoryPage().getTransferEntries().get(0);
            assertThat(actual.getAssetId()).isEqualTo(testCase.id);
            assertThat(actual.getTransferProcessId()).isEqualTo(transferId);
            assertThat(actual.getState().getSimplifiedState()).isEqualTo(OK);

            assertThat(received.get()).isTrue();
        });
    }

    private record Context(
        int port,
        String sourceUrl,
        String destinationUrl
    ) {
    }

    private void withMockServer(BiConsumer<ClientAndServer, Context> consumer) {
        int port = getFreePort();
        val mockServer = ClientAndServer.startClientAndServer(port);

        val sourceUrl = "http://localhost:" + port + sourcePath;
        val destinationUrl = "http://localhost:" + port + destinationPath;

        consumer.accept(mockServer, new Context(port, sourceUrl, destinationUrl));
        stopQuietly(mockServer);
    }

    private void createData(EdcClient providerClient, TestCase testCase, Context context) {
        createPolicy(providerClient, testCase);
        createAssetWithParameterizedMethod(providerClient, testCase, context);
        createContractDefinition(providerClient, testCase);
    }

    @Test
    void sendWithEdcManagementApi(
        E2eScenario scenario,
        @Consumer ConnectorRemote consumerConnector,
        @Consumer EdcClient consumerClient,
        @Provider ConnectorConfig providerConfig,
        @Provider EdcClient providerClient
    ) {
        // arrange
        val testCase = new TestCase(
            "",
            "data-offer-" + DATA_OFFER_INDEX.getAndIncrement(),
            HttpMethod.PATCH,
            "[]",
            "application/json",
            "my-endpoint",
            Map.of("filter", List.of("a", "b", "c"))
        );
        val received = new AtomicBoolean(false);
        withMockServer((mockServer, context) -> {
            prepareDataTransferBackends(testCase, () -> received.set(true), mockServer);

            createData(providerClient, testCase, context);

            // act
            val providerEndpoint = providerConfig.getProtocolEndpoint().getUri().toString();
            val dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(providerEndpoint);
            val startNegotiation = initiateNegotiation(consumerClient, dataOffers.get(0), dataOffers.get(0).getContractOffers().get(0));
            val negotiation = awaitNegotiationDone(consumerClient, startNegotiation.getContractNegotiationId());

            String workaroundBase = "https://sovity.de/workaround/proxy/param/";
            String standardBase = "https://w3id.org/edc/v0.0.1/ns/";
            val transferId = consumerConnector.initiateTransfer(
                negotiation.getContractAgreementId(),
                testCase.id,
                URI.create(providerEndpoint),
                Json.createObjectBuilder(Map.of(
                    standardBase + "type", "HttpData",
                    standardBase + "baseUrl", context.destinationUrl,
                    standardBase + "method", "PUT",
                    workaroundBase + "pathSegments", testCase.path,
                    workaroundBase + "method", testCase.method,
                    workaroundBase + "queryParams", "filter=a&filter=b&filter=c",
                    workaroundBase + "mediaType", testCase.mediaType,
                    workaroundBase + "body", testCase.body
                )).build()
            );

            scenario.awaitTransferCompletion(transferId);

            // assert
            TransferHistoryEntry actual = consumerClient.uiApi().getTransferHistoryPage().getTransferEntries().get(0);
            assertThat(actual.getAssetId()).isEqualTo(testCase.id);
            assertThat(actual.getTransferProcessId()).isEqualTo(transferId);
            assertThat(actual.getState().getSimplifiedState()).isEqualTo(OK);

            assertThat(received.get()).isTrue();
        });
    }

    @DisabledOnGithub
    @Test
    void canTransferParameterizedAsset(
        E2eScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider ConnectorConfig providerConfig,
        @Provider EdcClient providerClient) {

        source().parallel().forEach(testCase -> {
            // arrange
            val received = new AtomicBoolean(false);
            withMockServer((mockServer, context) -> {
                prepareDataTransferBackends(testCase, () -> received.set(true), mockServer);

                createData(providerClient, testCase, context);

                // act
                val connectorEndpoint = providerConfig.getProtocolEndpoint().getUri().toString();
                val dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(connectorEndpoint);
                val dataOffer = dataOffers.stream().filter(it -> it.getAsset().getAssetId().equals(testCase.id)).findFirst().get();
                val negotiationInit = initiateNegotiation(consumerClient, dataOffer, dataOffer.getContractOffers().get(0));
                val negotiation = awaitNegotiationDone(consumerClient, negotiationInit.getContractNegotiationId());
                val transferId = initiateTransferWithParameters(consumerClient, negotiation, testCase, context);

                scenario.awaitTransferCompletion(transferId);

                // assert
                TransferHistoryEntry actual = consumerClient.uiApi()
                    .getTransferHistoryPage()
                    .getTransferEntries()
                    .stream()
                    .filter(it -> it.getAssetId().equals(testCase.id))
                    .findFirst()
                    .get();
                assertThat(actual.getAssetId()).isEqualTo(testCase.id);
                assertThat(actual.getTransferProcessId()).isEqualTo(transferId);
                assertThat(actual.getState().getSimplifiedState()).isEqualTo(OK);

                assertThat(received.get()).isTrue();
            });
        });
    }

    private Stream<TestCase> source() {
        val httpMethods = List.of(
            HttpMethod.POST,
            // HttpMethod.HEAD,
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
                            "data-offer-" + DATA_OFFER_INDEX.getAndIncrement(),
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

    private void prepareDataTransferBackends(TestCase testCase, Runnable onRequestReceived, ClientAndServer mockServer) {
        String payload = generateRandomPayload();


        val requestDefinition = request(sourcePath).withMethod(testCase.method);
        if (testCase.body != null) {
            requestDefinition.withBody(testCase.body);
        }
        if (testCase.path != null) {
            requestDefinition.withPath(sourcePath + testCase.path);
        }
        if (testCase.mediaType != null) {
            requestDefinition.withHeader(HttpHeaders.CONTENT_TYPE, testCase.mediaType);
        }
        if (testCase.queryParams != null) {
            requestDefinition.withQueryStringParameters(testCase.queryParams);
        }


        mockServer.when(requestDefinition, once())
            .respond((it) -> new HttpResponse()
                .withStatusCode(HttpStatusCode.OK_200.code())
                .withBody(payload, StandardCharsets.UTF_8));

        mockServer.when(request(destinationPath).withMethod(HttpMethod.PUT))
            .respond((HttpRequest httpRequest) -> {
                if (new String(httpRequest.getBodyAsRawBytes()).equals(payload)) {
                    onRequestReceived.run();
                }
                return new HttpResponse().withStatusCode(200);
            });

        mockServer.when(request("/.*"))
            .respond((HttpRequest httpRequest) -> {
                fail("Unexpected network call");
                return new HttpResponse().withStatusCode(HttpStatusCode.GONE_410.code());
            });
    }

    private static String generateRandomPayload() {
        byte[] data = new byte[10];
        new Random().nextBytes(data);
        return Base64.getEncoder().encodeToString(data);
    }

    private String createAssetWithParameterizedMethod(EdcClient providerClient, TestCase testCase, Context context) {
        var httpData = new UiDataSourceHttpData();
        httpData.setBaseUrl(context.sourceUrl);
        if (testCase.path != null) {
            httpData.setEnablePathParameterization(true);
        }
        if (testCase.body != null) {
            httpData.setEnableBodyParameterization(true);
        }
        if (testCase.method != null) {
            httpData.setEnableMethodParameterization(true);
        }
        if (testCase.queryParams != null) {
            httpData.setEnableQueryParameterization(true);
        }

        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(httpData)
            .build();

        var asset = UiAssetCreateRequest.builder()
            .id(testCase.id)
            .title("My Data Offer")
            .dataSource(dataSource)
            .build();

        return providerClient.uiApi().createAsset(asset).getId();
    }

    private void createPolicy(EdcClient providerClient, TestCase testCase) {
        var policyDefinition = PolicyDefinitionCreateDto.builder()
            .policyDefinitionId(testCase.id)
            .expression(UiPolicyExpression.builder()
                .type(UiPolicyExpressionType.EMPTY)
                .build())
            .build();

        providerClient.uiApi().createPolicyDefinitionV2(policyDefinition);
    }

    private String createContractDefinition(EdcClient providerClient, TestCase testCase) {
        var contractDefinition = ContractDefinitionRequest.builder()
            .contractDefinitionId(testCase.id)
            .accessPolicyId(testCase.id)
            .contractPolicyId(testCase.id)
            .assetSelector(List.of(UiCriterion.builder()
                .operandLeft(Prop.Edc.ID)
                .operator(UiCriterionOperator.EQ)
                .operandRight(UiCriterionLiteral.builder()
                    .type(UiCriterionLiteralType.VALUE)
                    .value(testCase.id)
                    .build())
                .build()))
            .build();

        return providerClient.uiApi().createContractDefinition(contractDefinition).getId();
    }

    private UiContractNegotiation initiateNegotiation(EdcClient consumerClient, UiDataOffer dataOffer, UiContractOffer contractOffer) {
        var negotiationRequest = ContractNegotiationRequest.builder()
            .counterPartyAddress(dataOffer.getEndpoint())
            .counterPartyParticipantId(dataOffer.getParticipantId())
            .assetId(dataOffer.getAsset().getAssetId())
            .contractOfferId(contractOffer.getContractOfferId())
            .policyJsonLd(contractOffer.getPolicy().getPolicyJsonLd())
            .build();

        return consumerClient.uiApi().initiateContractNegotiation(negotiationRequest);
    }

    private UiContractNegotiation awaitNegotiationDone(EdcClient consumerClient, String negotiationId) {
        var negotiation = Awaitility.await().atMost(ofSeconds(10)).until(
            () -> consumerClient.uiApi().getContractNegotiation(negotiationId),
            it -> it.getState().getSimplifiedState() != ContractNegotiationSimplifiedState.IN_PROGRESS
        );

        assertThat(negotiation.getState().getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.AGREED);
        return negotiation;
    }

    private String initiateTransferWithParameters(
        EdcClient consumerClient,
        UiContractNegotiation negotiation,
        TestCase testCase,
        Context context) {

        String rootKey = "https://w3id.org/edc/v0.0.1/ns/";

        val transferProcessProperties = new HashMap<String, String>();

        var contractAgreementId = negotiation.getContractAgreementId();
        Map<String, String> dataSinkProperties = new HashMap<>();
        dataSinkProperties.put(EDC_NAMESPACE + "baseUrl", context.destinationUrl);
        dataSinkProperties.put(EDC_NAMESPACE + "method", HttpMethod.PUT);
        dataSinkProperties.put(EDC_NAMESPACE + "type", "HttpData");
        transferProcessProperties.put(rootKey + METHOD, testCase.method);

        if (testCase.body != null) {
            dataSinkProperties.put("https://w3id.org/edc/v0.0.1/ns/body", testCase.body);
            transferProcessProperties.put(rootKey + BODY, testCase.body);
            transferProcessProperties.put(rootKey + MEDIA_TYPE, testCase.mediaType);
            transferProcessProperties.put(rootKey + "contentType", testCase.mediaType);
        }

        if (testCase.path != null) {
            transferProcessProperties.put(rootKey + PATH, testCase.path);
        }

        if (!testCase.queryParams.isEmpty()) {
            HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme("http")
                .host("example.com");

            for (val multiValueParam : testCase.queryParams.entrySet()) {
                for (val singleValue : multiValueParam.getValue()) {
                    builder.addQueryParameter(multiValueParam.getKey(), singleValue);
                }
            }

            val allQueryParams = builder.build().encodedQuery();

            transferProcessProperties.put(rootKey + QUERY_PARAMS, allQueryParams);
        }

        var transferRequest = InitiateTransferRequest.builder()
            .contractAgreementId(contractAgreementId)
            .dataSinkProperties(dataSinkProperties)
            .transferProcessProperties(transferProcessProperties)
            .build();
        return consumerClient.uiApi().initiateTransfer(transferRequest).getId();
    }


}
