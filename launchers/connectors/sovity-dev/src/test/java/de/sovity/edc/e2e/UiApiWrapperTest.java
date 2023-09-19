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
import de.sovity.edc.client.gen.model.ContractAgreementTransferRequest;
import de.sovity.edc.client.gen.model.ContractAgreementTransferRequestParams;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationState.SimplifiedStateEnum;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiContractOffer;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiDataOffer;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.awaitility.Awaitility;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Map;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;

class UiApiWrapperTest {

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
    private MockDataAddressRemote dataAddress;

    @BeforeEach
    void setup() {
        var providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, 22000, PROVIDER_DATABASE);
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
    void provide_consume_assetMapping_policyMapping() {
        // arrange
        var data = "expected data 123";

        var policyId = providerClient.uiApi().createPolicyDefinition(PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId("policy-1")
                .policy(UiPolicyCreateRequest.builder()
                        .constraints(List.of())
                        .build())
                .build()).getId();

        var assetId = providerClient.uiApi().createAsset(UiAssetCreateRequest.builder()
                .id("asset-1")
                .name("AssetName")
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
                .keywords(List.of("keyword1", "keyword2"))
                .creatorOrganizationName("creatorOrganizationName")
                .publisherHomepage("publisherHomepage")
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.METHOD, "GET",
                        Prop.Edc.BASE_URL, dataAddress.getDataSourceUrl(data)
                ))
                .build()).getId();
        assertThat(assetId).isEqualTo("asset-1");

        providerClient.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
                .contractDefinitionId("cd-1")
                .accessPolicyId(policyId)
                .contractPolicyId(policyId)
                .assetSelector(List.of(UiCriterion.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(UiCriterion.OperatorEnum.EQ)
                        .operandRight(UiCriterionLiteral.builder()
                                .type(UiCriterionLiteral.TypeEnum.VALUE)
                                .value(assetId)
                                .build())
                        .build()))
                .build());

        var dataOffers = consumerClient.uiApi().catalogPageDataOffers(getProtocolEndpoint(providerConnector));
        assertThat(dataOffers).hasSize(1);
        var dataOffer = dataOffers.get(0);
        assertThat(dataOffer.getContractOffers()).hasSize(1);
        var contractOffer = dataOffer.getContractOffers().get(0);

        // act
        var negotiation = negotiate(dataOffer, contractOffer);
        initiateTransfer(negotiation);

        // assert
        assertThat(dataOffer.getEndpoint()).isEqualTo(getProtocolEndpoint(providerConnector));
        assertThat(dataOffer.getParticipantId()).isEqualTo(PROVIDER_PARTICIPANT_ID);
        assertThat(dataOffer.getAsset().getAssetId()).isEqualTo(assetId);
        assertThat(dataOffer.getAsset().getKeywords()).isEqualTo(List.of("keyword1", "keyword2"));
        assertThat(dataOffer.getAsset().getName()).isEqualTo("AssetName");
        assertThat(dataOffer.getAsset().getDescription()).isEqualTo("AssetDescription");
        assertThat(dataOffer.getAsset().getVersion()).isEqualTo("1.0.0");
        assertThat(dataOffer.getAsset().getLanguage()).isEqualTo("en");
        assertThat(dataOffer.getAsset().getMediaType()).isEqualTo("application/json");
        assertThat(dataOffer.getAsset().getDataCategory()).isEqualTo("dataCategory");
        assertThat(dataOffer.getAsset().getDataSubcategory()).isEqualTo("dataSubcategory");
        assertThat(dataOffer.getAsset().getDataModel()).isEqualTo("dataModel");
        assertThat(dataOffer.getAsset().getGeoReferenceMethod()).isEqualTo("geoReferenceMethod");
        assertThat(dataOffer.getAsset().getTransportMode()).isEqualTo("transportMode");
        assertThat(dataOffer.getAsset().getLicenseUrl()).isEqualTo("https://license-url");
        assertThat(dataOffer.getAsset().getKeywords()).isEqualTo(List.of("keyword1", "keyword2"));
        assertThat(dataOffer.getAsset().getCreatorOrganizationName()).isEqualTo("creatorOrganizationName");
        assertThat(dataOffer.getAsset().getPublisherHomepage()).isEqualTo("publisherHomepage");
        assertThat(dataOffer.getAsset().getHttpDatasourceHintsProxyMethod()).isFalse();
        assertThat(dataOffer.getAsset().getHttpDatasourceHintsProxyPath()).isFalse();
        assertThat(dataOffer.getAsset().getHttpDatasourceHintsProxyQueryParams()).isFalse();
        assertThat(dataOffer.getAsset().getHttpDatasourceHintsProxyBody()).isFalse();
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), data);
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
                it -> it.getState().getSimplifiedState() != SimplifiedStateEnum.IN_PROGRESS
        );

        assertThat(negotiation.getState().getSimplifiedState()).isEqualTo(SimplifiedStateEnum.AGREED);
        return negotiation;
    }

    private void initiateTransfer(UiContractNegotiation negotiation) {
        var contractAgreementId = negotiation.getContractAgreementId();
        var transferRequest = ContractAgreementTransferRequest.builder()
                .type(ContractAgreementTransferRequest.TypeEnum.PARAMS_ONLY)
                .params(ContractAgreementTransferRequestParams.builder()
                        .contractAgreementId(contractAgreementId)
                        .dataSinkProperties(dataAddress.getDataSinkProperties())
                        .build())
                .build();
        consumerClient.uiApi().initiateTransfer(transferRequest);
    }

    private String getProtocolEndpoint(ConnectorRemote connector) {
        return connector.getConfig().getProtocolEndpoint().getUri().toString();
    }
}
