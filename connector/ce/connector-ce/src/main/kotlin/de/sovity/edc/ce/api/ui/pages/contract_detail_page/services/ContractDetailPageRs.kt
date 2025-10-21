/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_detail_page.services

import de.sovity.edc.ce.db.jooq.enums.ContractTerminatedBy
import org.jooq.JSON
import java.time.OffsetDateTime

data class ContractDetailPageRs(
    val contractAgreementId: String,
    val contractSigningDate: OffsetDateTime,
    val transferProcesses: List<ContractDetailPageTransferProcessRs>,
    val policy: JSON,
    val negotiationId: String,
    val negotiationType: String,
    val negotiationCounterPartyId: String,
    val negotiationCounterPartyAddress: String,

    val terminatedAt: OffsetDateTime?,
    val terminatedBy: ContractTerminatedBy?,
    val terminationReason: String?,
    val terminationDetail: String?,

    val assetId: String,
    val assetCreatedAt: Long?,
    val assetProperties: JSON?,
    val assetPrivateProperties: JSON?,
    val assetDataAddress: JSON?
)
