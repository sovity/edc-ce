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
import de.sovity.edc.client.gen.model.ContractAgreementDirection;
import de.sovity.edc.client.gen.model.TransferProcessSimplifiedState;
import de.sovity.edc.ext.wrapper.utils.EdcDateUtils;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Predicate;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test data offers and contracts of an MS8 connector migrated to the current version.
 */
class Ms8ConnectorMigrationTest {

    private static final String PROVIDER_PARTICIPANT_ID = "example-provider";
    private static final String CONSUMER_PARTICIPANT_ID = "example-connector";

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
        providerConfig.setProperty("edc.flyway.additional.migration.locations",
                "filesystem:%s".formatted(getAbsoluteTestResourcePath("db/additional-test-data/provider")));
        providerEdcContext.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

        providerClient = EdcClient.builder()
                .managementApiUrl(providerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(providerConfig.getProperties().get("edc.api.auth.key"))
                .build();

        var consumerConfig = forTestDatabase(CONSUMER_PARTICIPANT_ID, 23000, CONSUMER_DATABASE);
        consumerConfig.setProperty("edc.flyway.additional.migration.locations",
                "filesystem:%s".formatted(getAbsoluteTestResourcePath("db/additional-test-data/consumer")));
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
    void testMs8DataOffer_Properties() {
        // arrange
        var providerEndpoint = endpoint(providerConnector);

        // act
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(providerEndpoint);
        var asset = first(dataOffers, it -> it.getAsset().getAssetId().equals("first-asset-1.0")).getAsset();

        // assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(asset.getAssetId()).isEqualTo("first-asset-1.0");
            softly.assertThat(asset.getAssetJsonLd()).startsWith("{").endsWith("}");
            softly.assertThat(asset.getCreatorOrganizationName()).isEqualTo("Example GmbH");
            softly.assertThat(asset.getDataCategory()).isEqualTo("Traffic Information");
            softly.assertThat(asset.getDataModel()).isEqualTo("data-model");
            softly.assertThat(asset.getDataSubcategory()).isEqualTo("Accidents");
            softly.assertThat(asset.getDescription()).isEqualTo("My First Asset");
            softly.assertThat(asset.getGeoReferenceMethod()).isEqualTo("geo-ref");
            softly.assertThat(asset.getHttpDatasourceHintsProxyBody()).isFalse();
            softly.assertThat(asset.getHttpDatasourceHintsProxyMethod()).isFalse();
            softly.assertThat(asset.getHttpDatasourceHintsProxyPath()).isFalse();
            softly.assertThat(asset.getHttpDatasourceHintsProxyQueryParams()).isFalse();
            softly.assertThat(asset.getKeywords()).containsExactlyInAnyOrder("first", "asset");
            softly.assertThat(asset.getLandingPageUrl()).isEqualTo("https://endpoint-documentation");
            softly.assertThat(asset.getLanguage()).isEqualTo("https://w3id.org/idsa/code/EN");
            softly.assertThat(asset.getLicenseUrl()).isEqualTo("https://standard-license");
            softly.assertThat(asset.getMediaType()).isEqualTo("text/plain");
            softly.assertThat(asset.getTitle()).isEqualTo("First Asset");
            softly.assertThat(asset.getPublisherHomepage()).isEqualTo("https://publisher");
            softly.assertThat(asset.getTransportMode()).isEqualTo("Rail");
            softly.assertThat(asset.getVersion()).isEqualTo("1.0");
        });
    }

    @Test
    void testMs8ProvidingTransferProcess() {
        // arrange

        // act
        var providerTransfers = providerClient.uiApi().getTransferHistoryPage().getTransferEntries();
        assertThat(providerTransfers).hasSize(1);
        var providerTransfer = providerTransfers.get(0);

        // assert
        assertThat(providerTransfer.getAssetId()).isEqualTo("first-asset-1.0");
        assertThat(providerTransfer.getAssetName()).isEqualTo("First Asset");
        assertThat(providerTransfer.getContractAgreementId()).isEqualTo("Zmlyc3QtY2Q=:Zmlyc3QtYXNzZXQtMS4w:MjgzNTZkMTMtN2ZhYy00NTQwLTgwZjItMjI5NzJjOTc1ZWNi");
        assertThat(providerTransfer.getCounterPartyConnectorEndpoint()).isEqualTo(endpoint(consumerConnector));
        assertThat(providerTransfer.getCounterPartyParticipantId()).isEqualTo(consumerConnector.getParticipantId());
        assertIsEqualOffsetDateTime(providerTransfer.getCreatedDate(), EdcDateUtils.utcMillisToOffsetDateTime(1695208010855L));
        assertThat(providerTransfer.getDirection()).isEqualTo(ContractAgreementDirection.PROVIDING);
        assertThat(providerTransfer.getErrorMessage()).isNull();
        assertIsEqualOffsetDateTime(providerTransfer.getLastUpdatedDate(), EdcDateUtils.utcMillisToOffsetDateTime(1695208010083L));
        assertThat(providerTransfer.getState().getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
        assertThat(providerTransfer.getTransferProcessId()).isEqualTo("27075fc4-b18f-44e1-8bde-a9f62817dab2");
    }

    private void assertIsEqualOffsetDateTime(OffsetDateTime actual, OffsetDateTime expected) {
        assertThat(actual).isCloseTo(expected, new TemporalUnitLessThanOffset(1, ChronoUnit.MINUTES));
    }

    @Test
    void testMs8ConsumingTransferProcess() {
        // arrange

        // act
        var consumerTransfers = consumerClient.uiApi().getTransferHistoryPage().getTransferEntries();
        assertThat(consumerTransfers).hasSize(1);
        var consumerTransfer = consumerTransfers.get(0);

        // assert
        assertThat(consumerTransfer.getAssetId()).isEqualTo("first-asset-1.0");
        assertThat(consumerTransfer.getAssetName()).isEqualTo("first-asset-1.0");
        assertThat(consumerTransfer.getContractAgreementId()).isEqualTo("Zmlyc3QtY2Q=:Zmlyc3QtYXNzZXQtMS4w:MjgzNTZkMTMtN2ZhYy00NTQwLTgwZjItMjI5NzJjOTc1ZWNi");
        assertThat(consumerTransfer.getCounterPartyConnectorEndpoint()).isEqualTo(endpoint(providerConnector));
        assertThat(consumerTransfer.getCounterPartyParticipantId()).isEqualTo(providerConnector.getParticipantId());
        assertIsEqualOffsetDateTime(consumerTransfer.getCreatedDate(), EdcDateUtils.utcMillisToOffsetDateTime(1695208008652L));
        assertThat(consumerTransfer.getDirection()).isEqualTo(ContractAgreementDirection.CONSUMING);
        assertThat(consumerTransfer.getErrorMessage()).isNull();
        assertIsEqualOffsetDateTime(consumerTransfer.getLastUpdatedDate(), EdcDateUtils.utcMillisToOffsetDateTime(1695208011094L));
        assertThat(consumerTransfer.getState().getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
        assertThat(consumerTransfer.getTransferProcessId()).isEqualTo("946aadd4-d4bf-47e9-8aea-c2279070e839");
    }

    @Test
    void testMs8DataOffer_negotiateAndTransferNewContract() {
        // arrange
        var assetIds = providerConnector.getAssetIds();
        assertThat(assetIds).contains("second-asset");

        // act
        consumerConnector.consumeOffer(
                providerConnector.getParticipantId(),
                providerConnector.getConfig().getProtocolEndpoint().getUri(),
                "second-asset",
                dataAddress.getDataSinkJsonLd());

        // assert
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), "second-asset-data");
    }

    @Test
    void testMs8Contract_transfer() {
        // arrange
        var assetIds = providerConnector.getAssetIds();
        assertThat(assetIds).contains("second-asset");

        // act
        var transferProcessId = consumerConnector.initiateTransfer(
                "Zmlyc3QtY2Q=:Zmlyc3QtYXNzZXQtMS4w:MjgzNTZkMTMtN2ZhYy00NTQwLTgwZjItMjI5NzJjOTc1ZWNi",
                "first-asset-1.0",
                providerConnector.getConfig().getProtocolEndpoint().getUri(),
                dataAddress.getDataSinkJsonLd()
        );

        // assert
        assertThat(transferProcessId).isNotNull();
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), "first-asset-data");
    }

    private <T> T first(List<T> items, Predicate<T> predicate) {
        return items.stream().filter(predicate).findFirst().get();
    }

    private String endpoint(ConnectorRemote remote) {
        return remote.getConfig().getProtocolEndpoint().getUri().toString();
    }

    public String getAbsoluteTestResourcePath(String path) {
        return Paths.get("").resolve("src/test/resources").resolve(path).toAbsolutePath().toString();
    }
}
