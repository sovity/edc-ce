/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.utils.config;

import de.sovity.edc.utils.config.model.ConfigProp;
import de.sovity.edc.utils.config.utils.UrlPathUtils;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@UtilityClass
public class ConfigUtils {

    public static String getProtocolApiUrl(Map<String, String> props) {
        return UrlPathUtils.urlPathJoin(
            ConfigProps.MY_EDC_PROTOCOL.getRaw(props),
            getHost(props, ConfigProps.WEB_HTTP_PROTOCOL_PORT),
            ConfigProps.WEB_HTTP_PROTOCOL_PATH.getRaw(props)
        );
    }

    public static String getManagementApiUrl(Map<String, String> props) {
        return UrlPathUtils.urlPathJoin(
            ConfigProps.MY_EDC_PROTOCOL.getRaw(props),
            getHost(props, ConfigProps.WEB_HTTP_MANAGEMENT_PORT),
            ConfigProps.WEB_HTTP_MANAGEMENT_PATH.getRaw(props)
        );
    }

    public static String getManagementApiKey(Map<String, String> props) {
        return ConfigProps.EDC_API_AUTH_KEY.getRaw(props);
    }

    public static String getDefaultApiUrl(Map<String, String> props) {
        return UrlPathUtils.urlPathJoin(
            ConfigProps.MY_EDC_PROTOCOL.getRaw(props),
            getHost(props, ConfigProps.WEB_HTTP_PORT),
            ConfigProps.WEB_HTTP_PATH.getRaw(props)
        );
    }

    public static String getPublicApiUrl(Map<String, String> props) {
        return UrlPathUtils.urlPathJoin(
            ConfigProps.MY_EDC_PROTOCOL.getRaw(props),
            getHost(props, ConfigProps.WEB_HTTP_PUBLIC_PORT),
            ConfigProps.WEB_HTTP_PUBLIC_PATH.getRaw(props)
        );
    }

    @Nullable
    private static String getHost(Map<String, String> props, ConfigProp portIfNoReverseProxy) {
        var hasReverseProxy = ConfigProps.NetworkType.isProduction(props);

        var host = ConfigProps.MY_EDC_FQDN.getRaw(props);
        if (!hasReverseProxy) {
            host = "%s:%s".formatted(host, portIfNoReverseProxy.getRaw(props));
        }
        return host;
    }
}
