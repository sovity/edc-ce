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

package de.sovity.edc.ext.brokerserver.services.refreshing;

import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;

import java.util.List;

public class ContractOfferFetcher {

    /**
     * Fetches Connector contract offers
     *
     * @param connectorEndpoint connector endpoint
     * @return updated connector db row
     */
    public List<ContractOffer> fetchContractOffers(String connectorEndpoint) {
        // TODO implement
        throw new IllegalArgumentException("Not yet implemented");
    }
}
