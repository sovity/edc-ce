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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.ApiException;
import de.sovity.edc.client.gen.model.ContractAgreementPageQuery;
import de.sovity.edc.client.gen.model.ContractDefinitionEntry;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.DataOfferCreationRequest;
import de.sovity.edc.client.gen.model.DataSourceAvailability;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.InitiateCustomTransferRequest;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionDto;
import de.sovity.edc.client.gen.model.TransferProcessSimplifiedState;
import de.sovity.edc.client.gen.model.UiAsset;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetEditRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiContractOffer;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataOffer;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.client.gen.model.UiDataSourceHttpDataMethod;
import de.sovity.edc.client.gen.model.UiDataSourceOnRequest;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyExpression;
import de.sovity.edc.client.gen.model.UiPolicyExpressionType;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenario;
import de.sovity.edc.extension.e2e.connector.remotes.management_api.ManagementApiConnectorRemote;
import de.sovity.edc.extension.e2e.connector.remotes.test_backend_controller.TestBackendRemote;
import de.sovity.edc.extension.e2e.junit.CeE2eTestExtension;
import de.sovity.edc.extension.e2e.junit.CeE2eTestSide;
import de.sovity.edc.extension.e2e.junit.utils.Consumer;
import de.sovity.edc.extension.e2e.junit.utils.Provider;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.config.ConfigUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.val;
import org.awaitility.Awaitility;
import org.eclipse.edc.protocol.dsp.http.spi.types.HttpMessageProtocol;
import org.eclipse.edc.spi.system.configuration.Config;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static de.sovity.edc.client.gen.model.ContractAgreementDirection.CONSUMING;
import static de.sovity.edc.client.gen.model.ContractAgreementDirection.PROVIDING;
import static de.sovity.edc.e2e.WrapperApiUtils.getAssetsWithId;
import static de.sovity.edc.e2e.WrapperApiUtils.getContractDefinitionWithAssetId;
import static de.sovity.edc.extension.e2e.connector.remotes.management_api.DataTransferTestUtil.validateDataTransferred;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.jooq.JSON.json;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UiApiWrapperTest {

    private static final String PROVIDER_PARTICIPANT_ID = CeE2eTestSide.PROVIDER.getParticipantId();
    private static final String CONSUMER_PARTICIPANT_ID = CeE2eTestSide.CONSUMER.getParticipantId();
    private static final Random RANDOM = new Random();

    @RegisterExtension
    private static final CeE2eTestExtension E2E_TEST_EXTENSION = CeE2eTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();

    private TestBackendRemote dataAddress;

    @BeforeEach
    void setup(@Provider ManagementApiConnectorRemote providerConnector) {
        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new TestBackendRemote(providerConnector.getConfig().getDefaultApiUrl());
    }

    @DisabledOnGithub
    @Test
    void provide_consume_assetMapping_policyMapping_agreements(
        @Consumer Config consumerConfig,
        @Consumer ManagementApiConnectorRemote consumerConnector,
        @Consumer EdcClient consumerClient,
        @Provider Config providerConfig,
        @Provider EdcClient providerClient
    ) {
        // arrange
        var data = "expected data 123";
        var yesterday = OffsetDateTime.now().minusDays(1);

        var expression = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.CONSTRAINT)
            .constraint(UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.GT)
                .right(UiPolicyLiteral.builder()
                    .type(UiPolicyLiteralType.STRING)
                    .value(yesterday.toString())
                    .build())
                .build())
            .build();

        var policyId = providerClient.uiApi().createPolicyDefinitionV2(PolicyDefinitionCreateDto.builder()
            .policyDefinitionId("policy-1")
            .expression(expression)
            .build()).getId();

        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(dataAddress.getDataSourceUrl(data))
                .build())
            .build();

        var assetId = providerClient.uiApi().createAsset(UiAssetCreateRequest.builder()
            .id("asset-1")
            .title("AssetName")
            .description("AssetDescription")
            .licenseUrl("https://license-url")
            .version("1.0.0")
            .language("en")
            .mediaType("application/json")
            .dataCategory("dataCategory")
            .dataSubcategory("dataSubcategory")
            .dataModel("dataModel")
            .geoReferenceMethod("geoReferenceMethod")
            .transportMode("transportMode")
            .sovereignLegalName("my-sovereign")
            .geoLocation("my-geolocation")
            .nutsLocations(Arrays.asList("my-nuts-location1", "my-nuts-location2"))
            .dataSampleUrls(Arrays.asList("my-data-sample-urls1", "my-data-sample-urls2"))
            .referenceFileUrls(Arrays.asList("my-reference-files1", "my-reference-files2"))
            .referenceFilesDescription("my-additional-description")
            .conditionsForUse("my-conditions-for-use")
            .dataUpdateFrequency("my-data-update-frequency")
            .temporalCoverageFrom(LocalDate.parse("2007-12-03"))
            .temporalCoverageToInclusive(LocalDate.parse("2024-01-22"))
            .keywords(List.of("keyword1", "keyword2"))
            .publisherHomepage("publisherHomepage")
            .dataSource(dataSource)
            .customJsonAsString("""
                {"test": "value"}
                """)
            .customJsonLdAsString("""
                {"https://public/some#key": "public LD value"}
                """)
            .privateCustomJsonAsString("""
                {"private_test": "private value"}
                """)
            .privateCustomJsonLdAsString("""
                {"https://private/some#key": "private LD value"}
                """)
            .build()).getId();

        assertThat(assetId).isEqualTo("asset-1");


        providerClient.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
            .contractDefinitionId("cd-1")
            .accessPolicyId(policyId)
            .contractPolicyId(policyId)
            .assetSelector(List.of(UiCriterion.builder()
                .operandLeft(Prop.Edc.ID)
                .operator(UiCriterionOperator.EQ)
                .operandRight(UiCriterionLiteral.builder()
                    .type(UiCriterionLiteralType.VALUE)
                    .value(assetId)
                    .build())
                .build()))
            .build());


        val createdAsset = getAssetsWithId(providerClient, assetId);

        assertThat(createdAsset).hasSize(1);
        var asset = createdAsset.stream().filter(it -> it.getAssetId().equals("asset-1")).findFirst().get();

        var providerProtocolEndpoint = ConfigUtils.getProtocolApiUrl(providerConfig);
        var providerParticipantId = ConfigUtils.getParticipantId(providerConfig);
        var dataOffers = consumerClient.uiApi()
            .getCatalogPageDataOffers(providerParticipantId, providerProtocolEndpoint)
            .stream()
            .filter(it -> it.getAsset().getAssetId().equals(assetId))
            .toList();
        assertThat(dataOffers).hasSize(1);
        var dataOffer = dataOffers.get(0);
        assertThat(dataOffer.getContractOffers()).hasSize(1);
        var contractOffer = dataOffer.getContractOffers().get(0);

        // act
        var negotiation = negotiate(consumerClient, consumerConnector, dataOffer, contractOffer);
        initiateTransfer(consumerClient, negotiation);
        var providerAgreements = providerClient.uiApi()
            .getContractAgreementPage(null)
            .getContractAgreements()
            .stream()
            .filter(it -> it.getContractAgreementId().equals(negotiation.getContractAgreementId()))
            .toList();
        var consumerAgreements = consumerClient
            .uiApi()
            .getContractAgreementPage(null)
            .getContractAgreements()
            .stream()
            .filter(it -> it.getContractAgreementId().equals(negotiation.getContractAgreementId()))
            .toList();

        // assert
        assertThat(dataOffer.getEndpoint()).isEqualTo(providerProtocolEndpoint);
        assertThat(dataOffer.getParticipantId()).isEqualTo(PROVIDER_PARTICIPANT_ID);
        assertThat(dataOffer.getAsset().getAssetId()).isEqualTo(assetId);
        assertThat(dataOffer.getAsset().getTitle()).isEqualTo("AssetName");
        assertThat(dataOffer.getAsset().getConnectorEndpoint()).isEqualTo(providerProtocolEndpoint);
        assertThat(dataOffer.getAsset().getParticipantId()).isEqualTo(ConfigUtils.getParticipantId(providerConfig));
        assertThat(dataOffer.getAsset().getKeywords()).isEqualTo(List.of("keyword1", "keyword2"));
        assertThat(dataOffer.getAsset().getDescription()).isEqualTo("AssetDescription");
        assertThat(dataOffer.getAsset().getVersion()).isEqualTo("1.0.0");
        assertThat(dataOffer.getAsset().getLanguage()).isEqualTo("en");
        assertThat(dataOffer.getAsset().getMediaType()).isEqualTo("application/json");
        assertThat(dataOffer.getAsset().getDataCategory()).isEqualTo("dataCategory");
        assertThat(dataOffer.getAsset().getDataSubcategory()).isEqualTo("dataSubcategory");
        assertThat(dataOffer.getAsset().getDataModel()).isEqualTo("dataModel");
        assertThat(dataOffer.getAsset().getGeoReferenceMethod()).isEqualTo("geoReferenceMethod");
        assertThat(dataOffer.getAsset().getTransportMode()).isEqualTo("transportMode");
        assertThat(dataOffer.getAsset().getSovereignLegalName()).isEqualTo("my-sovereign");
        assertThat(dataOffer.getAsset().getGeoLocation()).isEqualTo("my-geolocation");
        assertThat(dataOffer.getAsset().getNutsLocations()).isEqualTo(Arrays.asList("my-nuts-location1", "my-nuts-location2"));
        assertThat(dataOffer.getAsset().getDataSampleUrls()).isEqualTo(Arrays.asList("my-data-sample-urls1", "my-data-sample-urls2"));
        assertThat(dataOffer.getAsset().getReferenceFileUrls()).isEqualTo(Arrays.asList("my-reference-files1", "my-reference-files2"));
        assertThat(dataOffer.getAsset().getReferenceFilesDescription()).isEqualTo("my-additional-description");
        assertThat(dataOffer.getAsset().getConditionsForUse()).isEqualTo("my-conditions-for-use");
        assertThat(dataOffer.getAsset().getDataUpdateFrequency()).isEqualTo("my-data-update-frequency");
        assertThat(dataOffer.getAsset().getTemporalCoverageFrom()).isEqualTo(LocalDate.parse("2007-12-03"));
        assertThat(dataOffer.getAsset().getTemporalCoverageToInclusive()).isEqualTo(LocalDate.parse("2024-01-22"));
        assertThat(dataOffer.getAsset().getLicenseUrl()).isEqualTo("https://license-url");
        assertThat(dataOffer.getAsset().getKeywords()).isEqualTo(List.of("keyword1", "keyword2"));
        assertThat(dataOffer.getAsset().getCreatorOrganizationName()).isEqualTo("Curator Name provider");
        assertThat(dataOffer.getAsset().getPublisherHomepage()).isEqualTo("publisherHomepage");
        assertThat(dataOffer.getAsset().getHttpDatasourceHintsProxyMethod()).isFalse();
        assertThat(dataOffer.getAsset().getHttpDatasourceHintsProxyPath()).isFalse();
        assertThat(dataOffer.getAsset().getHttpDatasourceHintsProxyQueryParams()).isFalse();
        assertThat(dataOffer.getAsset().getHttpDatasourceHintsProxyBody()).isFalse();
        assertThatJson(dataOffer.getAsset().getCustomJsonAsString()).isEqualTo("""
            {"test": "value"}
            """);
        assertThatJson(dataOffer.getAsset().getCustomJsonLdAsString()).isEqualTo("""
            {"https://public/some#key":"public LD value"}
            """);
        assertThat(dataOffer.getAsset().getPrivateCustomJsonAsString()).isNullOrEmpty();
        assertThatJson(dataOffer.getAsset().getPrivateCustomJsonLdAsString()).isObject().isEmpty();

        // while the data offer on the consumer side won't contain private properties, the asset page on the provider side should
        assertThat(asset.getAssetId()).isEqualTo(assetId);
        assertThat(asset.getTitle()).isEqualTo("AssetName");
        assertThat(asset.getConnectorEndpoint()).isEqualTo(providerProtocolEndpoint);
        assertThat(asset.getParticipantId()).isEqualTo(ConfigUtils.getParticipantId(providerConfig));

        assertThatJson(asset.getCustomJsonAsString()).isEqualTo("""
            { "test": "value" }
            """);
        assertThatJson(asset.getCustomJsonLdAsString()).isEqualTo("""
            { "https://public/some#key": "public LD value" }
            """);
        assertThatJson(asset.getPrivateCustomJsonAsString()).isEqualTo("""
            { "private_test": "private value" }
            """);
        assertThatJson(asset.getPrivateCustomJsonLdAsString()).isEqualTo("""
            { "https://private/some#key": "private LD value" }
            """);

        // Contract Agreement
        assertThat(providerAgreements).hasSize(1);
        var providerAgreement = providerAgreements.get(0);

        assertThat(consumerAgreements).hasSize(1);
        var consumerAgreement = consumerAgreements.get(0);

        assertThat(providerAgreement.getContractAgreementId()).isEqualTo(consumerAgreement.getContractAgreementId());

        // Provider Contract Agreement
        assertThat(providerAgreement.getContractAgreementId()).isEqualTo(negotiation.getContractAgreementId());
        assertThat(providerAgreement.getDirection()).isEqualTo(PROVIDING);
        assertThat(providerAgreement.getCounterPartyAddress()).isEqualTo(ConfigUtils.getProtocolApiUrl(consumerConfig));
        assertThat(providerAgreement.getCounterPartyId()).isEqualTo(CONSUMER_PARTICIPANT_ID);

        assertThat(providerAgreement.getAsset().getAssetId()).isEqualTo(assetId);
        var providingContractPolicyConstraint = providerAgreement.getContractPolicy().getExpression();
        assertThat(providingContractPolicyConstraint).usingRecursiveComparison().isEqualTo(expression);

        assertThat(providerAgreement.getAsset().getAssetId()).isEqualTo(assetId);
        assertThat(providerAgreement.getAsset().getKeywords()).isEqualTo(List.of("keyword1", "keyword2"));
        assertThat(providerAgreement.getAsset().getTitle()).isEqualTo("AssetName");
        assertThat(providerAgreement.getAsset().getDescription()).isEqualTo("AssetDescription");

        // Consumer Contract Agreement
        assertThat(consumerAgreement.getContractAgreementId()).isEqualTo(negotiation.getContractAgreementId());
        assertThat(consumerAgreement.getDirection()).isEqualTo(CONSUMING);
        assertThat(consumerAgreement.getCounterPartyAddress()).isEqualTo(dataOffer.getEndpoint());
        assertThat(consumerAgreement.getCounterPartyId()).isEqualTo(PROVIDER_PARTICIPANT_ID);
        assertThat(consumerAgreement.getAsset().getAssetId()).isEqualTo(assetId);

        var consumingContractPolicyConstraint = consumerAgreement.getContractPolicy().getExpression();
        assertThat(consumingContractPolicyConstraint).usingRecursiveComparison().isEqualTo(expression);

        assertThat(consumerAgreement.getAsset().getAssetId()).isEqualTo(assetId);
        assertThat(consumerAgreement.getAsset().getTitle()).isEqualTo(assetId);

        // Test Policy
        assertThat(contractOffer.getPolicy().getExpression().getType()).isEqualTo(UiPolicyExpressionType.CONSTRAINT);
        var constraint = contractOffer.getPolicy().getExpression().getConstraint();
        assertThat(constraint.getLeft()).isEqualTo("POLICY_EVALUATION_TIME");
        assertThat(constraint.getOperator()).isEqualTo(OperatorDto.GT);
        assertThat(constraint.getRight().getType()).isEqualTo(UiPolicyLiteralType.STRING);
        assertThat(constraint.getRight().getValue()).isEqualTo(yesterday.toString());

        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), data);

        validateTransferProcessesOk(consumerClient, providerClient);
    }

    @Test
    void canOverrideTheWellKnowPropertiesUsingTheCustomProperties(@Provider EdcClient providerClient) {
        // arrange
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("http://example.com/base")
                .build())
            .build();

        var assetId = providerClient.uiApi().createAsset(UiAssetCreateRequest.builder()
            .id("asset-2")
            .title("will be overridden")
            .dataSource(dataSource)
            .customJsonLdAsString("""
                {
                    "http://purl.org/dc/terms/title": "The real title",
                    "http://purl.org/dc/terms/spatial": {
                      "http://purl.org/dc/terms/identifier": ["a", "b", "c"]
                    },
                    "http://example.com/an-actual-custom-property": "custom value"
                }
                """)
            .build()).getId();
        assertThat(assetId).isEqualTo("asset-2");

        // act

        val createdAsset = getAssetsWithId(providerClient, assetId);

        assertThat(createdAsset).hasSize(1);
        val asset = createdAsset.get(0);

        // assert

        // while the data offer on the consumer side won't contain private properties, the asset page on the provider side should
        assertThat(asset.getAssetId()).isEqualTo(assetId);
        // overridden property
        assertThat(asset.getTitle()).isEqualTo("The real title");
        // added property
        assertThat(asset.getNutsLocations()).isEqualTo(List.of("a", "b", "c"));
        // remaining custom property
        assertThatJson(asset.getCustomJsonLdAsString()).isEqualTo("""
            {
                "http://example.com/an-actual-custom-property": "custom value"
            }
            """);
    }

    @DisabledOnGithub
    @Test
    void customTransferRequest(
        @Consumer ManagementApiConnectorRemote consumerConnector,
        @Consumer EdcClient consumerClient,
        @Provider Config providerConfig,
        @Provider EdcClient providerClient
    ) {
        // arrange
        var data = "expected data 123";

        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(dataAddress.getDataSourceUrl(data))
                .build())
            .build();

        var assetId = providerClient.uiApi().createAsset(UiAssetCreateRequest.builder()
            .id("asset-3")
            .dataSource(dataSource)
            .build()).getId();

        assertThat(assetId).isEqualTo("asset-3");

        var policyId = providerClient.uiApi().createPolicyDefinitionV2(PolicyDefinitionCreateDto.builder()
            .policyDefinitionId("policy-3")
            .expression(UiPolicyExpression.builder()
                .type(UiPolicyExpressionType.EMPTY)
                .build())
            .build()).getId();

        val contractDefinitionId = providerClient.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
            .contractDefinitionId("cd-3")
            .accessPolicyId(policyId)
            .contractPolicyId(policyId)
            .assetSelector(List.of())
            .build());

        val providerProtocolEndpoint = ConfigUtils.getProtocolApiUrl(providerConfig);
        val providerParticipantId = ConfigUtils.getParticipantId(providerConfig);
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(providerParticipantId, providerProtocolEndpoint);
        assertThat(dataOffers).hasSize(1);
        var dataOffer = dataOffers.get(0);
        assertThat(dataOffer.getContractOffers()).hasSize(1);
        var contractOffer = dataOffer.getContractOffers().get(0);

        // act
        var negotiation = negotiate(consumerClient, consumerConnector, dataOffer, contractOffer);
        var transferRequestJsonLd = Json.createObjectBuilder()
            .add(Prop.Edc.DATA_DESTINATION, getDatasinkPropertiesJsonObject())
            .add(Prop.Edc.CTX + "transferType", Json.createValue("HttpData-PUSH"))
            .add(Prop.Edc.CTX + "protocol", HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
            .add(Prop.Edc.CTX + "managedResources", false)
            .build();
        var transferRequest = InitiateCustomTransferRequest.builder()
            .contractAgreementId(negotiation.getContractAgreementId())
            .transferProcessRequestJsonLd(JsonUtils.toJson(transferRequestJsonLd))
            .build();
        consumerClient.uiApi().initiateCustomTransfer(transferRequest);

        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), data);

        providerClient.uiApi().deleteContractDefinition(contractDefinitionId.getId());
        providerClient.uiApi().deletePolicyDefinition(policyId);
    }

    @DisabledOnGithub
    @Test
    void editAssetOnLiveContract(
        @Consumer ManagementApiConnectorRemote consumerConnector,
        @Consumer EdcClient consumerClient,
        @Provider Config providerConfig,
        @Provider EdcClient providerClient
    ) {
        // arrange
        var data = "expected data 123";

        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(dataAddress.getDataSourceUrl(data))
                .build())
            .build();

        var assetId = providerClient.uiApi().createAsset(UiAssetCreateRequest.builder()
            .id("asset-4")
            .title("Bad Asset Title")
            .dataSource(dataSource)
            .customJsonAsString("""
                {
                    "test": "value"
                }
                """)
            .customJsonLdAsString("""
                {
                    "test": "not a valid key, will be deleted",
                    "http://example.com/key-to-delete": "with a valida key",
                    "http://example.com/key-to-edit": "with a valida key"
                }
                """)
            .privateCustomJsonAsString("""
                {
                    "private-test": "value"
                }
                """)
            .privateCustomJsonLdAsString("""
                {
                    "private-test": "not a valid key, will be deleted",
                    "http://example.com/private-key-to-delete": "private with a valid key",
                    "http://example.com/private-key-to-edit": "private with a valid key"
                }
                """)
            .build()).getId();

        providerClient.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
            .contractDefinitionId("cd-4")
            .accessPolicyId("always-true")
            .contractPolicyId("always-true")
            .assetSelector(List.of(UiCriterion.builder()
                .operandLeft(Prop.Edc.ID)
                .operator(UiCriterionOperator.EQ)
                .operandRight(UiCriterionLiteral.builder()
                    .type(UiCriterionLiteralType.VALUE)
                    .value(assetId)
                    .build())
                .build()))
            .build());

        val providerProtocolEndpoint = ConfigUtils.getProtocolApiUrl(providerConfig);
        val providerParticipantId = ConfigUtils.getParticipantId(providerConfig);
        var dataOffers = consumerClient.uiApi()
            .getCatalogPageDataOffers(providerParticipantId, providerProtocolEndpoint)
            .stream()
            .filter(it -> it.getAsset().getAssetId().equals(assetId))
            .toList();
        assertThat(dataOffers).hasSize(1);
        var dataOffer = dataOffers.get(0);
        assertThat(dataOffer.getContractOffers()).hasSize(1);
        var contractOffer = dataOffer.getContractOffers().get(0);
        var negotiation = negotiate(consumerClient, consumerConnector, dataOffer, contractOffer);

        // act
        providerClient.uiApi().editAsset(assetId, UiAssetEditRequest.builder()
            .title("Good Asset Title")
            .customJsonAsString("""
                {
                    "edited": "new value"
                }
                """)
            .customJsonLdAsString("""
                {
                    "edited": "not a valid key, will be deleted",
                    "http://example.com/key-to-delete": null,
                    "http://example.com/key-to-edit": "with a valid key",
                    "http://example.com/extra": "value to add"
                }
                """)
            .privateCustomJsonAsString("""
                {
                    "private-edited": "new value"
                }
                """)
            .privateCustomJsonLdAsString("""
                {
                    "private-edited": "not a valid key, will be deleted",
                    "http://example.com/private-key-to-delete": null,
                    "http://example.com/private-key-to-edit": "private with a valid key",
                    "http://example.com/private-extra": "private value to add"
                }
                """)
            .build());
        initiateTransfer(consumerClient, negotiation);

        // assert
        String asset4title = consumerClient.uiApi()
            .getCatalogPageDataOffers(providerParticipantId, providerProtocolEndpoint)
            .stream()
            .filter(it -> it.getAsset().getAssetId().equals("asset-4"))
            .findFirst()
            .get()
            .getAsset()
            .getTitle();

        assertThat(
            asset4title)
            .isEqualTo("Good Asset Title");
        val firstAsset = providerClient.uiApi().getContractAgreementPage(null).getContractAgreements().get(0).getAsset();
        assertThat(firstAsset.getTitle()).isEqualTo("Good Asset Title");
        assertThat(firstAsset.getCustomJsonAsString()).isEqualTo("""
            {
                "edited": "new value"
            }
            """);
        assertThatJson(firstAsset.getCustomJsonLdAsString()).isEqualTo("""
            {
                "http://example.com/key-to-edit": "with a valid key",
                "http://example.com/extra": "value to add"
            }
            """);
        assertThat(firstAsset.getPrivateCustomJsonAsString()).isEqualTo("""
            {
                "private-edited": "new value"
            }
            """);
        assertThatJson(firstAsset.getPrivateCustomJsonLdAsString()).isEqualTo("""
            {
                "http://example.com/private-key-to-edit": "private with a valid key",
                "http://example.com/private-extra": "private value to add"
            }
            """);
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), data);
        validateTransferProcessesOk(consumerClient, providerClient);
        assertThat(providerClient.uiApi().getTransferHistoryPage().getTransferEntries().get(0).getAssetName())
            .isEqualTo("Good Asset Title");
    }

    @Test
    @DisabledOnGithub
    void checkIdAvailability(E2eTestScenario scenario, @Provider EdcClient providerClient) {
        // arrange
        var assetId = scenario.createAsset();
        var policyId = "policy-id";
        scenario.createPolicy(policyId, OffsetDateTime.MIN, OffsetDateTime.MAX);
        var contractDefinitionId = scenario.createContractDefinition(policyId, assetId);

        // act
        val negAssetResponse = providerClient.uiApi().isAssetIdAvailable(assetId);
        val negPolicyResponse = providerClient.uiApi().isPolicyIdAvailable(policyId);
        val negContractDefinitionResponse = providerClient.uiApi().isContractDefinitionIdAvailable(contractDefinitionId.getId());
        val posAssetResponse = providerClient.uiApi().isAssetIdAvailable("new-asset");
        val posPolicyResponse = providerClient.uiApi().isPolicyIdAvailable("new-policy");
        val posContractDefinitionResponse = providerClient.uiApi().isContractDefinitionIdAvailable("new-cd");

        // assert
        assertThat(negAssetResponse.getAvailable()).isFalse();
        assertThat(negPolicyResponse.getAvailable()).isFalse();
        assertThat(negContractDefinitionResponse.getAvailable()).isFalse();

        assertThat(posAssetResponse.getAvailable()).isTrue();
        assertThat(posPolicyResponse.getAvailable()).isTrue();
        assertThat(posContractDefinitionResponse.getAvailable()).isTrue();
    }

    @DisabledOnGithub
    @Test
    void retrieveSingleContractAgreement(
        E2eTestScenario scenario,
        @Provider EdcClient providerClient
    ) {
        // arrange
        val assetId = scenario.createAsset();

        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAssetAndAwait(assetId);

        // act
        val retrieved = providerClient.uiApi().getContractAgreementCard(negotiation.getContractAgreementId());
        val alternative = providerClient.uiApi()
            .getContractAgreementPage(null)
            .getContractAgreements()
            .stream()
            .filter(it -> it.getContractAgreementId().equals(negotiation.getContractAgreementId()))
            .findFirst()
            .orElseThrow();

        val retrievedPolicy = retrieved.getContractPolicy();
        val alternativePolicy = alternative.getContractPolicy();

        retrieved.setContractPolicy(null);
        alternative.setContractPolicy(null);

        // assert
        assertThat(retrieved).usingRecursiveAssertion().isEqualTo(alternative);

        // assert separately because the policy ID is re-generated on each query
        assertThat(retrievedPolicy)
            .usingRecursiveComparison()
            .ignoringFields("policyJsonLd")
            .isEqualTo(alternativePolicy);

        assertThatJson(retrievedPolicy.getPolicyJsonLd())
            .whenIgnoringPaths("@id")
            .isEqualTo(alternativePolicy.getPolicyJsonLd());
    }

    @Test
    @DisabledOnGithub
    void canMakeAnOnDemandDataSourceAvailable(
        E2eTestScenario scenario,
        @Provider EdcClient providerClient
    ) {
        // arrange
        val assetId = scenario.createAsset(UiAssetCreateRequest.builder()
            .dataSource(UiDataSource.builder()
                .type(DataSourceType.ON_REQUEST)
                .onRequest(UiDataSourceOnRequest.builder()
                    .contactEmail("whatever@example.com")
                    .contactPreferredEmailSubject("Subject")
                    .build())
                .build())
            .id("asset-" + RANDOM.nextInt())
            .title("foo")
            .build());

        // act

        providerClient.uiApi().editAsset(
            assetId,
            UiAssetEditRequest.builder()
                .dataSourceOverrideOrNull(UiDataSource.builder()
                    .type(DataSourceType.HTTP_DATA)
                    .httpData(UiDataSourceHttpData.builder()
                        .method(UiDataSourceHttpDataMethod.GET)
                        .baseUrl("http://example.com/baseUrl")
                        .build())
                    .build())
                .build());

        val asset =
            providerClient.uiApi().getAssetPage().getAssets().stream().filter(it -> it.getAssetId().equals(assetId)).findFirst().get();

        // assert
        assertThat(asset.getDataSourceAvailability()).isEqualTo(DataSourceAvailability.LIVE);
        assertThatJson(asset.getAssetJsonLd())
            .inPath("$.[\"https://w3id.org/edc/v0.0.1/ns/dataAddress\"][\"https://w3id.org/edc/v0.0.1/ns/baseUrl\"]")
            .isEqualTo("\"http://example.com/baseUrl\"");
    }

    @Test
    void canCreateDataOfferWithoutAnyNewPolicyNotContractDefinition(
        @Provider EdcClient providerClient
    ) {
        // arrange
        val dataSource = UiDataSource.builder()
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("http://example.com")
                .method(UiDataSourceHttpDataMethod.GET)
                .build())
            .type(DataSourceType.HTTP_DATA)
            .build();

        val assetId = "asset";
        val asset = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(assetId)
            .title("My asset")
            .build();

        val dataOfferCreateRequest = new DataOfferCreationRequest(
            asset,
            DataOfferCreationRequest.PolicyEnum.DONT_PUBLISH,
            null
        );

        // act
        val returnedId = providerClient.uiApi().createDataOffer(dataOfferCreateRequest).getId();

        // assert
        assertThat(returnedId).isEqualTo(assetId);

        assertThat(providerClient.uiApi().getAssetPage().getAssets())
            .extracting(UiAsset::getAssetId)
            .first()
            .isEqualTo(assetId);

        assertThat(getPolicyNamed(assetId, providerClient)).hasSize(0);

        assertThat(providerClient.uiApi().getContractDefinitionPage().getContractDefinitions())
            .hasSize(0);
    }

    @Test
    void canCreateDataOfferWithNewPolicy(
        @Provider EdcClient providerClient
    ) {
        // arrange
        val dataSource = UiDataSource.builder()
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("http://example.com")
                .method(UiDataSourceHttpDataMethod.GET)
                .build())
            .type(DataSourceType.HTTP_DATA)
            .build();

        val assetId = "asset-" + RANDOM.nextInt();
        val asset = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(assetId)
            .title("My asset")
            .build();

        val dataOfferCreateRequest = new DataOfferCreationRequest(
            asset,
            DataOfferCreationRequest.PolicyEnum.PUBLISH_RESTRICTED,
            UiPolicyExpression.builder()
                .constraint(UiPolicyConstraint.builder()
                    .left("foo")
                    .operator(OperatorDto.EQ)
                    .right(UiPolicyLiteral.builder()
                        .value("bar")
                        .build())
                    .build())
                .build()
        );

        // act
        val returnedId = providerClient.uiApi().createDataOffer(dataOfferCreateRequest).getId();

        // assert
        assertThat(returnedId).isEqualTo(assetId);

        assertThat(providerClient.uiApi().getAssetPage().getAssets())
            .extracting(UiAsset::getAssetId)
            .first()
            .isEqualTo(assetId);

        assertThat(getPolicyNamed(assetId, providerClient))
            .hasSize(1)
            .extracting(PolicyDefinitionDto::getPolicyDefinitionId)
            .first()
            .isEqualTo(assetId);

        assertThat(providerClient.uiApi().getContractDefinitionPage().getContractDefinitions())
            .extracting(ContractDefinitionEntry::getContractDefinitionId)
            .first()
            .isEqualTo(assetId);
    }

    @Test
    void canCreateDataOfferWithNewEmptyPolicyAndRestrictedPublishing(
        @Provider EdcClient providerClient
    ) {
        // arrange
        val dataSource = UiDataSource.builder()
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("http://example.com")
                .method(UiDataSourceHttpDataMethod.GET)
                .build())
            .type(DataSourceType.HTTP_DATA)
            .build();

        val assetId = "asset-" + RANDOM.nextInt();
        val asset = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(assetId)
            .title("My asset")
            .build();

        val dataOfferCreateRequest = new DataOfferCreationRequest(
            asset,
            DataOfferCreationRequest.PolicyEnum.PUBLISH_RESTRICTED,
            UiPolicyExpression.builder().build()
        );

        // act
        val returnedId = providerClient.uiApi().createDataOffer(dataOfferCreateRequest).getId();

        // assert
        assertThat(returnedId).isEqualTo(assetId);

        assertThat(providerClient.uiApi().getAssetPage().getAssets())
            .extracting(UiAsset::getAssetId)
            .first()
            .isEqualTo(assetId);

        assertThat(getPolicyNamed(assetId, providerClient))
            .hasSize(1)
            .extracting(PolicyDefinitionDto::getPolicyDefinitionId)
            .first()
            .isEqualTo(assetId);

        assertThat(providerClient.uiApi().getContractDefinitionPage().getContractDefinitions())
            .extracting(ContractDefinitionEntry::getContractDefinitionId)
            .first()
            .isEqualTo(assetId);
    }

    @Test
    void dontCreateAnythingIfTheAssetAlreadyExists(
        E2eTestScenario scenario,
        @Provider EdcClient providerClient
    ) {
        // arrange
        val assetId = scenario.createAsset();

        // act
        assertThrows(
            ApiException.class,
            () -> providerClient.uiApi()
                .createDataOffer(DataOfferCreationRequest.builder()
                    .uiAssetCreateRequest(UiAssetCreateRequest.builder()
                        .id(assetId)
                        .dataSource(UiDataSource.builder()
                            .type(DataSourceType.ON_REQUEST)
                            .onRequest(UiDataSourceOnRequest.builder()
                                .contactEmail("foo@example.com")
                                .contactPreferredEmailSubject("Subject")
                                .build())
                            .build())
                        .build())
                    .build()));

        // assert

        val createdAsset = getAssetsWithId(providerClient, assetId);

        assertThat(createdAsset)
            .hasSize(1)
            .extracting(UiAsset::getAssetId)
            .first()
            .isEqualTo(assetId);

        assertThat(getPolicyNamed(assetId, providerClient)).hasSize(0);

        val createdContractDefinition = getContractDefinitionWithAssetId(providerClient, assetId);

        assertThat(createdContractDefinition).hasSize(0);
    }

    @Test
    void dontCreateAnythingIfThePolicyAlreadyExists(
        E2eTestScenario scenario,
        @Provider EdcClient providerClient
    ) {
        // arrange
        val assetId = "assetId-" + RANDOM.nextInt();
        scenario.createPolicy(assetId, OffsetDateTime.now(), OffsetDateTime.now());

        // act
        assertThrows(
            ApiException.class,
            () -> providerClient.uiApi()
                .createDataOffer(DataOfferCreationRequest.builder()
                    .uiAssetCreateRequest(UiAssetCreateRequest.builder()
                        .id(assetId)
                        .dataSource(UiDataSource.builder()
                            .type(DataSourceType.ON_REQUEST)
                            .onRequest(UiDataSourceOnRequest.builder()
                                .contactEmail("foo@example.com")
                                .contactPreferredEmailSubject("Subject")
                                .build())
                            .build())
                        .build())
                    .build()));

        // assert

        val createdAsset = getAssetsWithId(providerClient, assetId);

        assertThat(createdAsset).hasSize(0);

        assertThat(getPolicyNamed(assetId, providerClient)).hasSize(1)
            .extracting(PolicyDefinitionDto::getPolicyDefinitionId)
            .first()
            .isEqualTo(assetId);

        val createdContractDefinition = getContractDefinitionWithAssetId(providerClient, assetId);

        assertThat(createdContractDefinition).hasSize(0);
    }

    @Test
    void dontCreateAnythingIfTheContractDefinitionAlreadyExists(
        E2eTestScenario scenario,
        @Provider EdcClient providerClient
    ) {
        // arrange
        val id = "assetId";
        val assetId = scenario.createAsset();
        providerClient.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
            .contractDefinitionId(id)
            .accessPolicyId("always-true")
            .contractPolicyId("always-true")
            .assetSelector(List.of(UiCriterion.builder()
                .operandLeft(Prop.Edc.ID)
                .operator(UiCriterionOperator.EQ)
                .operandRight(UiCriterionLiteral.builder()
                    .type(UiCriterionLiteralType.VALUE)
                    .value(assetId)
                    .build())
                .build()))
            .build());

        // act
        assertThrows(
            ApiException.class,
            () -> providerClient.uiApi()
                .createDataOffer(DataOfferCreationRequest.builder()
                    .policy(DataOfferCreationRequest.PolicyEnum.PUBLISH_RESTRICTED)
                    .uiAssetCreateRequest(UiAssetCreateRequest.builder()
                        .id(id)
                        .dataSource(UiDataSource.builder()
                            .type(DataSourceType.ON_REQUEST)
                            .onRequest(UiDataSourceOnRequest.builder()
                                .contactEmail("foo@example.com")
                                .contactPreferredEmailSubject("Subject")
                                .build())
                            .build())
                        .build())
                    .build()));

        // assert
        val createdAsset = getAssetsWithId(providerClient, assetId);

        assertThat(createdAsset)
            // the asset used for the placeholder contract definition
            .hasSize(1)
            .extracting(UiAsset::getAssetId)
            .first()
            .isEqualTo(assetId);

        assertThat(getPolicyNamed(id, providerClient)).hasSize(0);

        assertThat(providerClient.uiApi().getContractDefinitionPage().getContractDefinitions())
            .hasSize(1)
            .filteredOn(it -> it.getContractDefinitionId().equals(id))
            .extracting(ContractDefinitionEntry::getContractDefinitionId)
            .first()
            // the already existing one, before the data offer creation attempt
            .isEqualTo(id);
    }

    @Test
    void reuseTheAlwaysTruePolicyWhenPublishingUnrestricted(
        @Provider EdcClient providerClient
    ) {
        // arrange
        val assetId = "assetId-" + RANDOM.nextInt();

        // act
        providerClient.uiApi()
            .createDataOffer(DataOfferCreationRequest.builder()
                .uiAssetCreateRequest(UiAssetCreateRequest.builder()
                    .id(assetId)
                    .dataSource(UiDataSource.builder()
                        .type(DataSourceType.ON_REQUEST)
                        .onRequest(UiDataSourceOnRequest.builder()
                            .contactEmail("foo@example.com")
                            .contactPreferredEmailSubject("Subject")
                            .build())
                        .build())
                    .build())
                .policy(DataOfferCreationRequest.PolicyEnum.PUBLISH_UNRESTRICTED)
                .build());

        // assert
        val createdAsset = getAssetsWithId(providerClient, assetId);

        assertThat(createdAsset)
            // the asset used for the placeholder contract definition
            .hasSize(1)
            .extracting(UiAsset::getAssetId)
            .first()
            .isEqualTo(assetId);

        assertThat(getPolicyNamed(assetId, providerClient)).hasSize(0);

        val createdContractDefinition = getContractDefinitionWithAssetId(providerClient, assetId);

        assertThat(createdContractDefinition)
            .hasSize(1)
            .filteredOn(it -> it.getContractDefinitionId().equals(assetId))
            .extracting(ContractDefinitionEntry::getContractDefinitionId)
            .first()
            // the already existing one, before the data offer creation attempt
            .isEqualTo(assetId);
    }

    @Test
    void onlyCreateTheAssetWhenDontPublish(
        @Provider EdcClient providerClient
    ) {
        // arrange
        val assetId = "assetId";

        // act
        providerClient.uiApi()
            .createDataOffer(DataOfferCreationRequest.builder()
                .uiAssetCreateRequest(UiAssetCreateRequest.builder()
                    .id(assetId)
                    .dataSource(UiDataSource.builder()
                        .type(DataSourceType.ON_REQUEST)
                        .onRequest(UiDataSourceOnRequest.builder()
                            .contactEmail("foo@example.com")
                            .contactPreferredEmailSubject("Subject")
                            .build())
                        .build())
                    .build())
                .policy(DataOfferCreationRequest.PolicyEnum.DONT_PUBLISH)
                .build());

        // assert
        val createdAsset = getAssetsWithId(providerClient, assetId);

        assertThat(createdAsset)
            // the asset used for the placeholder contract definition
            .hasSize(1)
            .extracting(UiAsset::getAssetId)
            .first()
            .isEqualTo(assetId);

        assertThat(getPolicyNamed(assetId, providerClient)).hasSize(0);

        assertThat(providerClient.uiApi().getContractDefinitionPage().getContractDefinitions())
            .hasSize(0);
    }

    @Order(Order.DEFAULT + 100)
    @Test
    void canLoadTheDashboardWithLargeNumberOfAssets(
        E2eTestScenario scenario,
        @Provider DSLContext dsl,
        @Provider EdcClient providerClient,
        @Consumer Config config
    ) {
        // arrange
        int target = 10000;

        int firstPort = config.getInteger("my.edc.first.port");
        int consumerDspPort = firstPort + 3;
        System.out.println("first port " + firstPort);

        IntStream.range(999, 999 + target).forEach((i) -> {
            // insert new asset with jooq
            String assetId = "asset-" + i;
            insertAsset(dsl, assetId);
            val contractAgreementId = insertContractAgreement(dsl, assetId);
            val contractDefinition = insertContractDefinition(dsl, assetId);
            val neg = insertContractNegotiation(dsl, consumerDspPort, assetId, contractDefinition, contractAgreementId);
        });

        // act
        assertDoesNotThrow(() -> providerClient.uiApi().getDashboardPage());
        assertDoesNotThrow(() -> providerClient.uiApi().getContractAgreementPage(new ContractAgreementPageQuery()));
    }

    private String insertContractNegotiation(
        DSLContext dsl,
        int consumerDspPort,
        String assetId,
        String contractDefinition,
        String contractAgreementId
    ) {
        val n = Tables.EDC_CONTRACT_NEGOTIATION;
        val contractNegotiationId = UUID.randomUUID().toString();
        dsl.insertInto(n)
            .set(n.ID, contractNegotiationId)
            .set(n.CREATED_AT, System.currentTimeMillis())
            .set(n.UPDATED_AT, System.currentTimeMillis())
            .set(n.CORRELATION_ID, UUID.randomUUID().toString())
            .set(n.COUNTERPARTY_ID, "consumer")
            .set(n.COUNTERPARTY_ADDRESS, "http://localhost:" + consumerDspPort + "/api/dsp")
            .set(n.PROTOCOL, "dataspace-protocol-http")
            .set(n.TYPE, "PROVIDER")
            .set(n.STATE, 1200)
            .set(n.STATE_COUNT, 1)
            .set(n.STATE_TIMESTAMP, System.currentTimeMillis())
            .set(n.ERROR_DETAIL, (String) null)
            .set(n.AGREEMENT_ID, contractAgreementId)
            .set(n.CONTRACT_OFFERS, json(
                "[{\"id\":\"" + base64(contractDefinition) + ":" + base64(assetId) + ":" + base64(assetId) +
                    "\",\"policy\":{\"permissions\":[{\"edctype\":\"dataspaceconnector:permission\",\"action\":{\"type\":\"USE\",\"includedIn\":null,\"constraint\":null},\"constraints\":[],\"duties\":[]}],\"prohibitions\":[],\"obligations\":[],\"extensibleProperties\":{},\"inheritsFrom\":null,\"assigner\":null,\"assignee\":null,\"target\":\"" + assetId + "\",\"@type\":{\"@policytype\":\"set\"}},\"assetId\":\"" + assetId + "\"}]"))
            .set(n.CALLBACK_ADDRESSES, json("[]"))
            .set(n.TRACE_CONTEXT, json("{}"))
            .set(n.PENDING, false)
            .set(n.PROTOCOL_MESSAGES, json("{\"lastSent\":null,\"received\":[]}"))
            .set(n.LEASE_ID, (String) null)
            .execute();

        return contractNegotiationId;
    }

    private static String base64(String contractDefinition) {
        return Base64.getEncoder().encodeToString(contractDefinition.getBytes());
    }

    private String insertContractDefinition(DSLContext dsl, String assetId) {
        val cd = Tables.EDC_CONTRACT_DEFINITIONS;
        val contractDefinitionId = "cd-always-true-" + assetId;
        dsl.insertInto(cd)
            .set(cd.CREATED_AT, System.currentTimeMillis())
            .set(cd.CONTRACT_DEFINITION_ID, contractDefinitionId)
            .set(cd.ACCESS_POLICY_ID, "always-true")
            .set(cd.CONTRACT_POLICY_ID, "always-true")
            .set(cd.ASSETS_SELECTOR, json("""
                [{"operandLeft":"https://w3id.org/edc/v0.0.1/ns/id","operator":"=","operandRight":\"""" + assetId + """
                "}]"""))
            .set(cd.PRIVATE_PROPERTIES, json("{}"))
            .execute();

        return contractDefinitionId;
    }

    private String insertContractAgreement(DSLContext dsl, String assetId) {

        val a = Tables.EDC_CONTRACT_AGREEMENT;
        val contractAgreementId = UUID.randomUUID().toString();
        dsl.insertInto(a)
            .set(a.AGR_ID, contractAgreementId)
            .set(a.PROVIDER_AGENT_ID, PROVIDER_PARTICIPANT_ID)
            .set(a.CONSUMER_AGENT_ID, CONSUMER_PARTICIPANT_ID)
            .set(a.SIGNING_DATE, System.currentTimeMillis())
            .set(a.ASSET_ID, assetId)
            .set(a.POLICY, json("""
                {
                  "permissions": [
                    {
                      "edctype": "dataspaceconnector:permission",
                      "action": {
                        "type": "USE",
                        "includedIn": null,
                        "constraint": null
                      },
                      "constraints": [],
                      "duties": []
                    }
                  ],
                  "prohibitions": [],
                  "obligations": [],
                  "extensibleProperties": {},
                  "inheritsFrom": null,
                  "assigner": null,
                  "assignee": null,
                  "target": \"""" + assetId + """
                                   ",
                                   "@type": {
                                     "@policytype": "contract"
                                   }
                                 }
                """))
            .execute();

        return contractAgreementId;
    }

    private static void insertAsset(DSLContext dsl, String assetId) {
        val a = Tables.EDC_ASSET;
        dsl.insertInto(a)
            .set(a.ASSET_ID, assetId)
            .set(a.CREATED_AT, System.currentTimeMillis())
            .set(a.PROPERTIES, json("""
                {"https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyBody":"false","http://www.w3.org/ns/dcat#version":"1.0.0","http://purl.org/dc/terms/creator":{"http://xmlns.com/foaf/0.1/name":[{"@value":"Curator Name provider"}]},"https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyPath":"false","http://purl.org/dc/terms/title":"AssetName_""" + assetId + """
                ","http://purl.org/dc/terms/language":"en","https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyMethod":"false","https://w3id.org/edc/v0.0.1/ns/id":\"""" + assetId + """
                ","https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyQueryParams":"false"}
                """))
            .set(a.PRIVATE_PROPERTIES, json("{}"))
            .set(a.DATA_ADDRESS, json("""
                {"https://w3id.org/edc/v0.0.1/ns/type":"HttpData","https://w3id.org/edc/v0.0.1/ns/baseUrl":"http://example.com"}
                """))
            .execute();
    }

    private static @NotNull List<PolicyDefinitionDto> getPolicyNamed(String name, EdcClient edcClient) {
        return edcClient.uiApi()
            .getPolicyDefinitionPage()
            .getPolicies()
            .stream()
            .filter(it -> it.getPolicyDefinitionId().equals(name))
            .toList();
    }

    private UiContractNegotiation negotiate(
        EdcClient consumerClient,
        ManagementApiConnectorRemote consumerConnector,
        UiDataOffer dataOffer,
        UiContractOffer contractOffer
    ) {
        var negotiationRequest = ContractNegotiationRequest.builder()
            .counterPartyAddress(dataOffer.getEndpoint())
            .counterPartyId(dataOffer.getParticipantId())
            .assetId(dataOffer.getAsset().getAssetId())
            .contractOfferId(contractOffer.getContractOfferId())
            .policyJsonLd(contractOffer.getPolicy().getPolicyJsonLd())
            .build();

        var negotiationId = consumerClient.uiApi().initiateContractNegotiation(negotiationRequest)
            .getContractNegotiationId();

        var negotiation = Awaitility.await().atMost(consumerConnector.timeout).until(
            () -> consumerClient.uiApi().getContractNegotiation(negotiationId),
            it -> it.getState().getSimplifiedState() != ContractNegotiationSimplifiedState.IN_PROGRESS
        );

        assertThat(negotiation.getState().getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.AGREED);
        return negotiation;
    }

    private void initiateTransfer(EdcClient consumerClient, UiContractNegotiation negotiation) {
        var contractAgreementId = negotiation.getContractAgreementId();
        var transferRequest = InitiateTransferRequest.builder()
            .contractAgreementId(contractAgreementId)
            .transferType("HttpData-PUSH")
            .dataSinkProperties(dataAddress.getDataSinkProperties())
            .build();
        consumerClient.uiApi().initiateTransfer(transferRequest);
    }

    private void validateTransferProcessesOk(EdcClient consumerClient, EdcClient providerClient) {
        await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {
            var providing = providerClient.uiApi().getTransferHistoryPage().getTransferEntries().get(0);
            var consuming = consumerClient.uiApi().getTransferHistoryPage().getTransferEntries().get(0);
            assertThat(providing.getState().getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
            assertThat(consuming.getState().getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private JsonObject getDatasinkPropertiesJsonObject() {
        var props = dataAddress.getDataSinkProperties();
        return Json.createObjectBuilder((Map<String, Object>) (Map) props).build();
    }
}
