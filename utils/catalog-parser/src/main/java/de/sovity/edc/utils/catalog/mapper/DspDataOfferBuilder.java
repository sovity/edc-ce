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

import de.sovity.edc.utils.JsonUtils;
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
import org.eclipse.edc.connector.contract.spi.ContractId;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
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
         *
         * The Eclipse EDC uses a new random UUID for each policy that it returns and in turn a new contract ID.
         * This Eclipse ID can't be used as such.
         * As a workaround, we must introduce our own ID.
         * For a first iteration, we will assume that the content of the policy remains the same (same content, same order)
         * and hash it to use it as a key.
         */

        // FIXME: This doesn't enforce any property order and may cause trouble if the returned policy schema is not consistent
        //  Use canonical form if needed later.
        val noId = Json.createObjectBuilder(json).remove(Prop.ID).build();
        val policyJsonString = JsonUtils.toJson(noId);
        val sha1 = sha1(policyJsonString);
        // encoding with base16 to make the hash readable to humans when decoding with ContractId
        val humanReadableSha1 = toBase16String(sha1);
        // re-encoding as base64 because this is the format that ContractId expects
        val policyId = toBase64String(humanReadableSha1);

        val idAsString = JsonLdUtils.string(json, Prop.ID);

        val stableId = ContractId
                .parseId(idAsString)
                .map(it -> it.definitionPart() + ":" + it.assetIdPart() + ":" + policyId)
                .orElseThrow((failure) -> {
                    String message = "Failed to parse the contract id: " + failure.getFailureDetail();
                    monitor.severe(message);
                    throw new RuntimeException(message);
                });

        val copy = Json.createObjectBuilder(json).remove(Prop.ID).add(Prop.ID, stableId).build();

        return new DspContractOffer(stableId, copy);
    }

    private static String toBase64String(String string) {
        byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = Base64.getEncoder().encode(stringBytes);
        return new String(bytes);
    }

    @NotNull
    private static String toBase16String(byte[] bytes) {
        val sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Character.forDigit(b >> 4 & 0xf, 16));
            sb.append(Character.forDigit(b & 0xf, 16));
        }
        return sb.toString();
    }

    private byte[] sha1(String policyJsonString) {
        try {
            return MessageDigest.getInstance("sha-1").digest(policyJsonString.getBytes());
        } catch (NoSuchAlgorithmException e) {
            monitor.severe("Failed to hash with sha-1", e);
            throw new RuntimeException(e);
        }
    }
}
