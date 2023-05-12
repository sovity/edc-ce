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

package de.sovity.edc.ext.brokerserver.dao.stores;

import de.sovity.edc.ext.brokerserver.dao.models.ContractOfferRecord;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ContractOfferStore {
    private final Map<String, ContractOfferRecord> contractOffersById = new HashMap<>();

    public Stream<ContractOfferRecord> findAll() {
        return contractOffersById.values().stream();
    }

    public ContractOfferRecord findById(String contractOfferId) {
        return contractOffersById.get(contractOfferId);
    }

    public ContractOfferRecord save(ContractOfferRecord contractOffer) {
        Validate.notBlank(contractOffer.getId(), "Need Contract Offer ID");
        return contractOffersById.put(contractOffer.getId(), contractOffer);
    }
}
