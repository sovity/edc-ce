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

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.InitiateCustomTransferRequest;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.TransferProcessSimplifiedState;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetEditMetadataRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiContractOffer;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataOffer;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.val;
import org.awaitility.Awaitility;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.protocol.dsp.spi.types.HttpMessageProtocol;
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
import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class UiApiWrapperTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    @RegisterExtension
    static final TestDatabase PROVIDER_DATABASE = new TestDatabaseViaTestcontainers();
    @RegisterExtension
    static final TestDatabase CONSUMER_DATABASE = new TestDatabaseViaTestcontainers();

    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;

    private EdcClient providerClient;
    private EdcClient consumerClient;
    private MockDataAddressRemote dataAddress;

    @BeforeEach
    void setup() {
        var providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, 21000, PROVIDER_DATABASE);
        providerEdcContext.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

        providerClient = EdcClient.builder()
                .managementApiUrl(providerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(providerConfig.getProperties().get("edc.api.auth.key"))
                .build();

        var consumerConfig = forTestDatabase(CONSUMER_PARTICIPANT_ID, 23000, CONSUMER_DATABASE);
        consumerEdcContext.setConfiguration(consumerConfig.getProperties());
        consumerConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));

        consumerClient = EdcClient.builder()
                .managementApiUrl(consumerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
                .build();

        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new MockDataAddressRemote(providerConnector.getConfig().getDefaultEndpoint());
    }

    @Test
    void provide_consume_assetMapping_policyMapping_agreements() {
        // arrange
        var data = "expected data 123";
        var yesterday = OffsetDateTime.now().minusDays(1);

        var constraintRequest = UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.GT)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteralType.STRING)
                        .value(yesterday.toString())
                        .build())
                .build();

        var policyId = providerClient.uiApi().createPolicyDefinition(PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId("policy-1")
                .policy(UiPolicyCreateRequest.builder()
                        .constraints(List.of(constraintRequest))
                        .build())
                .build()).getId();

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
                .nutsLocation(Arrays.asList("my-nuts-location1", "my-nuts-location2"))
                .dataSampleUrls(Arrays.asList("my-data-sample-urls1", "my-data-sample-urls2"))
                .referenceFileUrls(Arrays.asList("my-reference-files1", "my-reference-files2"))
                .referenceFilesDescription("my-additional-description")
                .conditionsForUse("my-conditions-for-use")
                .dataUpdateFrequency("my-data-update-frequency")
                .temporalCoverageFrom(LocalDate.parse("2007-12-03"))
                .temporalCoverageToInclusive(LocalDate.parse("2024-01-22"))
                .keywords(List.of("keyword1", "keyword2"))
                .publisherHomepage("publisherHomepage")
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.METHOD, "GET",
                        Prop.Edc.BASE_URL, dataAddress.getDataSourceUrl(data)
                ))
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

        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(getProtocolEndpoint(providerConnector));
        assertThat(dataOffers).hasSize(1);
        var dataOffer = dataOffers.get(0);
        assertThat(dataOffer.getContractOffers()).hasSize(1);
        var contractOffer = dataOffer.getContractOffers().get(0);

        // act
        var negotiation = negotiate(dataOffer, contractOffer);
        initiateTransfer(negotiation);
        var providerAgreements = providerClient.uiApi().getContractAgreementPage().getContractAgreements();
        var consumerAgreements = consumerClient.uiApi().getContractAgreementPage().getContractAgreements();

        // assert
        assertThat(dataOffer.getEndpoint()).isEqualTo(getProtocolEndpoint(providerConnector));
        assertThat(dataOffer.getParticipantId()).isEqualTo(PROVIDER_PARTICIPANT_ID);
        assertThat(dataOffer.getAsset().getAssetId()).isEqualTo(assetId);
        assertThat(dataOffer.getAsset().getTitle()).isEqualTo("AssetName");
        assertThat(dataOffer.getAsset().getConnectorEndpoint()).isEqualTo(getProtocolEndpoint(providerConnector));
        assertThat(dataOffer.getAsset().getParticipantId()).isEqualTo(providerConnector.getParticipantId());
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
        assertThat(dataOffer.getAsset().getNutsLocation()).isEqualTo(Arrays.asList("my-nuts-location1", "my-nuts-location2"));
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
        assertThat(asset.getConnectorEndpoint()).isEqualTo(getProtocolEndpoint(providerConnector));
        assertThat(asset.getParticipantId()).isEqualTo(providerConnector.getParticipantId());

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
        assertThat(providerAgreement.getCounterPartyAddress()).isEqualTo("http://localhost:23003/api/dsp");
        assertThat(providerAgreement.getCounterPartyId()).isEqualTo(CONSUMER_PARTICIPANT_ID);

        assertThat(providerAgreement.getAsset().getAssetId()).isEqualTo(assetId);
        var providingContractPolicyConstraint = providerAgreement.getContractPolicy().getConstraints().get(0);
        assertThat(providingContractPolicyConstraint).usingRecursiveComparison().isEqualTo(providingContractPolicyConstraint);

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

        var consumingContractPolicyConstraint = consumerAgreement.getContractPolicy().getConstraints().get(0);
        assertThat(consumingContractPolicyConstraint).usingRecursiveComparison().isEqualTo(consumingContractPolicyConstraint);

        assertThat(consumerAgreement.getAsset().getAssetId()).isEqualTo(assetId);
        assertThat(consumerAgreement.getAsset().getTitle()).isEqualTo(assetId);

        // Test Policy
        assertThat(contractOffer.getPolicy().getConstraints()).hasSize(1);
        var constraint = contractOffer.getPolicy().getConstraints().get(0);
        assertThat(constraint.getLeft()).isEqualTo("POLICY_EVALUATION_TIME");
        assertThat(constraint.getOperator()).isEqualTo(OperatorDto.GT);
        assertThat(constraint.getRight().getType()).isEqualTo(UiPolicyLiteralType.STRING);
        assertThat(constraint.getRight().getValue()).isEqualTo(yesterday.toString());

        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), data);

        validateTransferProcessesOk();
    }

    @Test
    void canOverrideTheWellKnowPropertiesUsingTheCustomProperties() {
        // arrange
        var assetId = providerClient.uiApi().createAsset(UiAssetCreateRequest.builder()
                .id("asset-1")
                .title("will be overridden")
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.METHOD, "GET",
                        Prop.Edc.BASE_URL, "http://example.com/base"
                ))
                .customJsonLdAsString("""
                        {
                            "http://purl.org/dc/terms/title": "The real title",
                            "https://semantic.sovity.io/mds-dcat-ext#nuts-location": ["a", "b", "c"],
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
        assertThat(asset.getNutsLocation()).isEqualTo(List.of("a", "b", "c"));
        // remaining custom property
        assertThatJson(asset.getCustomJsonLdAsString()).isEqualTo("""
                {
                    "http://example.com/an-actual-custom-property": "custom value"
                }
                """);
    }

    // TODO throw an error if the id is overridden

    @Test
    void customTransferRequest() {
        // arrange
        var data = "expected data 123";

        var assetId = providerClient.uiApi().createAsset(UiAssetCreateRequest.builder()
                .id("asset-1")
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.METHOD, "GET",
                        Prop.Edc.BASE_URL, dataAddress.getDataSourceUrl(data)
                ))
                .build()).getId();
        assertThat(assetId).isEqualTo("asset-1");

        var policyId = providerClient.uiApi().createPolicyDefinition(PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId("policy-1")
                .policy(UiPolicyCreateRequest.builder()
                        .constraints(List.of())
                        .build())
                .build()).getId();

        providerClient.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
                .contractDefinitionId("cd-1")
                .accessPolicyId(policyId)
                .contractPolicyId(policyId)
                .assetSelector(List.of())
                .build());

        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(getProtocolEndpoint(providerConnector));
        assertThat(dataOffers).hasSize(1);
        var dataOffer = dataOffers.get(0);
        assertThat(dataOffer.getContractOffers()).hasSize(1);
        var contractOffer = dataOffer.getContractOffers().get(0);

        // act
        var negotiation = negotiate(dataOffer, contractOffer);
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

    @Test
    void editAssetMetadataOnLiveContract() {
        // arrange
        var data = "expected data 123";

        var assetId = providerClient.uiApi().createAsset(UiAssetCreateRequest.builder()
                .id("asset-1")
                .title("Bad Asset Title")
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.METHOD, "GET",
                        Prop.Edc.BASE_URL, dataAddress.getDataSourceUrl(data)
                ))
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

        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(getProtocolEndpoint(providerConnector));
        assertThat(dataOffers).hasSize(1);
        var dataOffer = dataOffers.get(0);
        assertThat(dataOffer.getContractOffers()).hasSize(1);
        var contractOffer = dataOffer.getContractOffers().get(0);
        var negotiation = negotiate(dataOffer, contractOffer);

        // act
        providerClient.uiApi().editAssetMetadata(assetId, UiAssetEditMetadataRequest.builder()
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
        initiateTransfer(negotiation);

        // assert
        assertThat(consumerClient.uiApi().getCatalogPageDataOffers(getProtocolEndpoint(providerConnector)).get(0).getAsset().getTitle()).isEqualTo("Good Asset Title");
        val firstAsset = providerClient.uiApi().getContractAgreementPage().getContractAgreements().get(0).getAsset();
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
        validateTransferProcessesOk();
        assertThat(providerClient.uiApi().getTransferHistoryPage().getTransferEntries().get(0).getAssetName()).isEqualTo("Good Asset Title");
    }

    private UiContractNegotiation negotiate(UiDataOffer dataOffer, UiContractOffer contractOffer) {
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

    private void initiateTransfer(UiContractNegotiation negotiation) {
        var contractAgreementId = negotiation.getContractAgreementId();
        var transferRequest = InitiateTransferRequest.builder()
                .contractAgreementId(contractAgreementId)
                .dataSinkProperties(dataAddress.getDataSinkProperties())
                .build();
        consumerClient.uiApi().initiateTransfer(transferRequest);
    }

    private void validateTransferProcessesOk() {
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
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

    private String getProtocolEndpoint(ConnectorRemote connector) {
        return connector.getConfig().getProtocolEndpoint().getUri().toString();
    }
}
