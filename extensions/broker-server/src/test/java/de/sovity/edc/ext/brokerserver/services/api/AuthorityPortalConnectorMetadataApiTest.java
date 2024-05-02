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

@ApiTest
@ExtendWith(EdcExtension.class)
class AuthorityPortalConnectorMetadataApiTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of()));
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

            createConnector(dsl, now, 3);
            createDataOffer(dsl, now, 3, 1);

            createConnector(dsl, now, 4);

            // act
            var actual = brokerServerClient().brokerServerApi().getConnectorMetadata(
                ADMIN_API_KEY,
                Arrays.asList(
                    getEndpoint(1),
                    getEndpoint(1), // having this twice should not crash the query
                    getEndpoint(2),
                    getEndpoint(4),
                    getEndpoint(5) // having this not existing should not crash the query
            ));

            // assert
            var first = forConnector(actual, 1);
            assertThat(first.getParticipantId()).isEqualTo("my-connector");
            assertThat(first.getDataOfferCount()).isEqualTo(2);
            assertThat(first.getOnlineStatus()).isEqualTo(AuthorityPortalConnectorInfo.OnlineStatusEnum.ONLINE);
            assertThat(first.getOfflineSinceOrLastUpdatedAt()).isEqualTo(now);
            var second = forConnector(actual, 2);
            assertThat(second.getDataOfferCount()).isEqualTo(1);
            assertThat(second.getOnlineStatus()).isEqualTo(AuthorityPortalConnectorInfo.OnlineStatusEnum.ONLINE);
            assertThat(second.getOfflineSinceOrLastUpdatedAt()).isEqualTo(now);
            var fourth = forConnector(actual, 4);
            assertThat(fourth.getDataOfferCount()).isEqualTo(0);
            assertThat(fourth.getOnlineStatus()).isEqualTo(AuthorityPortalConnectorInfo.OnlineStatusEnum.ONLINE);
            assertThat(fourth.getOfflineSinceOrLastUpdatedAt()).isEqualTo(now);
        });
    }

    private AuthorityPortalConnectorInfo forConnector(List<AuthorityPortalConnectorInfo> actual, int iConnector) {
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

    private void createDataOffer(DSLContext dsl, OffsetDateTime now, int iConnector, int iDataOffer) {
        var connectorEndpoint = getEndpoint(iConnector);
        var assetJsonLd = getAssetJsonLd("my-asset-%d".formatted(iDataOffer));

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
