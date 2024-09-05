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
import de.sovity.edc.extension.e2e.connector.remotes.management_api.ManagementApiConnectorRemote;
import de.sovity.edc.extension.e2e.connector.remotes.test_backend_controller.TestBackendRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.junit.multi.annotations.Consumer;
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.ApiWrapperConnectorRemote;
import de.sovity.edc.extension.e2e.junit.multi.CeE2eTestExtension;
import de.sovity.edc.extension.e2e.junit.multi.annotations.Provider;
import de.sovity.edc.extension.policy.AlwaysTruePolicyConstants;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.val;
import org.awaitility.Awaitility;
import org.eclipse.edc.protocol.dsp.http.spi.types.HttpMessageProtocol;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static de.sovity.edc.client.gen.model.ContractAgreementDirection.CONSUMING;
import static de.sovity.edc.client.gen.model.ContractAgreementDirection.PROVIDING;
import static de.sovity.edc.extension.e2e.connector.remotes.management_api.DataTransferTestUtil.validateDataTransferred;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;


class UiApiWrapperTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";

    @RegisterExtension
    private static CeE2eTestExtension e2eTestExtension = new CeE2eTestExtension();

    private TestBackendRemote dataAddress;

    @BeforeEach
    void setup(@Provider ManagementApiConnectorRemote providerConnector) {
        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new TestBackendRemote(providerConnector.getConfig().getDefaultApiUrl());
    }

    @DisabledOnGithub
    @Test
    void provide_consume_assetMapping_policyMapping_agreements(
        @Consumer ConnectorConfig consumerConfig,
        @Consumer ManagementApiConnectorRemote consumerConnector,
        @Consumer EdcClient consumerClient,
        @Provider ConnectorConfig providerConfig,
        @Provider EdcClient providerClient) {

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

        var assets = providerClient.uiApi().getAssetPage().getAssets();
        assertThat(assets).hasSize(1);
        var asset = assets.get(0);

        var providerProtocolEndpoint = providerConfig.getProtocolApiUrl();
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(providerProtocolEndpoint);
        assertThat(dataOffers).hasSize(1);
        var dataOffer = dataOffers.get(0);
        assertThat(dataOffer.getContractOffers()).hasSize(1);
        var contractOffer = dataOffer.getContractOffers().get(0);

        // act
        var negotiation = negotiate(consumerClient, consumerConnector, dataOffer, contractOffer);
        initiateTransfer(consumerClient, negotiation);
        var providerAgreements = providerClient.uiApi().getContractAgreementPage(null).getContractAgreements();
        var consumerAgreements = consumerClient.uiApi().getContractAgreementPage(null).getContractAgreements();

        // assert
        assertThat(dataOffer.getEndpoint()).isEqualTo(providerProtocolEndpoint);
        assertThat(dataOffer.getParticipantId()).isEqualTo(PROVIDER_PARTICIPANT_ID);
        assertThat(dataOffer.getAsset().getAssetId()).isEqualTo(assetId);
        assertThat(dataOffer.getAsset().getTitle()).isEqualTo("AssetName");
        assertThat(dataOffer.getAsset().getConnectorEndpoint()).isEqualTo(providerProtocolEndpoint);
        assertThat(dataOffer.getAsset().getParticipantId()).isEqualTo(providerConfig.getProperties().get("edc.participant.id"));
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
        assertThat(asset.getParticipantId()).isEqualTo(providerConfig.getProperties().get("edc.participant.id"));

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
        assertThat(providerAgreement.getCounterPartyAddress()).isEqualTo(consumerConfig.getProtocolApiUrl());
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
            .id("asset-1")
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
        assertThat(assetId).isEqualTo("asset-1");

        // act
        val assets = providerClient.uiApi().getAssetPage().getAssets();
        assertThat(assets).hasSize(1);
        val asset = assets.get(0);

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
        @Provider ConnectorConfig providerConfig,
        @Provider EdcClient providerClient) {

        // arrange
        var data = "expected data 123";

        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(dataAddress.getDataSourceUrl(data))
                .build())
            .build();

        var assetId = providerClient.uiApi().createAsset(UiAssetCreateRequest.builder()
            .id("asset-1")
            .dataSource(dataSource)
            .build()).getId();
        assertThat(assetId).isEqualTo("asset-1");

        var policyId = providerClient.uiApi().createPolicyDefinitionV2(PolicyDefinitionCreateDto.builder()
            .policyDefinitionId("policy-1")
            .expression(UiPolicyExpression.builder()
                .type(UiPolicyExpressionType.EMPTY)
                .build())
            .build()).getId();

        providerClient.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
            .contractDefinitionId("cd-1")
            .accessPolicyId(policyId)
            .contractPolicyId(policyId)
            .assetSelector(List.of())
            .build());

        val providerProtocolEndpoint = providerConfig.getProtocolApiUrl();
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(providerProtocolEndpoint);
        assertThat(dataOffers).hasSize(1);
        var dataOffer = dataOffers.get(0);
        assertThat(dataOffer.getContractOffers()).hasSize(1);
        var contractOffer = dataOffer.getContractOffers().get(0);

        // act
        var negotiation = negotiate(consumerClient, consumerConnector, dataOffer, contractOffer);
        var transferRequestJsonLd = Json.createObjectBuilder()
            .add(
                Prop.Edc.DATA_DESTINATION,
                getDatasinkPropertiesJsonObject()
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
        consumerClient.uiApi().initiateCustomTransfer(transferRequest);

        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), data);
    }

    @DisabledOnGithub
    @Test
    void editAssetOnLiveContract(
        @Consumer ManagementApiConnectorRemote consumerConnector,
        @Consumer EdcClient consumerClient,
        @Provider ConnectorConfig providerConfig,
        @Provider EdcClient providerClient) {

        // arrange
        var data = "expected data 123";

        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(dataAddress.getDataSourceUrl(data))
                .build())
            .build();

        var assetId = providerClient.uiApi().createAsset(UiAssetCreateRequest.builder()
            .id("asset-1")
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
            .contractDefinitionId("cd-1")
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

        val providerProtocolEndpoint = providerConfig.getProtocolApiUrl();
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(providerProtocolEndpoint);
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
        assertThat(consumerClient.uiApi().getCatalogPageDataOffers(providerProtocolEndpoint).get(0).getAsset().getTitle())
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
    void checkIdAvailability(ApiWrapperConnectorRemote scenario, @Provider EdcClient providerClient) {
        // arrange
        var assetId = scenario.createAsset();
        var policyId = "policy-id";
        scenario.createPolicy(policyId, OffsetDateTime.MIN, OffsetDateTime.MAX);
        var contractDefinitionId = scenario.createContractDefinition(policyId, assetId);

        val asset = providerClient.uiApi().getAssetPage();

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
        ApiWrapperConnectorRemote scenario,
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
        ApiWrapperConnectorRemote scenario,
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
            .id("asset")
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
    void canCreateDataOfferWithoutAnyNewPolicy(
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

        assertThat(getAllPoliciesExceptTheAlwaysTruePolicy(providerClient)).hasSize(0);

        assertThat(providerClient.uiApi().getContractDefinitionPage().getContractDefinitions())
            .extracting(ContractDefinitionEntry::getContractDefinitionId)
            .first()
            .isEqualTo(assetId);
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

        val assetId = "asset";
        val asset = UiAssetCreateRequest.builder()
            .dataSource(dataSource)
            .id(assetId)
            .title("My asset")
            .build();

        val dataOfferCreateRequest = new DataOfferCreationRequest(
            asset,
            DataOfferCreationRequest.PolicyEnum.DONT_PUBLISH,
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

        assertThat(getAllPoliciesExceptTheAlwaysTruePolicy(providerClient))
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
        ApiWrapperConnectorRemote scenario,
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
        assertThat(providerClient.uiApi().getAssetPage().getAssets())
            .hasSize(1)
            .extracting(UiAsset::getAssetId)
            .first()
            .isEqualTo(assetId);

        assertThat(getAllPoliciesExceptTheAlwaysTruePolicy(providerClient)).hasSize(0);
        assertThat(providerClient.uiApi().getContractDefinitionPage().getContractDefinitions()).hasSize(0);
    }

    @Test
    void dontCreateAnythingIfThePolicyAlreadyExists(
        ApiWrapperConnectorRemote scenario,
        @Provider EdcClient providerClient
    ) {
        // arrange
        val assetId = "assetId";
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
        assertThat(providerClient.uiApi().getAssetPage().getAssets()).hasSize(0);

        assertThat(getAllPoliciesExceptTheAlwaysTruePolicy(providerClient)).hasSize(1)
            .extracting(PolicyDefinitionDto::getPolicyDefinitionId)
            .first()
            .isEqualTo("assetId");

        assertThat(providerClient.uiApi().getContractDefinitionPage().getContractDefinitions()).hasSize(0);
    }

    @Test
    void dontCreateAnythingIfTheContractDefinitionAlreadyExists(
        ApiWrapperConnectorRemote scenario,
        @Provider EdcClient providerClient
    ) {
        // arrange
        val assetId = "assetId";
        val placeholder = scenario.createAsset();
        providerClient.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
            .contractDefinitionId(assetId)
            .accessPolicyId("always-true")
            .contractPolicyId("always-true")
            .assetSelector(List.of(UiCriterion.builder()
                .operandLeft(Prop.Edc.ID)
                .operator(UiCriterionOperator.EQ)
                .operandRight(UiCriterionLiteral.builder()
                    .type(UiCriterionLiteralType.VALUE)
                    .value(placeholder)
                    .build())
                .build()))
            .build());

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
        assertThat(providerClient.uiApi().getAssetPage().getAssets())
            // the asset used for the placeholder contract definition
            .hasSize(1)
            .extracting(UiAsset::getAssetId)
            .first()
            .isEqualTo(placeholder);

        assertThat(getAllPoliciesExceptTheAlwaysTruePolicy(providerClient)).hasSize(0);

        assertThat(providerClient.uiApi().getContractDefinitionPage().getContractDefinitions())
            .hasSize(1)
            .filteredOn(it -> it.getContractDefinitionId().equals(assetId))
            .extracting(ContractDefinitionEntry::getContractDefinitionId)
            .first()
            // the already existing one, before the data offer creation attempt
            .isEqualTo(assetId);
    }

    private static @NotNull List<PolicyDefinitionDto> getAllPoliciesExceptTheAlwaysTruePolicy(EdcClient edcClient) {
        return edcClient.uiApi().getPolicyDefinitionPage().getPolicies().stream().filter(it -> !it.getPolicyDefinitionId().equals(
            AlwaysTruePolicyConstants.POLICY_DEFINITION_ID)).toList();
    }

    private UiContractNegotiation negotiate(
        EdcClient consumerClient,
        ManagementApiConnectorRemote consumerConnector,
        UiDataOffer dataOffer,
        UiContractOffer contractOffer) {

        var negotiationRequest = ContractNegotiationRequest.builder()
            .counterPartyAddress(dataOffer.getEndpoint())
            .counterPartyParticipantId(dataOffer.getParticipantId())
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
