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
package de.sovity.edc.ce.libs.mappers.dataaddress;

import de.sovity.edc.ce.api.common.model.DataSourceType;
import de.sovity.edc.ce.api.common.model.UiDataSource;
import de.sovity.edc.ce.api.common.model.UiDataSourceHttpData;
import de.sovity.edc.ce.api.common.model.UiDataSourceOnRequest;
import de.sovity.edc.ce.libs.mappers.asset.utils.EdcPropertyUtils;
import de.sovity.edc.ce.libs.mappers.dataaddress.http.HttpDataSourceMapper;
import de.sovity.edc.runtime.simple_di.Service;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;


@RequiredArgsConstructor
@Service
public class DataSourceMapper {
    private final EdcPropertyUtils edcPropertyUtils;
    private final HttpDataSourceMapper httpDataSourceMapper;

    public JsonObject buildDataSourceJsonLd(@NonNull UiDataSource dataSource) {
        var props = this.matchDataSource(
            dataSource,
            httpDataSourceMapper::buildDataAddress,
            httpDataSourceMapper::buildOnRequestDataAddress,
            dataSource::getCustomProperties
        );

        if (dataSource.getCustomProperties() != null) {
            props.putAll(dataSource.getCustomProperties());
        }

        return buildDataAddressJsonLd(props);
    }

    public JsonObject buildAssetPropsFromDataAddress(JsonObject dataAddressJsonLd) {
        // We purposefully do not match the DataSource type but the properties to support the data address type "CUSTOM"
        var dataAddress = parseDataAddressJsonLd(dataAddressJsonLd);
        var type = dataAddress.getOrDefault(Prop.Edc.TYPE, "");

        if (type.equals(Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)) {
            return httpDataSourceMapper.enhanceAssetWithDataSourceHints(dataAddress);
        }

        return JsonValue.EMPTY_JSON_OBJECT;
    }

    private <T> T matchDataSource(
        @NonNull UiDataSource dataSource,
        @NonNull Function<UiDataSourceHttpData, T> httpDataMapper,
        @NonNull Function<UiDataSourceOnRequest, T> onRequestMapper,
        @NonNull Supplier<T> customMapper
    ) {
        var type = dataSource.getType();
        if (type == null) {
            type = DataSourceType.CUSTOM;
        }

        return switch (type) {
            case HTTP_DATA -> httpDataMapper.apply(dataSource.getHttpData());
            case ON_REQUEST -> onRequestMapper.apply(dataSource.getOnRequest());
            case CUSTOM -> customMapper.get();
        };
    }

    private JsonObject buildDataAddressJsonLd(Map<String, String> properties) {
        var props = edcPropertyUtils.toMapOfObject(properties);
        return Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .addAll(Json.createObjectBuilder(props))
            .build();
    }

    private Map<String, String> parseDataAddressJsonLd(JsonObject dataAddressJsonLd) {
        return dataAddressJsonLd.entrySet().stream()
            .collect(toMap(Map.Entry::getKey, it -> {
                var value = JsonLdUtils.string(it.getValue());
                return value == null ? "" : value;
            }));
    }
}
