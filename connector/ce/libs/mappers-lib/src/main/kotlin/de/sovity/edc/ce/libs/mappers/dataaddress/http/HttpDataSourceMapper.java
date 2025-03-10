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
package de.sovity.edc.ce.libs.mappers.dataaddress.http;

import de.sovity.edc.ce.api.common.model.UiDataSourceHttpData;
import de.sovity.edc.ce.api.common.model.UiDataSourceOnRequest;
import de.sovity.edc.ce.libs.mappers.PlaceholderEndpointService;
import de.sovity.edc.runtime.simple_di.Service;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;


@RequiredArgsConstructor
@Service
public class HttpDataSourceMapper {
    private final HttpHeaderMapper httpHeaderMapper;
    private final PlaceholderEndpointService placeholderEndpointService;

    /**
     * Data Address for type HTTP_DATA
     *
     * @param httpData {@link UiDataSourceHttpData}
     * @return properties for {@link org.eclipse.edc.spi.types.domain.DataAddress}
     */
    public Map<String, String> buildDataAddress(@NonNull UiDataSourceHttpData httpData) {
        var baseUrl = requireNonNull(httpData.getBaseUrl(), "baseUrl must not be null");
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

        if (StringUtils.isNotBlank(httpData.getAuthHeaderName())) {
            props.put(Prop.Edc.AUTH_KEY, httpData.getAuthHeaderName());
            if (httpData.getAuthHeaderValue() != null) {
                if (httpData.getAuthHeaderValue().getRawValue() != null) {
                    props.put(Prop.Edc.AUTH_CODE, httpData.getAuthHeaderValue().getRawValue());
                } else if (httpData.getAuthHeaderValue().getSecretName() != null) {
                    props.put(Prop.Edc.SECRET_NAME, httpData.getAuthHeaderValue().getSecretName());
                }
            }
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
        var contactEmail = requireNonNull(onRequest.getContactEmail(), "contactEmail must not be null");
        var contactEmailSubject = requireNonNull(
            onRequest.getContactPreferredEmailSubject(),
            "Need contactPreferredEmailSubject"
        );

        var placeholderEndpointForAsset = placeholderEndpointService.getPlaceholderEndpointForAsset(
            onRequest.getContactEmail(),
            onRequest.getContactPreferredEmailSubject());

        var actualDataSource = UiDataSourceHttpData.builder()
            .baseUrl(placeholderEndpointForAsset)
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
        var isOnRequest = Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY_ON_REQUEST
            .equals(dataAddress.get(Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY));
        if (!isOnRequest) {
            Map.of(
                Prop.Edc.PROXY_METHOD, Prop.SovityDcatExt.HttpDatasourceHints.METHOD,
                Prop.Edc.PROXY_PATH, Prop.SovityDcatExt.HttpDatasourceHints.PATH,
                Prop.Edc.PROXY_QUERY_PARAMS, Prop.SovityDcatExt.HttpDatasourceHints.QUERY_PARAMS,
                Prop.Edc.PROXY_BODY, Prop.SovityDcatExt.HttpDatasourceHints.BODY
            ).forEach((prop, hint) ->
                // Will add hints as "true" or "false"
                json.add(hint, String.valueOf("true".equals(dataAddress.get(prop))))
            );
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
