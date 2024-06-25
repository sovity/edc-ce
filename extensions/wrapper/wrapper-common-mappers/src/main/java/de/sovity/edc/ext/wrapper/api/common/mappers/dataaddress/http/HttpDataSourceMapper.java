package de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.http;

import de.sovity.edc.ext.wrapper.api.common.model.UiDataSourceHttpData;
import de.sovity.edc.ext.wrapper.api.common.model.UiDataSourceOnRequest;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;


@RequiredArgsConstructor
public class HttpDataSourceMapper {
    private final HttpHeaderMapper httpHeaderMapper;

    /**
     * Data Address for type HTTP_DATA
     *
     * @param httpData {@link UiDataSourceHttpData}
     * @return properties for {@link org.eclipse.edc.spi.types.domain.DataAddress}
     */
    public Map<String, String> buildDataAddress(@NonNull UiDataSourceHttpData httpData) {
        var baseUrl = requireNonNull(httpData.getBaseUrl(), "Need baseUrl");
        var props = new HashMap<>(Map.of(
            Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA,
            Prop.Edc.BASE_URL, baseUrl
        ));

        if (httpData.getMethod() != null) {
            props.put(Prop.Edc.METHOD, httpData.getMethod().name());
        }

        if (StringUtils.isNotBlank(httpData.getQueryString())) {
            props.put(Prop.Edc.QUERY_PARAMS, httpData.getQueryString());
        }

        props.putAll(httpHeaderMapper.buildHeaderProps(httpData.getHeaders()));

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

    public Map<String, String> buildOnRequestDataAddress(@NonNull UiDataSourceOnRequest onRequest) {
        var contactEmail = requireNonNull(onRequest.getContactEmail(), "Need contactEmail");
        var contactEmailSubject = requireNonNull(
            onRequest.getContactPreferredEmailSubject(),
            "Need contactPreferredEmailSubject"
        );

        var actualDataSource = UiDataSourceHttpData.builder()
            .baseUrl("http://0.0.0.0") // TODO replace with an internal URL with some info text
            .build();

        var props = buildDataAddress(actualDataSource);
        props.put(Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY, Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY_ON_REQUEST);
        props.put(Prop.SovityDcatExt.CONTACT_EMAIL, contactEmail);
        props.put(Prop.SovityDcatExt.CONTACT_PREFERRED_EMAIL_SUBJECT, contactEmailSubject);
        return props;
    }

    /**
     * Public information from Data Address
     *
     * @param dataAddress data address
     * @return json object to be merged with asset properties
     */
    public JsonObject enhanceAssetWithDataSourceHints(Map<String, String> dataAddress) {
        var json = Json.createObjectBuilder();

        // Parameterization Hints
        if ("true".equals(dataAddress.get(Prop.Edc.PROXY_METHOD))) {
            json.add(Prop.SovityDcatExt.HttpDatasourceHints.METHOD, "true");
        }
        if ("true".equals(dataAddress.get(Prop.Edc.PROXY_PATH))) {
            json.add(Prop.SovityDcatExt.HttpDatasourceHints.PATH, "true");
        }
        if ("true".equals(dataAddress.get(Prop.Edc.PROXY_QUERY_PARAMS))) {
            json.add(Prop.SovityDcatExt.HttpDatasourceHints.QUERY_PARAMS, "true");
        }
        if ("true".equals(dataAddress.get(Prop.Edc.PROXY_BODY))) {
            json.add(Prop.SovityDcatExt.HttpDatasourceHints.BODY, "true");
        }

        // On Request information
        Set.of(
            Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY,
            Prop.SovityDcatExt.CONTACT_EMAIL,
            Prop.SovityDcatExt.CONTACT_PREFERRED_EMAIL_SUBJECT
        ).forEach(prop -> {
            var value = dataAddress.get(prop);
            if (StringUtils.isNotBlank(value)) {
                json.add(prop, value);
            }
        });

        return json.build();
    }
}
