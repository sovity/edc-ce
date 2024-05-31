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

import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.brokerserver.services.config.BrokerServerSettings;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class DataOfferLimitsEnforcer {
    private final BrokerServerSettings brokerServerSettings;
    private final BrokerEventLogger brokerEventLogger;

    public record DataOfferLimitsEnforced(
            Collection<FetchedDataOffer> abbreviatedDataOffers,
            boolean dataOfferLimitsExceeded,
            boolean contractOfferLimitsExceeded
    ) {
    }

    public DataOfferLimitsEnforced enforceLimits(Collection<FetchedDataOffer> dataOffers) {
        // Get limits from config
        var maxDataOffers = brokerServerSettings.getMaxDataOffersPerConnector();
        var maxContractOffers = brokerServerSettings.getMaxContractOffersPerDataOffer();
        List<FetchedDataOffer> offerList = new ArrayList<>(dataOffers);

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

    public void logEnforcedLimitsIfChanged(DSLContext dsl, ConnectorRecord connector, DataOfferLimitsEnforced enforcedLimits) {
        String endpoint = connector.getEndpoint();

        // DataOffer
        if (enforcedLimits.dataOfferLimitsExceeded() && connector.getDataOffersExceeded() == ConnectorDataOffersExceeded.OK) {
            var maxDataOffers = brokerServerSettings.getMaxDataOffersPerConnector();
            brokerEventLogger.logConnectorUpdateDataOfferLimitExceeded(dsl, maxDataOffers, endpoint);
            connector.setDataOffersExceeded(ConnectorDataOffersExceeded.EXCEEDED);
        } else if (!enforcedLimits.dataOfferLimitsExceeded() && connector.getDataOffersExceeded() == ConnectorDataOffersExceeded.EXCEEDED) {
            brokerEventLogger.logConnectorUpdateDataOfferLimitOk(dsl, endpoint);
            connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        }

        // ContractOffer
        if (enforcedLimits.contractOfferLimitsExceeded() && connector.getContractOffersExceeded() == ConnectorContractOffersExceeded.OK) {
            var maxContractOffers = brokerServerSettings.getMaxContractOffersPerDataOffer();
            brokerEventLogger.logConnectorUpdateContractOfferLimitExceeded(dsl, maxContractOffers, endpoint);
            connector.setContractOffersExceeded(ConnectorContractOffersExceeded.EXCEEDED);
        } else if (!enforcedLimits.contractOfferLimitsExceeded() && connector.getContractOffersExceeded() == ConnectorContractOffersExceeded.EXCEEDED) {
            brokerEventLogger.logConnectorUpdateContractOfferLimitOk(dsl, endpoint);
            connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        }
    }
}
