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

import de.sovity.edc.ext.brokerserver.dao.queries.DataOfferContractOfferQueries;
import de.sovity.edc.ext.brokerserver.dao.queries.DataOfferQueries;
import de.sovity.edc.ext.brokerserver.services.BrokerServerSettings;
import lombok.Value;
import org.eclipse.edc.spi.system.configuration.Config;

import static org.mockito.Mockito.mock;

@Value
class DataOfferWriterTestDydi {
    Config config = mock(Config.class);
    BrokerServerSettings brokerServerSettings = new BrokerServerSettings(config);
    DataOfferQueries dataOfferQueries = new DataOfferQueries(brokerServerSettings);
    DataOfferContractOfferQueries dataOfferContractOfferQueries = new DataOfferContractOfferQueries();
    ContractOfferRecordUpdater contractOfferRecordUpdater = new ContractOfferRecordUpdater();
    DataOfferRecordUpdater dataOfferRecordUpdater = new DataOfferRecordUpdater();
    DataOfferPatchBuilder dataOfferPatchBuilder = new DataOfferPatchBuilder(
            dataOfferContractOfferQueries,
            dataOfferQueries,
            dataOfferRecordUpdater,
            contractOfferRecordUpdater
    );
    DataOfferPatchApplier dataOfferPatchApplier = new DataOfferPatchApplier();
    DataOfferWriter dataOfferWriter = new DataOfferWriter(dataOfferPatchBuilder, dataOfferPatchApplier);
}
