/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard.model

data class DashboardRs(
    val numAssets: Int,
    val numPolicies: Int,
    val numContractDefinitions: Int,
    val numContractAgreementsConsuming: Int,
    val numContractAgreementsProviding: Int,
    val transferProcessesConsumingTotal: Int,
    val transferProcessesConsumingRunning: Int,
    val transferProcessesConsumingOk: Int,
    val transferProcessesConsumingError: Int,
    val transferProcessesProvidingTotal: Int,
    val transferProcessesProvidingRunning: Int,
    val transferProcessesProvidingOk: Int,
    val transferProcessesProvidingError: Int,
)
