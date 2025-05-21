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

import de.sovity.edc.ce.libs.mappers.dsp.model.DspCatalog
import de.sovity.edc.utils.JsonUtils
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.edc.jsonld.TitaniumJsonLd
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class DspDataOfferParserTest {
    @Test
    fun `can parse a catalog in IDS v8 spec format`() {
        // arrange
        val jsonString = javaClass.getResource("catalog_IDS_v0.8.json").readText()
        val json = JsonUtils.parseJsonObj(jsonString)

        // act
        val parsed = DspDataOfferParser(TitaniumJsonLd(mock()))
            .parseDataOffers("http://localhost:16100/api/dsp", json)

        // assert
        assertThat(parsed)
            .extracting(DspCatalog::getParticipantId)
            .isEqualTo("theParticipantId")
    }

    @Test
    fun `can parse a catalog in EDC 07 spec format`() {
        // arrange
        val jsonString = javaClass.getResource("catalog_EDC_v0.7.json").readText()
        val json = JsonUtils.parseJsonObj(jsonString)

        // act
        val parsed = DspDataOfferParser(TitaniumJsonLd(mock()))
            .parseDataOffers("http://localhost:16100/api/dsp", json)

        // assert
        assertThat(parsed)
            .extracting(DspCatalog::getParticipantId)
            .isEqualTo("theParticipantId")
    }
}
