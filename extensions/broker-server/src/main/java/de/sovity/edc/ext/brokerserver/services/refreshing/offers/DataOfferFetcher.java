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

package de.sovity.edc.ext.brokerserver.services.refreshing.offers;

import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;

import java.util.Collection;

@RequiredArgsConstructor
public class DataOfferFetcher {
    private final ContractOfferFetcher contractOfferFetcher;
    private final DataOfferBuilder dataOfferBuilder;

    /**
     * Fetches {@link ContractOffer}s and de-duplicates them into {@link FetchedDataOffer}s.
     *
     * @param connectorEndpoint connector endpoint
     * @return updated connector db row
     */
    @SneakyThrows
    public Collection<FetchedDataOffer> fetch(String connectorEndpoint) {
        // Contract Offers contain assets multiple times, with different policies
        var contractOffers = contractOfferFetcher.fetch(connectorEndpoint);

        // Data Offers represent unique assets
        return dataOfferBuilder.deduplicateContractOffers(contractOffers);
    }
}
