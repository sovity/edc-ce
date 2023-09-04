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

import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroupConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class ConnectorRemoteConfig {
    private final String participantId;
    private final EdcApiGroupConfig defaultEndpoint;
    private final EdcApiGroupConfig managementEndpoint;
    private final EdcApiGroupConfig protocolEndpoint;
}
