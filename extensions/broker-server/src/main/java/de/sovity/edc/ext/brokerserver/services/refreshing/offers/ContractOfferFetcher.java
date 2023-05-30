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

import de.sovity.edc.ext.brokerserver.services.refreshing.exceptions.ConnectorUnreachableException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.eclipse.edc.spi.query.QuerySpec;

import java.util.List;

@RequiredArgsConstructor
public class ContractOfferFetcher {
    private final CatalogService catalogService;

    /**
     * Fetches Connector contract offers
     *
     * @param connectorEndpoint connector endpoint
     * @return updated connector db row
     */
    @SneakyThrows
    public List<ContractOffer> fetch(String connectorEndpoint) {
        try {
            return catalogService.getByProviderUrl(connectorEndpoint, QuerySpec.max()).get().getContractOffers();
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new ConnectorUnreachableException("Failed to fetch connector contract offers", e);
        }
    }
}
