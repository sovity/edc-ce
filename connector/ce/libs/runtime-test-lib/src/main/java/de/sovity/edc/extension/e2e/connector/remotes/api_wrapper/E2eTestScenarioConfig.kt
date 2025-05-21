/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.connector.remotes.api_wrapper

import de.sovity.edc.extension.e2e.utils.DebugUtils.isDebug
import de.sovity.edc.runtime.config.ConfigUtils
import lombok.Getter
import java.time.Duration

@Getter
class E2eTestScenarioConfig(
    val provider: ConfigUtils,
    val consumer: ConfigUtils,
    @Suppress("MagicNumber")
    val timeout: Duration = if (isDebug) Duration.ofHours(10) else Duration.ofSeconds(20)
) {

    companion object {
        fun forConfig(
            providerConfig: ConfigUtils,
            consumerConfig: ConfigUtils
        ) =
            E2eTestScenarioConfig(providerConfig, consumerConfig)
    }
}
