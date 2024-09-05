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

package de.sovity.edc.extension.e2e.connector.remotes.management_api;

import de.sovity.edc.utils.config.ConfigUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.edc.spi.system.configuration.Config;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ManagementApiConnectorRemoteConfig {
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

    public static ManagementApiConnectorRemoteConfig forConnector(
        Config config,
        Supplier<Pair<String, String>> managementApiAuthHeaderFactory
    ) {
        var configAfterEdcBoot = config.getEntries();
        return builder()
            .participantId(ConfigUtils.getParticipantId(configAfterEdcBoot))
            .managementApiUrl(ConfigUtils.getManagementApiUrl(configAfterEdcBoot))
            .managementApiAuthHeaderFactory(managementApiAuthHeaderFactory)
            .protocolApiUrl(ConfigUtils.getProtocolApiUrl(configAfterEdcBoot))
            .publicApiUrl(ConfigUtils.getPublicApiUrl(configAfterEdcBoot))
            .defaultApiUrl(ConfigUtils.getDefaultApiUrl(configAfterEdcBoot))
            .build();
    }
}
