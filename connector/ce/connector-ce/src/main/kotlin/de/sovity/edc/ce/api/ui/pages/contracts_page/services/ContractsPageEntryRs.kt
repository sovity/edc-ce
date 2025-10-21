/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contracts_page.services

import java.time.OffsetDateTime

data class ContractsPageEntryRs(
    val contractAgreementId: String,
    val contractSigningDate: OffsetDateTime,
    val transferProcessCount: Int,
    val assetId: String,
    val assetName: String,
    val negotiationType: String,
    val negotiationCounterPartyId: String,
    val terminatedAt: OffsetDateTime?,
)
