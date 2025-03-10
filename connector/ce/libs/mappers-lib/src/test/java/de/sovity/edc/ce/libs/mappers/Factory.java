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
package de.sovity.edc.ce.libs.mappers;

import de.sovity.edc.ce.libs.mappers.asset.AssetEditRequestMapper;
import de.sovity.edc.ce.libs.mappers.asset.AssetJsonLdBuilder;
import de.sovity.edc.ce.libs.mappers.asset.AssetJsonLdParser;
import de.sovity.edc.ce.libs.mappers.asset.OwnConnectorEndpointService;
import de.sovity.edc.ce.libs.mappers.asset.utils.AssetJsonLdUtils;
import de.sovity.edc.ce.libs.mappers.asset.utils.EdcPropertyUtils;
import de.sovity.edc.ce.libs.mappers.asset.utils.ShortDescriptionBuilder;
import de.sovity.edc.ce.libs.mappers.dataaddress.DataSourceMapper;
import de.sovity.edc.ce.libs.mappers.dataaddress.http.HttpDataSourceMapper;
import de.sovity.edc.ce.libs.mappers.dataaddress.http.HttpHeaderMapper;
import de.sovity.edc.runtime.config.ConfigUtils;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class Factory {

    @NotNull
    public static AssetJsonLdBuilder newAssetJsonLdBuilder(OwnConnectorEndpointService ownConnectorEndpointService) {
        val configUtils = Mockito.mock(ConfigUtils.class);
        when(configUtils.getProtocolApiUrl()).thenReturn("http://example.com/dummy/baseUrl");

        return new AssetJsonLdBuilder(
            new DataSourceMapper(
                new EdcPropertyUtils(),
                new HttpDataSourceMapper(
                    new HttpHeaderMapper(),
                    new PlaceholderEndpointService(configUtils))
            ),
            newAssetJsonLdParser(ownConnectorEndpointService),
            new AssetEditRequestMapper()
        );
    }

    @NotNull
    public static AssetJsonLdParser newAssetJsonLdParser(OwnConnectorEndpointService ownConnectorEndpointService) {
        return new AssetJsonLdParser(
            new AssetJsonLdUtils(),
            new ShortDescriptionBuilder(),
            ownConnectorEndpointService
        );
    }
}
