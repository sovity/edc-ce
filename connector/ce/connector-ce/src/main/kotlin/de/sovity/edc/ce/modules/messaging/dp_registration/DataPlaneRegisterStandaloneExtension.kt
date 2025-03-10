/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.dp_registration

import de.sovity.edc.ce.modules.messaging.dp_registration.retrying.SimpleRetry
import de.sovity.edc.runtime.config.ConfigUtils
import org.eclipse.edc.http.spi.EdcHttpClient
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import java.time.Duration

class DataPlaneRegisterStandaloneExtension : ServiceExtension {
    @Inject
    private lateinit var httpClient: EdcHttpClient

    @Inject
    private lateinit var configUtils: ConfigUtils

    override fun name() = "Data-Plane Registration (Standalone)"

    override fun initialize(context: ServiceExtensionContext) {
        val config = context.config
        val monitor = context.monitor

        val cpManagementApi = ExternalCpManagementApi(config, monitor, httpClient)

        val instanceDto = DataPlaneInstanceDto.describeSelf(config, configUtils)

        SimpleRetry.retry(30, Duration.ofSeconds(1)) {
            cpManagementApi.callPost("v2/dataplanes", instanceDto.toCreateRequestJsonLd())
        }

        monitor.info("Successfully registered Data Plane.")
    }
}
