/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers.dsp;

import de.sovity.edc.ce.libs.mappers.dsp.mapper.DspDataOfferBuilder;
import de.sovity.edc.ce.libs.mappers.dsp.model.DspCatalog;
import de.sovity.edc.runtime.simple_di.Service;
import de.sovity.edc.utils.JsonUtils;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.controlplane.services.spi.catalog.CatalogService;
import org.eclipse.edc.spi.query.QuerySpec;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
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
