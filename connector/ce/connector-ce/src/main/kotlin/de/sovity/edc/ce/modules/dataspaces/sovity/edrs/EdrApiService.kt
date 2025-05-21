/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.edrs

import de.sovity.edc.ce.api.common.model.EdrDto
import de.sovity.edc.ce.api.ui.model.IdResponseDto

/**
 * Subset / Union of EDR v3 API (Eclipse EDC) and the EDR v2 API (Tractus-X)
 */
interface EdrApiService {
    /**
     * Get or refresh the EDR for a transfer process.
     *
     * @param transferId the ID of the transfer process
     * @return the EDR for the transfer process
     */
    fun getTransferProcessEdr(transferId: String): EdrDto

    /**
     * Delete the EDR and terminate the transfer process.
     *
     * @param transferId the ID of the transfer process
     * @return the ID of the deleted EDR
     */
    fun terminateTransferProcess(transferId: String): IdResponseDto
}
