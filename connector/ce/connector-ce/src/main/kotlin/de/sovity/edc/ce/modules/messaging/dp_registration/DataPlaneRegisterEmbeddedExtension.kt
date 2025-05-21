/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.dp_registration

import de.sovity.edc.runtime.config.ConfigUtils
import lombok.SneakyThrows
import org.eclipse.edc.connector.dataplane.selector.spi.DataPlaneSelectorService
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.spi.EdcException
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import org.eclipse.edc.spi.system.configuration.Config

class DataPlaneRegisterEmbeddedExtension : ServiceExtension {

    @Inject
    private lateinit var dataPlaneSelectorService: DataPlaneSelectorService

    @Inject
    private lateinit var configUtils: ConfigUtils

    private lateinit var config: Config

    private lateinit var monitor: Monitor

    override fun initialize(context: ServiceExtensionContext) {
        config = context.config
        monitor = context.monitor
    }

    @SneakyThrows
    override fun start() {
        val instanceDto = DataPlaneInstanceDto.describeSelf(config, configUtils)
        if (hasCorrectlyConfiguredInstance(instanceDto)) {
            return
        }

        dataPlaneSelectorService.addInstance(instanceDto.toInstance()).orElseThrow {
            EdcException("Data Plane '${instanceDto.id}' registration failed: ${it.failureDetail}")
        }

        monitor.info("Data Plane Instance created: '${instanceDto}")
    }

    private fun hasCorrectlyConfiguredInstance(expected: DataPlaneInstanceDto): Boolean {
        val actual = dataPlaneSelectorService.findById(expected.id).orElse { null }
            ?.let { DataPlaneInstanceDto.fromInstance(it) }
        if (actual == null) {
            return false
        }

        if (actual == expected) {
            monitor.info("Data Plane Instance '${expected.id}' is already correctly registered.")
            return true
        }

        @Suppress("MaxLineLength")
        monitor.warning(
            "Data Plane instance '${expected.id}' is out of date. Since the DataPlaneInstance lacks an update method, the Data Plane Instance will be deleted for a moment. This can cause running transfers to fail.\nExpected: $expected\nActual: $actual"
        )
        dataPlaneSelectorService.unregister(expected.id).orElseThrow {
            EdcException("Data Plane '${expected.id}' de-registration failed: ${it.failureDetail}")
        }
        return false
    }
}
