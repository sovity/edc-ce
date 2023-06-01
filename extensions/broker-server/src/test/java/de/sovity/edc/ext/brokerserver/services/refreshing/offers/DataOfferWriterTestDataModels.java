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

import lombok.Value;
import lombok.With;

import java.util.ArrayList;
import java.util.List;

class DataOfferWriterTestDataModels {
    /**
     * Dummy Data Offer
     */
    @Value
    static class Do {
        @With
        String assetId;
        @With
        String assetName;
        @With
        List<Co> contractOffers;

        public Do withContractOffer(Co co) {
            var list = new ArrayList<>(contractOffers);
            list.add(co);
            return this.withContractOffers(list);
        }

        public static Do forName(String name) {
            return new Do(name, name + " Name", List.of(new Co(name + " CO", name + " Policy")));
        }
    }

    /**
     * Dummy Contract Offer
     */
    @Value
    static class Co {
        @With
        String id;
        @With
        String policyValue;
    }

    public static Co forName(String name) {
        return new Co(name + " CO", name + " Policy");
    }

}
