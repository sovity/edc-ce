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
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.Collection;

@RequiredArgsConstructor
public class DataOfferLimitsEnforcer {
    private final Config config;
    private final BrokerEventLogger brokerEventLogger;

    public record DataOfferLimitsEnforced(
            Collection<FetchedDataOffer> abbreviatedDataOffers,
            boolean dataOfferLimitsExceeded,
            boolean contractOfferLimitsExceeded
    ) {
    }

    public DataOfferLimitsEnforced enforceLimits(Collection<FetchedDataOffer> dataOffers) {
        // Get limits from config
        var maxDataOffers = config.getInteger(BrokerServerExtension.MAX_DATA_OFFERS_PER_CONNECTOR, -1);
        var maxContractOffers = config.getInteger(BrokerServerExtension.MAX_CONTRACT_OFFERS_PER_CONNECTOR, -1);
        var offerList = dataOffers.stream().toList();

        // No limits set
        if (maxDataOffers == -1 && maxContractOffers == -1) {
            return new DataOfferLimitsEnforced(dataOffers, false, false);
        }

        // Validate if limits exceeded
        var dataOfferLimitsExceeded = false;
        if (maxDataOffers != -1 && offerList.size() > maxDataOffers) {
            offerList = offerList.subList(0, maxDataOffers);
            dataOfferLimitsExceeded = true;
        }

        var contractOfferLimitsExceeded = false;
        for (var dataOffer : offerList) {
            var contractOffers = dataOffer.getContractOffers();
            if (contractOffers != null && maxContractOffers != -1 && contractOffers.size() > maxContractOffers) {
                dataOffer.setContractOffers(contractOffers.subList(0, maxContractOffers));
                contractOfferLimitsExceeded = true;
            }
        }

        // Create new list with limited offers
        return new DataOfferLimitsEnforced(offerList, dataOfferLimitsExceeded, contractOfferLimitsExceeded);
    }

    public void logEnforcedLimitsIfChanged(ConnectorRecord connector, DataOfferLimitsEnforced enforcedLimits) {
        // DataOffer
        if (enforcedLimits.dataOfferLimitsExceeded() && connector.getDataOffersExceeded() == ConnectorDataOffersExceeded.OK) {
            brokerEventLogger.logConnectorUpdateDataOfferLimitExceeded(enforcedLimits.abbreviatedDataOffers.size(), connector.getEndpoint());
            connector.setDataOffersExceeded(ConnectorDataOffersExceeded.EXCEEDED);
        } else if (!enforcedLimits.dataOfferLimitsExceeded() && connector.getDataOffersExceeded() == ConnectorDataOffersExceeded.EXCEEDED) {
            brokerEventLogger.logConnectorUpdateDataOfferLimitOk(connector.getEndpoint());
            connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        }

        // ContractOffer
        if (enforcedLimits.contractOfferLimitsExceeded() && connector.getContractOffersExceeded() == ConnectorContractOffersExceeded.OK) {
            brokerEventLogger.logConnectorUpdateContractOfferLimitExceeded(enforcedLimits.abbreviatedDataOffers.size(), connector.getEndpoint());
            connector.setContractOffersExceeded(ConnectorContractOffersExceeded.EXCEEDED);
        } else if (!enforcedLimits.contractOfferLimitsExceeded() && connector.getContractOffersExceeded() == ConnectorContractOffersExceeded.EXCEEDED) {
            brokerEventLogger.logConnectorUpdateContractOfferLimitOk(connector.getEndpoint());
            connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        }
    }
}
