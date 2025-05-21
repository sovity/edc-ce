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
import de.sovity.edc.utils.jsonld.vocab.Prop
import jakarta.json.Json
import jakarta.json.JsonObject
import org.eclipse.edc.jsonld.spi.JsonLd

@Service
class DspDataOfferBuilder(
    private val jsonLd: JsonLd
) {

    fun buildDataOffers(endpoint: String?, json: JsonObject): DspCatalog {
        val json = jsonLd.expand(json).orElseThrow { DspCatalogServiceException.ofFailure(it) }

        val participantId = JsonLdUtils.string(json, Prop.Edc.PARTICIPANT_ID)
        val dataOffers = JsonLdUtils.listOfObjects(json, Prop.Dcat.DATASET)
        val dataOffersMerged = withMergedContractOffersByAssetId(dataOffers)

        return DspCatalog.builder()
            .endpoint(endpoint)
            .participantId(participantId)
            .dataOffers(dataOffersMerged.map { buildDataOffer(it) })
            .build()
    }

    private fun buildDataOffer(dataset: JsonObject): DspDataOffer {
        val contractOffers = JsonLdUtils.listOfObjects(dataset, Prop.Odrl.HAS_POLICY)
            .map { buildContractOffer(it) }

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

    private fun buildContractOffer(json: JsonObject): DspContractOffer =
        DspContractOffer(JsonLdUtils.id(json), json)

    private fun withMergedContractOffersByAssetId(datasets: List<JsonObject>): List<JsonObject> {
        return datasets
            .groupBy { dataset -> JsonLdUtils.string(dataset, Prop.Edc.ID) }
            .map { (_, dataSets) ->
                val allContractOffers = dataSets.flatMap { JsonLdUtils.listOfObjects(it, Prop.Odrl.HAS_POLICY) }

                Json.createObjectBuilder(dataSets.first())
                    .add(Prop.Odrl.HAS_POLICY, Json.createArrayBuilder(allContractOffers))
                    .build()
            }
    }
}
