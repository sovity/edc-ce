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

package de.sovity.edc.utils.catalog;

import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.catalog.mapper.DspDataOfferBuilder;
import de.sovity.edc.utils.catalog.model.DspCatalog;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.controlplane.services.spi.catalog.CatalogService;
import org.eclipse.edc.spi.query.QuerySpec;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class DspCatalogService {
    private final CatalogService catalogService;
    private final DspDataOfferBuilder dspDataOfferBuilder;

    public DspCatalog fetchDataOffers(String participantId, String endpoint) throws DspCatalogServiceException {
        var catalogJson = fetchDcatResponse(participantId, endpoint, QuerySpec.max());
        return dspDataOfferBuilder.buildDataOffers(endpoint, catalogJson);
    }

    public DspCatalog fetchDataOffersWithFilters(String participantId, String endpoint, QuerySpec querySpec) {
        var catalogJson = fetchDcatResponse(participantId, endpoint, querySpec);
        return dspDataOfferBuilder.buildDataOffers(endpoint, catalogJson);
    }

    private JsonObject fetchDcatResponse(String participantId, String connectorEndpoint, QuerySpec querySpec) {
        var raw = fetchDcatRaw(participantId, connectorEndpoint, querySpec);
        var string = new String(raw, StandardCharsets.UTF_8);
        return JsonUtils.parseJsonObj(string);
    }

    @SneakyThrows
    private byte[] fetchDcatRaw(String participantId, String connectorEndpoint, QuerySpec querySpec) {
        return catalogService
            .requestCatalog(participantId, connectorEndpoint, "dataspace-protocol-http", querySpec)
            .get()
            .orElseThrow(DspCatalogServiceException::ofFailure);
    }
}
