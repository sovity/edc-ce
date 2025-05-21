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
package de.sovity.edc.ce.libs.mappers.dsp.mapper

import de.sovity.edc.ce.libs.mappers.dsp.DspCatalogServiceException
import de.sovity.edc.ce.libs.mappers.dsp.model.DspCatalog
import de.sovity.edc.ce.libs.mappers.dsp.model.DspContractOffer
import de.sovity.edc.ce.libs.mappers.dsp.model.DspDataOffer
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.JsonLdUtils
import de.sovity.edc.utils.jsonld.JsonLdUtilsExt.ldString
import de.sovity.edc.utils.jsonld.vocab.Prop
import jakarta.json.Json
import jakarta.json.JsonObject
import lombok.RequiredArgsConstructor
import org.eclipse.edc.jsonld.spi.JsonLd

@RequiredArgsConstructor
@Service
class DspDataOfferParser(
    private val jsonLd: JsonLd
) {
    fun parseDataOffers(endpoint: String, json: JsonObject): DspCatalog {
        val expandedJson = jsonLd.expand(json).orElseThrow(DspCatalogServiceException::ofFailure)
        val participantId = getParticipantId(expandedJson)
        val dataOffers = JsonLdUtils.listOfObjects(expandedJson, Prop.Dcat.DATASET)

        return DspCatalog.builder()
            .endpoint(endpoint)
            .participantId(participantId)
            .dataOffers(dataOffers.stream().map { dataset: JsonObject -> this.parseDataOffer(dataset) }.toList())
            .build()
    }

    private fun getParticipantId(json: JsonObject): String =
        json.ldString(Prop.Edc.PARTICIPANT_ID) ?: json.ldString(Prop.Edc.Version07.PARTICIPANT_ID)

    private fun parseDataOffer(dataset: JsonObject): DspDataOffer {
        val contractOffers = JsonLdUtils.listOfObjects(dataset, Prop.Odrl.HAS_POLICY)
            .map { parseContractOffer(it) }
            .toList()

        val distributions = JsonLdUtils.listOfObjects(dataset, Prop.Dcat.DISTRIBUTION_WILL_BE_OVERWRITTEN_BY_CATALOG)

        val assetProperties = Json.createObjectBuilder(dataset)
            .remove(Prop.TYPE)
            .remove(Prop.Odrl.HAS_POLICY)
            .remove(Prop.Dcat.DISTRIBUTION_WILL_BE_OVERWRITTEN_BY_CATALOG)
            .build()

        return DspDataOffer(
            assetProperties,
            contractOffers,
            distributions
        )
    }

    private fun parseContractOffer(json: JsonObject): DspContractOffer =
        DspContractOffer(JsonLdUtils.id(json), json)
}
