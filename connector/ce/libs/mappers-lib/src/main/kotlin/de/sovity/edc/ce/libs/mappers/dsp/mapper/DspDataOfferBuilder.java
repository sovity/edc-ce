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
package de.sovity.edc.ce.libs.mappers.dsp.mapper;

import de.sovity.edc.ce.libs.mappers.dsp.DspCatalogServiceException;
import de.sovity.edc.ce.libs.mappers.dsp.model.DspCatalog;
import de.sovity.edc.ce.libs.mappers.dsp.model.DspContractOffer;
import de.sovity.edc.ce.libs.mappers.dsp.model.DspDataOffer;
import de.sovity.edc.runtime.simple_di.Service;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Service
public class DspDataOfferBuilder {

    private final JsonLd jsonLd;

    public DspCatalog buildDataOffers(String endpoint, JsonObject json) {
        json = jsonLd.expand(json).orElseThrow(DspCatalogServiceException::ofFailure);
        var participantId = JsonLdUtils.string(json, Prop.Edc.PARTICIPANT_ID);
        var dataOffers = JsonLdUtils.listOfObjects(json, Prop.Dcat.DATASET);

        return DspCatalog.builder()
            .endpoint(endpoint)
            .participantId(participantId)
            .dataOffers(dataOffers.stream().map(this::buildDataOffer).toList())
            .build();
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
