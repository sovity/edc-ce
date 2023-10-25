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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.brokerserver.dao.AssetProperty;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import lombok.SneakyThrows;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.model.Policy;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;

import static de.sovity.edc.ext.brokerserver.TestUtils.brokerServerClient;
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static groovy.json.JsonOutput.toJson;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class DataOfferCountApiTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of()));
    }

    @Test
    void testCountByEndpoints() {
        TEST_DATABASE.testTransaction(dsl -> {
            var now = OffsetDateTime.now().withNano(0);

            createConnector(dsl, now, 1);
            createDataOffer(dsl, now, 1, 1);
            createDataOffer(dsl, now, 1, 2);

            createConnector(dsl, now, 2);
            createDataOffer(dsl, now, 2, 1);

            createConnector(dsl, now, 3);
            createDataOffer(dsl, now, 3, 1);

            createConnector(dsl, now, 4);

            var actual = brokerServerClient().brokerServerApi().dataOfferCount(Arrays.asList(
                    getEndpoint(1),
                    getEndpoint(1), // having this twice should not crash the query
                    getEndpoint(2),
                    getEndpoint(4)
            ));
            var dataOfferCountMap = actual.getDataOfferCount();
            assertThat(dataOfferCountMap).isEqualTo(Map.of(
                    getEndpoint(1), 2,
                    getEndpoint(2), 1,
                    getEndpoint(4), 0
            ));
        });
    }

    private void createConnector(DSLContext dsl, OffsetDateTime today, int iConnector) {
        var connector = dsl.newRecord(Tables.CONNECTOR);
        connector.setConnectorId("https://my-connector");
        connector.setEndpoint(getEndpoint(iConnector));
        connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        connector.setCreatedAt(today.minusDays(1));
        connector.setLastRefreshAttemptAt(today);
        connector.setLastSuccessfulRefreshAt(today);
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        connector.insert();
    }

    private String getEndpoint(int iConnector) {
        return "https://connector-%d".formatted(iConnector);
    }

    private void createDataOffer(DSLContext dsl, OffsetDateTime today, int iConnector, int iDataOffer) {
        var connectorEndpoint = getEndpoint(iConnector);
        var assetProperties = Map.of(
                AssetProperty.ASSET_ID, "urn:artifact:my-asset-%d".formatted(iDataOffer)
        );

        var dataOffer = dsl.newRecord(Tables.DATA_OFFER);
        dataOffer.setAssetId(assetProperties.get(AssetProperty.ASSET_ID));
        dataOffer.setAssetName(assetProperties.getOrDefault(AssetProperty.ASSET_NAME, dataOffer.getAssetId()));
        dataOffer.setAssetProperties(JSONB.jsonb(assetProperties(assetProperties)));
        dataOffer.setConnectorEndpoint(connectorEndpoint);
        dataOffer.setCreatedAt(today.minusDays(5));
        dataOffer.setUpdatedAt(today);
        dataOffer.insert();

        var contractOffer = dsl.newRecord(Tables.DATA_OFFER_CONTRACT_OFFER);
        contractOffer.setContractOfferId("my-contract-offer-1");
        contractOffer.setConnectorEndpoint(connectorEndpoint);
        contractOffer.setAssetId(assetProperties.get(AssetProperty.ASSET_ID));
        contractOffer.setCreatedAt(today.minusDays(5));
        contractOffer.setUpdatedAt(today);
        contractOffer.setPolicy(JSONB.jsonb(policyToJson(dummyPolicy())));
        contractOffer.insert();
    }

    private Policy dummyPolicy() {
        return Policy.Builder.newInstance()
                .assignee("Example Assignee")
                .build();
    }

    private String policyToJson(Policy policy) {
        return toJson(policy);
    }

    @SneakyThrows
    private String assetProperties(Map<String, String> assetProperties) {
        return new ObjectMapper().writeValueAsString(assetProperties);
    }
}
