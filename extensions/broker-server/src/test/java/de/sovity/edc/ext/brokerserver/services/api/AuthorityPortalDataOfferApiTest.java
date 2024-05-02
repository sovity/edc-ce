/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.brokerserver.services.api;

import de.sovity.edc.ext.brokerserver.TestPolicy;
import de.sovity.edc.ext.brokerserver.client.gen.ApiException;
import de.sovity.edc.ext.brokerserver.client.gen.model.AuthorityPortalConnectorDataOfferInfo;
import de.sovity.edc.ext.brokerserver.client.gen.model.AuthorityPortalConnectorInfo;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static de.sovity.edc.ext.brokerserver.TestAsset.getAssetJsonLd;
import static de.sovity.edc.ext.brokerserver.TestAsset.setDataOfferAssetMetadata;
import static de.sovity.edc.ext.brokerserver.TestUtils.ADMIN_API_KEY;
import static de.sovity.edc.ext.brokerserver.TestUtils.brokerServerClient;
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@ApiTest
@ExtendWith(EdcExtension.class)
class AuthorityPortalDataOfferApiTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of()));
    }

    @Test
    void testAuthenticationOfEndpoints() {
        TEST_DATABASE.testTransaction(dsl -> {
            var code = assertThrows(ApiException.class, () -> brokerServerClient().brokerServerApi().getConnectorDataOffers(
                ADMIN_API_KEY + "invalid",
                List.of())).getCode();
            assertThat(code).isEqualTo(401);
        });
    }

    @Test
    void testConnectorMetadataByEndpoints() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var now = OffsetDateTime.now().withNano(0);

            createConnector(dsl, now, 1);
            createDataOffer(dsl, now, 1, 1);
            createDataOffer(dsl, now, 1, 2);

            createConnector(dsl, now, 2);
            createDataOffer(dsl, now, 2, 1);

            createConnector(dsl, now, 4);

            // act
            var actual = brokerServerClient().brokerServerApi().getConnectorDataOffers(
                ADMIN_API_KEY,
                Arrays.asList(
                    getEndpoint(1),
                    getEndpoint(2),
                    getEndpoint(4)
                ));

            // assert
            // connector 1 with two data offer
            var connector1 = forConnector(actual, 1);
            assertThat(connector1.getConnectorEndpoint()).isEqualTo(getEndpoint(1));
            assertThat(connector1.getParticipantId()).isEqualTo("my-connector");
            assertThat(connector1.getOnlineStatus().getValue()).isEqualTo(AuthorityPortalConnectorInfo.OnlineStatusEnum.ONLINE.getValue());
            assertThat(connector1.getOfflineSinceOrLastUpdatedAt()).isEqualTo(now);
            assertThat(connector1.getDataOffers().size()).isEqualTo(2);
            var connector1asset1 = connector1.getDataOffers().stream().filter(dataOffer -> dataOffer.getDataOfferId().equals(getAssetId(1))).findFirst().orElseThrow();
            assertThat(connector1asset1.getDataOfferId()).isEqualTo("my-asset-1");
            assertThat(connector1asset1.getDataOfferName()).isEqualTo("my-asset-1");
            var connector1asset2 = connector1.getDataOffers().stream().filter(dataOffer -> dataOffer.getDataOfferId().equals(getAssetId(2))).findFirst().orElseThrow();
            assertThat(connector1asset2.getDataOfferId()).isEqualTo("my-asset-2");
            assertThat(connector1asset2.getDataOfferName()).isEqualTo("my-asset-2");

            // connector 2 with one data offer
            var connector2 = forConnector(actual, 2);
            assertThat(connector2.getConnectorEndpoint()).isEqualTo(getEndpoint(2));
            assertThat(connector2.getParticipantId()).isEqualTo("my-connector");
            assertThat(connector2.getOnlineStatus().getValue()).isEqualTo(AuthorityPortalConnectorInfo.OnlineStatusEnum.ONLINE.getValue());
            assertThat(connector2.getOfflineSinceOrLastUpdatedAt()).isEqualTo(now);
            assertThat(connector2.getDataOffers().size()).isEqualTo(1);
            var connector2asset1 = connector2.getDataOffers().stream().filter(dataOffer -> dataOffer.getDataOfferId().equals(getAssetId(1))).findFirst().orElseThrow();
            assertThat(connector2asset1.getDataOfferId()).isEqualTo("my-asset-1");
            assertThat(connector2asset1.getDataOfferName()).isEqualTo("my-asset-1");

            // connector 4 without data offers
            var connector4 = forConnector(actual, 4);
            assertThat(connector4.getConnectorEndpoint()).isEqualTo(getEndpoint(4));
            assertThat(connector4.getParticipantId()).isEqualTo("my-connector");
            assertThat(connector4.getOnlineStatus().getValue()).isEqualTo(AuthorityPortalConnectorInfo.OnlineStatusEnum.ONLINE.getValue());
            assertThat(connector4.getOfflineSinceOrLastUpdatedAt()).isEqualTo(now);
            assertThat(connector4.getDataOffers().size()).isEqualTo(0);
        });
    }

    private AuthorityPortalConnectorDataOfferInfo forConnector(List<AuthorityPortalConnectorDataOfferInfo> actual, int iConnector) {
        return actual.stream()
            .filter(connectorMetadata ->
                getEndpoint(iConnector).equals(connectorMetadata.getConnectorEndpoint())
            )
            .findFirst()
            .orElseThrow();
    }

    private void createConnector(DSLContext dsl, OffsetDateTime now, int iConnector) {
        var connector = dsl.newRecord(Tables.CONNECTOR);
        connector.setParticipantId("my-connector");
        connector.setEndpoint(getEndpoint(iConnector));
        connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        connector.setCreatedAt(now.minusDays(1));
        connector.setLastRefreshAttemptAt(now);
        connector.setLastSuccessfulRefreshAt(now);
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        connector.insert();
    }

    private String getEndpoint(int iConnector) {
        return "https://connector-%d".formatted(iConnector);
    }

    private String getAssetId(int iDataOffer) {
        return "my-asset-%d".formatted(iDataOffer);
    }

    private void createDataOffer(DSLContext dsl, OffsetDateTime now, int iConnector, int iDataOffer) {
        var connectorEndpoint = getEndpoint(iConnector);
        var assetJsonLd = getAssetJsonLd(getAssetId(iDataOffer));

        var dataOffer = dsl.newRecord(Tables.DATA_OFFER);
        setDataOfferAssetMetadata(dataOffer, assetJsonLd, "my-participant-id");
        dataOffer.setConnectorEndpoint(connectorEndpoint);
        dataOffer.setCreatedAt(now.minusDays(5));
        dataOffer.setUpdatedAt(now);
        dataOffer.insert();

        var contractOffer = dsl.newRecord(Tables.CONTRACT_OFFER);
        contractOffer.setContractOfferId("my-contract-offer-1");
        contractOffer.setConnectorEndpoint(connectorEndpoint);
        contractOffer.setAssetId(dataOffer.getAssetId());
        contractOffer.setCreatedAt(now.minusDays(5));
        contractOffer.setUpdatedAt(now);
        contractOffer.setPolicy(TestPolicy.createAfterYesterdayPolicyJson());
        contractOffer.insert();
    }
}
