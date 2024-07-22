/*
 *  Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress;

import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.http.HttpDataSourceMapper;
import de.sovity.edc.ext.wrapper.api.common.model.DataSourceType;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSource;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSourceHttpData;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSourceOnRequest;
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
