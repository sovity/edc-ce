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
import org.eclipse.edc.spi.system.configuration.Config;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@UtilityClass
public class ConfigUtils {
    public static String getParticipantId(Map<String, String> props) {
        return CeConfigProps.EDC_PARTICIPANT_ID.getRaw(props);
    }

    public static String getParticipantId(Config config) {
        return getParticipantId(config.getEntries());
    }

    public static String getProtocolApiUrl(Map<String, String> props) {
        return UrlPathUtils.urlPathJoin(
            CeConfigProps.MY_EDC_PROTOCOL.getRaw(props),
            getHost(props, CeConfigProps.WEB_HTTP_PROTOCOL_PORT),
            CeConfigProps.WEB_HTTP_PROTOCOL_PATH.getRaw(props)
        );
    }

    public static String getProtocolApiUrl(Config config) {
        return getProtocolApiUrl(config.getEntries());
    }

    public static String getManagementApiUrl(Map<String, String> props) {
        return UrlPathUtils.urlPathJoin(
            CeConfigProps.MY_EDC_PROTOCOL.getRaw(props),
            getHost(props, CeConfigProps.WEB_HTTP_MANAGEMENT_PORT),
            CeConfigProps.WEB_HTTP_MANAGEMENT_PATH.getRaw(props)
        );
    }

    public static String getManagementApiUrl(Config config) {
        return getManagementApiUrl(config.getEntries());
    }

    public static String getManagementApiKey(Map<String, String> props) {
        return CeConfigProps.EDC_API_AUTH_KEY.getRaw(props);
    }

    public static String getManagementApiKey(Config config) {
        return getManagementApiKey(config.getEntries());
    }

    public static String getDefaultApiUrl(Map<String, String> props) {
        return UrlPathUtils.urlPathJoin(
            CeConfigProps.MY_EDC_PROTOCOL.getRaw(props),
            getHost(props, CeConfigProps.WEB_HTTP_PORT),
            CeConfigProps.WEB_HTTP_PATH.getRaw(props)
        );
    }

    public static String getDefaultApiUrl(Config config) {
        return getDefaultApiUrl(config.getEntries());
    }

    public static String getPublicApiUrl(Map<String, String> props) {
        return UrlPathUtils.urlPathJoin(
            CeConfigProps.MY_EDC_PROTOCOL.getRaw(props),
            getHost(props, CeConfigProps.WEB_HTTP_PUBLIC_PORT),
            CeConfigProps.WEB_HTTP_PUBLIC_PATH.getRaw(props)
        );
    }

    public static String getPublicApiUrl(Config config) {
        return getPublicApiUrl(config.getEntries());
    }

    public static String getControlApiUrl(Map<String, String> props) {
        return UrlPathUtils.urlPathJoin(
            CeConfigProps.MY_EDC_PROTOCOL.getRaw(props),
            getHost(props, CeConfigProps.WEB_HTTP_CONTROL_PORT),
            CeConfigProps.WEB_HTTP_CONTROL_PATH.getRaw(props)
        );
    }

    public static String getControlApiUrl(Config config) {
        return getControlApiUrl(config.getEntries());
    }

    public static String getIntegratedDataPlaneUrl(Config config) {
        return UrlPathUtils.urlPathJoin(
            getControlApiUrl(config),
            "v1/dataflows"
        );
    }

    @Nullable
    public static String getHost(Map<String, String> props, ConfigProp portIfNoReverseProxy) {
        var hasReverseProxy = CeConfigProps.CeEnvironment.isProduction(props);

        var host = CeConfigProps.MY_EDC_FQDN.getRaw(props);
        if (!hasReverseProxy) {
            host = "%s:%s".formatted(host, portIfNoReverseProxy.getRaw(props));
        }
        return host;
    }
}
