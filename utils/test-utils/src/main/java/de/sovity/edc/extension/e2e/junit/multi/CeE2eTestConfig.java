/*
 * Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.e2e.junit.multi;

import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig.ConnectorConfigBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Consumer;

@Builder(toBuilder = true)
@Getter
public class CeE2eTestConfig {
    @Builder.Default

    private String moduleName = ":launchers:connectors:sovity-dev";

    @Builder.Default
    private Consumer<ConnectorConfigBuilder> configCustomizer = it -> {
    };

    @Builder.Default
    private Consumer<ConnectorConfigBuilder> consumerConfigCustomizer = it -> {
    };

    @Builder.Default
    private Consumer<ConnectorConfigBuilder> providerConfigCustomizer = it -> {
    };
}
