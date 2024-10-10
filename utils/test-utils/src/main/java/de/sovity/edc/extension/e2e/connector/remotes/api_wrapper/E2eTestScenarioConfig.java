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

package de.sovity.edc.extension.e2e.connector.remotes.api_wrapper;

import de.sovity.edc.utils.config.ConfigUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.edc.spi.system.configuration.Config;

import java.time.Duration;

import static java.time.Duration.ofSeconds;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class E2eTestScenarioConfig {
    @NonNull
    private final String providerParticipantId;

    @NonNull
    private final String providerProtocolApiUrl;

    @Builder.Default
    private final Duration timeout = ofSeconds(10);

    public static E2eTestScenarioConfig forProviderConfig(Config providerConfig) {
        return builder()
            .providerParticipantId(ConfigUtils.getParticipantId(providerConfig))
            .providerProtocolApiUrl(ConfigUtils.getManagementApiUrl(providerConfig))
            .build();
    }
}
