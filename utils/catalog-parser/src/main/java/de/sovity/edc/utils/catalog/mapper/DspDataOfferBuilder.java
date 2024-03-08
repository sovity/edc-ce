/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.utils.catalog.mapper;

import de.sovity.edc.utils.catalog.DspCatalogServiceException;
import de.sovity.edc.utils.catalog.model.DspCatalog;
import de.sovity.edc.utils.catalog.model.DspContractOffer;
import de.sovity.edc.utils.catalog.model.DspDataOffer;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class DspDataOfferBuilder {

    private final JsonLd jsonLd;

    public DspCatalog buildDataOffers(String endpoint, JsonObject json) {
        json = jsonLd.expand(json).orElseThrow(DspCatalogServiceException::ofFailure);
        String participantId = JsonLdUtils.string(json, Prop.Edc.PARTICIPANT_ID);

        return new DspCatalog(
                endpoint,
                participantId,
                JsonLdUtils.listOfObjects(json, Prop.Dcat.DATASET).stream()
                        .map(this::buildDataOffer)
                        .toList()
        );
    }

    private DspDataOffer buildDataOffer(JsonObject dataset) {
        var contractOffers = JsonLdUtils.listOfObjects(dataset, Prop.Odrl.HAS_POLICY).stream()
                .map(this::buildContractOffer)
                .toList();

        var distributions = JsonLdUtils.listOfObjects(dataset, Prop.Dcat.DISTRIBUTION);

        var assetProperties = Json.createObjectBuilder(dataset)
                .remove(Prop.TYPE)
                .remove(Prop.Odrl.HAS_POLICY)
                .remove(Prop.Dcat.DISTRIBUTION)
                .build();


        return new DspDataOffer(
                assetProperties,
                contractOffers,
                distributions
        );
    }

    @NotNull
    private DspContractOffer buildContractOffer(JsonObject json) {
        val stableId = DspContractOfferUtils.buildStableId(json);
        val withStableId = Json.createObjectBuilder(json).remove(Prop.ID).add(Prop.ID, stableId).build();

        return new DspContractOffer(stableId, withStableId);
    }
}
