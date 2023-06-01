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
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.ext.brokerserver.services.refreshing.offers;

import de.sovity.edc.ext.brokerserver.BrokerServerExtension;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.brokerserver.services.logging.ConnectorChangeTracker;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferWriterTestDataModels.Co;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferWriterTestDataModels.Do;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class DataOfferWriterTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of(
                BrokerServerExtension.KNOWN_CONNECTORS, "https://example.com/ids/data",
                BrokerServerExtension.NUM_THREADS, "0"
        )));
    }

    @Test
    void testDataOfferWriter_allSortsOfUpdates() {
        TEST_DATABASE.testTransaction(dsl -> {
            var testDydi = new DataOfferWriterTestDydi();
            var testData = new DataOfferWriterTestDataHelper();
            var changes = new ConnectorChangeTracker();
            var dataOfferWriter = testDydi.getDataOfferWriter();

            // arrange
            var unchanged = Do.forName("unchanged");
            testData.existing(unchanged);
            testData.fetched(unchanged);

            var fieldChangedExisting = Do.forName("fieldChanged");
            var fieldChangedFetched = fieldChangedExisting.withAssetName("changed");
            testData.existing(fieldChangedExisting);
            testData.fetched(fieldChangedFetched);

            var added = Do.forName("added");
            testData.fetched(added);

            var removed = Do.forName("removed");
            testData.existing(removed);

            var changedCoExisting = Do.forName("contractOffer");
            var changedCoFetched = changedCoExisting.withContractOffers(List.of(
                    changedCoExisting.getContractOffers().get(0).withPolicyValue("changed")
            ));
            testData.existing(changedCoExisting);
            testData.fetched(changedCoFetched);

            var addedCoExisting = Do.forName("contractOfferAdded");
            var addedCoFetched = addedCoExisting.withContractOffer(new Co("added co", "added co"));
            testData.existing(addedCoExisting);
            testData.fetched(addedCoFetched);

            var removedCoExisting = Do.forName("contractOfferRemoved").withContractOffer(new Co("removed co", "removed co"));
            var removedCoFetched = Do.forName("contractOfferRemoved");
            testData.existing(removedCoExisting);
            testData.fetched(removedCoFetched);

            // act
            dsl.transaction(it -> testData.initialize(it.dsl()));
            dsl.transaction(it -> dataOfferWriter.updateDataOffers(
                    it.dsl(),
                    testData.connectorEndpoint,
                    testData.fetchedDataOffers,
                    changes
            ));
            var actual = dsl.transactionResult(it -> new DataOfferWriterTestResultHelper(it.dsl()));

            // assert
            assertThat(actual.numDataOffers()).isEqualTo(6);
            assertThat(changes.getNumOffersAdded()).isEqualTo(1);
            assertThat(changes.getNumOffersUpdated()).isEqualTo(4);
            assertThat(changes.getNumOffersDeleted()).isEqualTo(1);

            var now = OffsetDateTime.now();
            var minuteAccuracy = new TemporalUnitLessThanOffset(1, ChronoUnit.MINUTES);
            var addedActual = actual.getDataOffer(added.getAssetId());
            assertAssetPropertiesEqual(testData, addedActual, added);
            assertThat(addedActual.getCreatedAt()).isCloseTo(now, minuteAccuracy);
            assertThat(addedActual.getUpdatedAt()).isCloseTo(now, minuteAccuracy);
            assertThat(actual.numContractOffers(added.getAssetId())).isEqualTo(1);
            assertPolicyEquals(actual, testData, added, added.getContractOffers().get(0));

            var unchangedActual = actual.getDataOffer(unchanged.getAssetId());
            assertThat(unchangedActual.getUpdatedAt()).isEqualTo(testData.old);
            assertThat(unchangedActual.getCreatedAt()).isEqualTo(testData.old);

            var fieldChangedActual = actual.getDataOffer(fieldChangedExisting.getAssetId());
            assertAssetPropertiesEqual(testData, fieldChangedActual, fieldChangedFetched);
            assertThat(fieldChangedActual.getCreatedAt()).isEqualTo(testData.old);
            assertThat(fieldChangedActual.getUpdatedAt()).isCloseTo(now, minuteAccuracy);

            var removedActual = actual.getDataOffer(removed.getAssetId());
            assertThat(removedActual).isNull();

            var changedCoActual = actual.getDataOffer(changedCoExisting.getAssetId());
            assertThat(changedCoActual.getCreatedAt()).isEqualTo(testData.old);
            assertThat(changedCoActual.getUpdatedAt()).isCloseTo(now, minuteAccuracy);
            assertThat(actual.numContractOffers(changedCoExisting.getAssetId())).isEqualTo(1);
            assertPolicyEquals(actual, testData, changedCoFetched, changedCoFetched.getContractOffers().get(0));

            var addedCoActual = actual.getDataOffer(addedCoExisting.getAssetId());
            assertThat(addedCoActual.getCreatedAt()).isEqualTo(testData.old);
            assertThat(addedCoActual.getUpdatedAt()).isCloseTo(now, minuteAccuracy);
            assertThat(actual.numContractOffers(addedCoActual.getAssetId())).isEqualTo(2);

            var removedCoActual = actual.getDataOffer(removedCoExisting.getAssetId());
            assertThat(removedCoActual.getCreatedAt()).isEqualTo(testData.old);
            assertThat(removedCoActual.getUpdatedAt()).isCloseTo(now, minuteAccuracy);
            assertThat(actual.numContractOffers(removedCoActual.getAssetId())).isEqualTo(1);
        });
    }

    private void assertAssetPropertiesEqual(DataOfferWriterTestDataHelper testData, DataOfferRecord actual, Do expected) {
        var actualAssetJson = actual.getAssetProperties().data();
        var expectedAssetJson = testData.dummyAssetJson(expected);
        assertThat(actualAssetJson).isEqualTo(expectedAssetJson);
    }

    private void assertPolicyEquals(
            DataOfferWriterTestResultHelper actual,
            DataOfferWriterTestDataHelper scenario,
            Do expectedDo,
            Co expectedCo
    ) {
        var actualContractOffer = actual.getContractOffer(expectedDo.getAssetId(), expectedCo.getId());
        var actualPolicy = actualContractOffer.getPolicy().data();
        var expectedPolicy = scenario.dummyPolicyJson(expectedCo.getPolicyValue());
        assertThat(actualPolicy).isEqualTo(expectedPolicy);
    }
}
