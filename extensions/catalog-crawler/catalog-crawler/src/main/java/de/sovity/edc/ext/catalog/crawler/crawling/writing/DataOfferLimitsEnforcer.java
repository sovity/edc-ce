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

package de.sovity.edc.ext.catalog.crawler.crawling.writing;

import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class DataOfferLimitsEnforcer {
    private final CrawlerConfig crawlerConfig;
    private final CrawlerEventLogger crawlerEventLogger;

    public record DataOfferLimitsEnforced(
            Collection<FetchedDataOffer> abbreviatedDataOffers,
            boolean dataOfferLimitsExceeded,
            boolean contractOfferLimitsExceeded
    ) {
    }

    public DataOfferLimitsEnforced enforceLimits(Collection<FetchedDataOffer> dataOffers) {
        // Get limits from config
        var maxDataOffers = crawlerConfig.getMaxDataOffersPerConnector();
        var maxContractOffers = crawlerConfig.getMaxContractOffersPerDataOffer();
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

    public void logEnforcedLimitsIfChanged(
            DSLContext dsl,
            ConnectorRef connectorRef,
            ConnectorRecord connector,
            DataOfferLimitsEnforced enforcedLimits
    ) {

        // DataOffer
        if (enforcedLimits.dataOfferLimitsExceeded() && connector.getDataOffersExceeded() == ConnectorDataOffersExceeded.OK) {
            var maxDataOffers = crawlerConfig.getMaxDataOffersPerConnector();
            crawlerEventLogger.logConnectorUpdateDataOfferLimitExceeded(dsl, connectorRef, maxDataOffers);
            connector.setDataOffersExceeded(ConnectorDataOffersExceeded.EXCEEDED);
        } else if (!enforcedLimits.dataOfferLimitsExceeded() && connector.getDataOffersExceeded() == ConnectorDataOffersExceeded.EXCEEDED) {
            crawlerEventLogger.logConnectorUpdateDataOfferLimitOk(dsl, connectorRef);
            connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        }

        // ContractOffer
        if (enforcedLimits.contractOfferLimitsExceeded() && connector.getContractOffersExceeded() == ConnectorContractOffersExceeded.OK) {
            var maxContractOffers = crawlerConfig.getMaxContractOffersPerDataOffer();
            crawlerEventLogger.logConnectorUpdateContractOfferLimitExceeded(dsl, connectorRef, maxContractOffers);
            connector.setContractOffersExceeded(ConnectorContractOffersExceeded.EXCEEDED);
        } else if (!enforcedLimits.contractOfferLimitsExceeded() &&
                connector.getContractOffersExceeded() == ConnectorContractOffersExceeded.EXCEEDED) {
            crawlerEventLogger.logConnectorUpdateContractOfferLimitOk(dsl, connectorRef);
            connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        }
    }
}
