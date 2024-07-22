/*
 * Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.placeholderdatasource;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceOnRequest;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.SneakyThrows;
import lombok.val;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(E2eTestExtension.class)
class PlaceholderDataSourceExtensionTest {

    @SneakyThrows
    @Test
    void shouldAccessDummyEndpoint(
        @Provider EdcExtension providerExtension,
        @Provider ConnectorConfig providerConfig,
        @Provider EdcClient providerClient
    ) {
        // arrange

        val assetId = "asset-1";
        val email = "contact@example.com";
        val subject = "Data request";
        providerClient.uiApi().createAsset(
            UiAssetCreateRequest.builder()
                .dataSource(UiDataSource.builder()
                    .type(DataSourceType.ON_REQUEST)
                    .onRequest(UiDataSourceOnRequest.builder()
                        .contactEmail(email)
                        .contactPreferredEmailSubject(subject)
                        .build())
                    .build())
                .id(assetId)
                .build()
        );

        val a = providerClient.uiApi().getAssetPage().getAssets().get(0).getAssetJsonLd();
        val compacted = JsonLdUtils.tryCompact(JsonUtils.parseJsonObj(a));
        val dataAddress = compacted.getJsonObject(Prop.Edc.DATA_ADDRESS);
        val baseUrl = dataAddress.getString(Prop.Edc.BASE_URL);

        val service = providerExtension.getContext().getService(PlaceholderEndpointService.class);
        val expected = service.getPlaceholderEndpointForAsset(email, subject);

        // assert
        assertThat(baseUrl)
            .startsWith(providerConfig.getProperties().get(PlaceholderDataSourceExtension.MY_EDC_DATASOURCE_PLACEHOLDER_BASEURL))
            .isEqualTo(expected);

        val request = new Request.Builder()
            .url(baseUrl)
            .build();

        val client = new OkHttpClient();
        val content = client.newCall(request).execute().body().string();

        assertThat(content).contains("This is not real data.");
        assertThat(content).contains(email);
        assertThat(content).contains(subject);
    }
}
