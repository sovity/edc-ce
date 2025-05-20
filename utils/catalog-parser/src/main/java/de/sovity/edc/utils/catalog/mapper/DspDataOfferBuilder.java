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
 *       sovity GmbH - init
 *
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
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class DspDataOfferBuilder {

    private final JsonLd jsonLd;

    public DspCatalog buildDataOffers(String endpoint, JsonObject json) {
        final var expanded = jsonLd.expand(json).orElseThrow(DspCatalogServiceException::ofFailure);
        var participantId = JsonLdUtils.string(expanded, Prop.Edc.PARTICIPANT_ID);
        if (participantId == null) {
            // new coordinates for Tractus 0.9.0 responses
            participantId = JsonLdUtils.string(expanded, "https://w3id.org/dspace/v0.8/participantId");
        }
        var dataOffers = JsonLdUtils.listOfObjects(expanded, Prop.Dcat.DATASET);

        return new DspCatalog(
            endpoint,
            participantId,
            dataOffers.stream()
                .map(this::buildDataOffer)
                .toList()
        );
    }

    private DspDataOffer buildDataOffer(JsonObject dataset) {
        var contractOffers = JsonLdUtils.listOfObjects(dataset, Prop.Odrl.HAS_POLICY).stream()
            .map(this::buildContractOffer)
            .toList();

        var distributions = JsonLdUtils.listOfObjects(dataset, Prop.Dcat.DISTRIBUTION_WILL_BE_OVERWRITTEN_BY_CATALOG);

        var assetProperties = Json.createObjectBuilder(dataset)
            .remove(Prop.TYPE)
            .remove(Prop.Odrl.HAS_POLICY)
            .remove(Prop.Dcat.DISTRIBUTION_WILL_BE_OVERWRITTEN_BY_CATALOG)
            .build();

        return new DspDataOffer(
            assetProperties,
            contractOffers,
            distributions
        );
    }

    @NotNull
    private DspContractOffer buildContractOffer(JsonObject json) {
        return new DspContractOffer(JsonLdUtils.id(json), json);
    }
}
