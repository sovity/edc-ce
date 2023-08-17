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
 *      sovity GmbH - init
 */

package de.sovity.edc.extension.e2e.connector.config;

import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup;
import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroupConfig;
import de.sovity.edc.extension.e2e.connector.config.api.auth.ApiKeyAuthProvider;
import de.sovity.edc.extension.e2e.connector.config.api.auth.NoneAuthProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiConfigFactory.fromUri;
import static de.sovity.edc.extension.e2e.env.EnvUtil.getEnvVar;
import static de.sovity.edc.extension.e2e.env.EnvUtil.getEnvVarUri;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectorRemoteConfigFactory {
    public static final String EDC_DEFAULT_URL = "%s_EDC_DEFAULT_URL";
    public static final String EDC_MANAGEMENT_URL = "%s_EDC_MANAGEMENT_URL";
    public static final String EDC_PROTOCOL_URL = "%s_EDC_PROTOCOL_URL";
    public static final String EDC_MANAGEMENT_AUTH_HEADER = "%s_EDC_MANAGEMENT_AUTH_HEADER";
    public static final String EDC_MANAGEMENT_AUTH_VALUE = "%s_EDC_MANAGEMENT_AUTH_VALUE";
    public static final String TEST_BACKEND_DEFAULT_ENDPOINT = "TEST_BACKEND_DEFAULT_ENDPOINT";

    /**
     * Access a locally launched connector
     *
     * @param connectorConfig connector config
     * @return connector remote config
     */
    public static ConnectorRemoteConfig fromConnectorConfig(ConnectorConfig connectorConfig) {
        return new ConnectorRemoteConfig(
                connectorConfig.getParticipantId(),
                connectorConfig.getDefaultEndpoint(),
                connectorConfig.getManagementEndpoint(),
                connectorConfig.getProtocolEndpoint()
        );
    }

    /**
     * Access a connector started externally, e.g. by a Github Pipeline
     *
     * @param participantId participant id (prefix for env vars)
     * @return connector remote config
     */
    public static ConnectorRemoteConfig getFromEnv(String participantId) {
        return new ConnectorRemoteConfig(
                participantId,
                getDefaultApiGroupConfig(participantId),
                getManagementApiGroupConfig(participantId),
                getProtocolApiGroupConfig(participantId)
        );
    }

    private static EdcApiGroupConfig getDefaultApiGroupConfig(String participantId) {
        var uri = getEnvVarUri(EDC_DEFAULT_URL.formatted(participantId));
        return fromUri(EdcApiGroup.DEFAULT, uri, new NoneAuthProvider());
    }

    private static EdcApiGroupConfig getProtocolApiGroupConfig(String participantId) {
        var uri = getEnvVarUri(EDC_PROTOCOL_URL.formatted(participantId));
        return fromUri(EdcApiGroup.PROTOCOL, uri, new NoneAuthProvider());
    }

    private static EdcApiGroupConfig getManagementApiGroupConfig(String participantId) {
        var uri = getEnvVarUri(EDC_MANAGEMENT_URL.formatted(participantId));
        var auth = new ApiKeyAuthProvider(
                getEnvVar(EDC_MANAGEMENT_AUTH_HEADER.formatted(participantId)),
                getEnvVar(EDC_MANAGEMENT_AUTH_VALUE.formatted(participantId)));
        return fromUri(EdcApiGroup.MANAGEMENT, uri, auth);
    }
}
