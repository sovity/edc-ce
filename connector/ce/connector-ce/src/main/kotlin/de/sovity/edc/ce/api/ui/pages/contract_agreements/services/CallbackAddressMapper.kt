/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements.services

import de.sovity.edc.ce.api.common.model.CallbackAddressDto
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.spi.types.domain.callback.CallbackAddress

@Service
class CallbackAddressMapper {

    fun buildCallbackAddresses(
        dtos: List<CallbackAddressDto?>?
    ): List<CallbackAddress> = dtos?.mapNotNull { dto ->
        dto?.let { buildCallbackAddress(it) }
    } ?: emptyList()

    private fun buildCallbackAddress(dto: CallbackAddressDto): CallbackAddress =
        CallbackAddress.Builder.newInstance()
            .uri(dto.url)
            .authKey(dto.authHeaderName)
            .authCodeId(dto.authHeaderVaultSecretName)
            .events(dto.events.map { it.eclipseEdcEventName }.toSet())
            .build()
}
