/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.asset

import de.sovity.edc.ce.api.utils.jooq.EdcJsonUtils
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.spi.types.domain.DataAddress
import org.jooq.JSON

@Service
class DataAddressBuilder(private val jsonUtils: EdcJsonUtils) {
    fun buildDataAddress(dataAddress: JSON): DataAddress = buildDataAddress(jsonUtils.parseMap(dataAddress))

    fun buildDataAddress(dataAddress: Map<String, Any?>): DataAddress =
        DataAddress.Builder.newInstance()
            .properties(dataAddress)
            .build()
}
