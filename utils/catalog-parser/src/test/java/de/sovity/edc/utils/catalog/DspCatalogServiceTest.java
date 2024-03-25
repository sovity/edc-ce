/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.utils.catalog;

import de.sovity.edc.utils.catalog.mapper.DspDataOfferBuilder;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.response.StatusResult;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static de.sovity.edc.utils.JsonUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DspCatalogServiceTest {
    String endpoint = "http://localhost:11003/api/v1/dsp";

    private DspCatalogService newDspCatalogService(String resultJsonFilename) {
        var catalogJson = readFile(resultJsonFilename);
        var catalogService = mock(CatalogService.class);

        var result = CompletableFuture.completedFuture(StatusResult.success(catalogJson.getBytes(StandardCharsets.UTF_8)));
        when(catalogService.requestCatalog(eq(endpoint), eq("dataspace-protocol-http"), eq(QuerySpec.max()))).thenReturn(result);
        var monitor = mock(Monitor.class);
        var dataOfferBuilder = new DspDataOfferBuilder(new TitaniumJsonLd(monitor));

        return new DspCatalogService(catalogService, dataOfferBuilder);
    }

    @Test
    void testCatalogMapping() {
        // arrange
        var dspCatalogService = newDspCatalogService("catalogResponse.json");

        // act
        var actual = dspCatalogService.fetchDataOffers(endpoint);

        // assert
        var offers = actual.getDataOffers();
        assertThat(offers).hasSize(1);
        var offer = offers.get(0);
        assertThat(actual.getEndpoint()).isEqualTo(endpoint);
        assertThat(actual.getParticipantId()).isEqualTo("provider");
        assertThat(JsonLdUtils.id(offer.getAssetPropertiesJsonLd()))
                .isEqualTo("test-1.0");
        assertThat(offer.getAssetPropertiesJsonLd().get(Prop.TYPE)).isNull();
        assertThat(JsonLdUtils.string(offer.getAssetPropertiesJsonLd(), Prop.Dcat.VERSION)).isEqualTo("1.0");

        assertThat(offer.getContractOffers()).hasSize(1);
        var co = offer.getContractOffers().get(0);
        assertThat(co.getContractOfferId()).isEqualTo("policy-1");
        assertThat(toJson(co.getPolicyJsonLd())).contains("ALWAYS_TRUE");

        assertThat(offer.getDistributions()).hasSize(1);
        assertThat(JsonLdUtils.id(offer.getDistributions().get(0))).isEqualTo("dummy-distribution");
    }


    @SneakyThrows
    private String readFile(String fileName) {
        var is = getClass().getResourceAsStream(fileName);
        Objects.requireNonNull(is, "File not found: " + fileName);
        return IOUtils.toString(is, StandardCharsets.UTF_8);
    }
}
