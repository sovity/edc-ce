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
import jakarta.json.JsonString;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@RequiredArgsConstructor
public class DspDataOfferBuilder {

    private final JsonLd jsonLd;
    private final Monitor monitor;

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
    DspContractOffer buildContractOffer(JsonObject json) {
        /*
         * /!\ Workaround
         * TODO: can't reference a private repo in a public repo
         * https://github.com/sovity/edc-broker-server-extension/issues/278
         * https://github.com/sovity/edc-broker-server-extension/issues/409
         *
         * The Eclipse EDC uses a new random ID for each contract offer that it returns.
         * This can't be used as an id.
         * As a workaround, we must introduce our own ID.
         * For a first iteration, we will assume that the content of the policy remains the same (same content, same order)
         * and hash it to use it as a key.
         */

        String idFieldName = "@id";
        val id = json.get(idFieldName);
        val idAsString = (JsonString) id;
        val parts = idAsString.getString().split(":");
        if (parts.length != 3) {
            throw new RuntimeException("Can't use " + idAsString + ": wrong format, must be made of 3 parts.");
        }

        val sw = new StringWriter();
        try (val writer = Json.createWriter(sw)) {
            // FIXME: This doesn't enforce any property order and may cause trouble if the returned policy schema is not consistent
            //  Use canonical form if needed later.
            val noId = Json.createObjectBuilder(json).remove(idFieldName).build();
            writer.write(noId);
            val policyJsonString = sw.toString();
            try {
                val hash = MessageDigest.getInstance("sha-1").digest(policyJsonString.getBytes());
                val b64 = Base64.getEncoder().encode(hash);
                val contractId = parts[0];
                val assetId = parts[1];
                val policyId = new String(b64);
                val stableId = contractId + ":" + assetId + ":" + policyId;

                val copy = Json.createObjectBuilder(json).remove(idFieldName).add(idFieldName, stableId).build();

                return new DspContractOffer(stableId, copy);
            } catch (NoSuchAlgorithmException e) {
                monitor.severe("Failed to hash with sha-1", e);
                throw new RuntimeException(e);
            }
        }
    }
}
