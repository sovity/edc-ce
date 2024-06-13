package de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress;

import com.ibm.icu.impl.Pair;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.http.HttpDataSourceMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSource;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSourceHttpData;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSourceOnRequest;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
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
        var assetProps = Json.createObjectBuilder();

        // We purposefully do not match the DataSource type but the final Data Address properties
        // to work with "custom data addresses" to the best of our ability.
        var dataAddress = getDataAddressProperties(dataAddressJsonLd);
        var type = dataAddress.getOrDefault(Prop.Edc.TYPE, "");


        if (type.equals(Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA)) {
            assetProps.addAll(httpDataSourceMapper.enhanceAssetWithDataSourceHints(dataAddress));
        }

        return assetProps.build();
    }

    private <T> T matchDataSource(
        @NonNull UiDataSource dataSource,
        @NonNull Function<UiDataSourceHttpData, T> httpDataMapper,
        @NonNull Function<UiDataSourceOnRequest, T> onRequestMapper,
        @NonNull Supplier<T> customMapper
    ) {
        var type = dataSource.getType();
        requireNonNull(type, "Data Source Type must not be null");
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

    private Map<String, String> getDataAddressProperties(JsonObject dataAddressJsonLd) {
        return dataAddressJsonLd.entrySet().stream()
            .map(entry -> {
                var value = JsonLdUtils.string(entry.getValue());
                return Pair.of(entry.getKey(), value == null ? "" : value);
            })
            .collect(toMap(it -> it.first, it -> it.second));
    }
}
