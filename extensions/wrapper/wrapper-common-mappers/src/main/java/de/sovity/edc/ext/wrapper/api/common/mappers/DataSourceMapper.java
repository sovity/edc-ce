package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSource;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSourceHttpData;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSourceOnRequest;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


@RequiredArgsConstructor
public class DataSourceMapper {
    private final EdcPropertyUtils edcPropertyUtils;

    public JsonObjectBuilder buildDataSourceJsonLd(@NonNull UiDataSource dataSource) {
        var props = this.<Map<String, String>>matchDataSource(
            dataSource,
            this::httpDataDataAddress,
            this::onRequestDataAddress,
            HashMap::new
        );

        if (dataSource.getCustomProperties() != null) {
            props.putAll(dataSource.getCustomProperties());
        }

        return buildDataAddressJsonLd(props);
    }

    public JsonObjectBuilder buildAssetProps(@NonNull UiDataSource dataSource) {
        return this.matchDataSource(
            dataSource,
            this::httpDataAssetProps,
            this::onRequestAssetProps,
            Json::createObjectBuilder
        );
    }

    /**
     * Data Address for type HTTP_DATA
     *
     * @param httpData {@link UiDataSourceHttpData}
     * @return properties for {@link org.eclipse.edc.spi.types.domain.DataAddress}
     */
    private Map<String, String> httpDataDataAddress(@NonNull UiDataSourceHttpData httpData) {
        var props = new HashMap<>(Map.of(
            Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS,
            Prop.Edc.BASE_URL, httpData.getBaseUrl()
        ));

        if (httpData.getMethod() != null) {
            props.put(Prop.Edc.METHOD, httpData.getMethod().name());
        }

        if (StringUtils.isNotBlank(httpData.getQueryString())) {
            props.put(Prop.Edc.QUERY_PARAMS, httpData.getQueryString());
        }

        props.putAll(buildHeaderDataAddressProps(httpData.getHeaders()));

        // Parameterization
        if (Boolean.TRUE.equals(httpData.getEnableMethodParameterization())) {
            props.put(Prop.Edc.PROXY_METHOD, "true");
        }
        if (Boolean.TRUE.equals(httpData.getEnablePathParameterization())) {
            props.put(Prop.Edc.PROXY_PATH, "true");
        }
        if (Boolean.TRUE.equals(httpData.getEnableQueryParameterization())) {
            props.put(Prop.Edc.PROXY_QUERY_PARAMS, "true");
        }
        if (Boolean.TRUE.equals(httpData.getEnableBodyParameterization())) {
            props.put(Prop.Edc.PROXY_BODY, "true");
        }

        return props;
    }

    private Map<String, String> buildHeaderDataAddressProps(@Nullable Map<String, String> headers) {
        if (headers == null) {
            return Collections.emptyMap();
        }

        return MapUtils.transformedMap(
            headers,
            key -> {
                if ("content-type".equalsIgnoreCase(key)) {
                    // Content-Type is overridden by a special Data Address property
                    // So we should set that instead of attempting to set a header
                    return Prop.Edc.CONTENT_TYPE;
                } else {
                    return "header:%s".formatted(key);
                }
            },
            value -> value
        );
    }

    private JsonObjectBuilder httpDataAssetProps(@NonNull UiDataSourceHttpData httpData) {
        var props = new HashMap<String, String>();

        // Parameterization Hints
        if (Boolean.TRUE.equals(httpData.getEnableMethodParameterization())) {
            props.put(Prop.SovityDcatExt.HttpDatasourceHints.METHOD, "true");
        }
        if (Boolean.TRUE.equals(httpData.getEnablePathParameterization())) {
            props.put(Prop.SovityDcatExt.HttpDatasourceHints.PATH, "true");
        }
        if (Boolean.TRUE.equals(httpData.getEnableQueryParameterization())) {
            props.put(Prop.SovityDcatExt.HttpDatasourceHints.QUERY_PARAMS, "true");
        }
        if (Boolean.TRUE.equals(httpData.getEnableBodyParameterization())) {
            props.put(Prop.SovityDcatExt.HttpDatasourceHints.BODY, "true");
        }

        return Json.createObjectBuilder(edcPropertyUtils.toMapOfObject(props));
    }

    private Map<String, String> onRequestDataAddress(@NonNull UiDataSourceOnRequest onRequest) {
        var dummyRequest = new UiDataSourceHttpData();
        // TODO replace with an EDC URL that echoes an explanation why this data offer did not contain any data.
        dummyRequest.setBaseUrl("https://example.com");
        return httpDataDataAddress(dummyRequest);
    }

    private JsonObjectBuilder onRequestAssetProps(@NonNull UiDataSourceOnRequest onRequest) {
        var props = Map.of(
            // Data Source Type
            Prop.SovityDcatExt.DATA_SOURCE_TYPE, Prop.SovityDcatExt.DATA_SOURCE_TYPE_ON_REQUEST,

            // Contact Information
            Prop.SovityDcatExt.CONTACT_EMAIL, onRequest.getContactEmail(),
            Prop.SovityDcatExt.CONTACT_PREFERRED_EMAIL_SUBJECT, onRequest.getContactPreferredEmailSubject()
        );

        return Json.createObjectBuilder(edcPropertyUtils.toMapOfObject(props));
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

    private JsonObjectBuilder buildDataAddressJsonLd(Map<String, String> properties) {
        var props = edcPropertyUtils.toMapOfObject(properties);
        return Json.createObjectBuilder()
            .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
            .add(Prop.Edc.PROPERTIES, Json.createObjectBuilder(props));
    }
}
