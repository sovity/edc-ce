/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.config

import de.sovity.edc.runtime.modules.getEnvironment
import de.sovity.edc.runtime.modules.model.DocumentedEnum
import org.eclipse.edc.spi.system.configuration.Config

enum class CeEnvironment(override val documentation: String) : DocumentedEnum {
    PRODUCTION("EDC Connectors running in an environment behind a reverse proxy with TLS"),
    LOCAL_DEMO_DOCKER_COMPOSE("EDC Connectors running in a Docker Compose setup without TLS"),
    UNIT_TEST("EDC Connectors started for JUnit tests")
}

fun Config.getCeEnvironment(): CeEnvironment? = CeEnvironment.entries.find {
    it.isSelectedOption(this.getEnvironment())
}
