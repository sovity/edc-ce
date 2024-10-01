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

package de.sovity.edc.extension.e2e.connector.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectorRemoteConfig {
    @NonNull
    private final String participantId;

    @NonNull
    private final String defaultApiUrl;

    @NonNull
    private final String managementApiUrl;

    @Nullable
    private final Supplier<Pair<String, String>> managementApiAuthHeaderFactory;

    @NonNull
    private final String protocolApiUrl;

    @NonNull
    private final String publicApiUrl;

    public static ConnectorRemoteConfig fromConnectorConfig(ConnectorConfig connectorConfig) {
        return builder()
            .participantId(connectorConfig.getParticipantId())
            .managementApiUrl(connectorConfig.getManagementApiUrl())
            .managementApiAuthHeaderFactory(connectorConfig.getManagementApiAuthHeaderFactory())
            .protocolApiUrl(connectorConfig.getProtocolApiUrl())
            .publicApiUrl(connectorConfig.getPublicApiUrl())
            .defaultApiUrl(connectorConfig.getDefaultApiUrl())
            .build();
    }
}
