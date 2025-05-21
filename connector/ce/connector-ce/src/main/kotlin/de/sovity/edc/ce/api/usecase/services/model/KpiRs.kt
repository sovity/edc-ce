/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.model

class KpiRs(
    val assetsCount: Int,
    val policiesCount: Int,
    val contractDefinitionsCount: Int,
    val contractAgreementsCount: Int,
    val incomingTransferProcessRunningCount: Int,
    val incomingTransferProcessOkCount: Int,
    val incomingTransferProcessErrorCount: Int,
    val outgoingTransferProcessRunningCount: Int,
    val outgoingTransferProcessOkCount: Int,
    val outgoingTransferProcessErrorCount: Int
)
