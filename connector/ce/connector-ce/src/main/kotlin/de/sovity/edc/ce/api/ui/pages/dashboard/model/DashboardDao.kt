/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard.model

import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess
import org.jooq.DSLContext

@Service
class DashboardDao {
    fun getDashboardPage(
        dsl: DSLContext
    ): DashboardRs =
        dsl.select(
            DashboardRs::numAssets from {
                DashboardFields.getNumberOfAssets()
            },
            DashboardRs::numPolicies from {
                DashboardFields.getNumberOfPolicies()
            },
            DashboardRs::numContractDefinitions from {
                DashboardFields.getNumberOfContractDefinitions()
            },
            DashboardRs::numContractAgreementsConsuming from {
                DashboardFields.agreementCount(ContractNegotiation.Type.CONSUMER)
            },
            DashboardRs::numContractAgreementsProviding from {
                DashboardFields.agreementCount(ContractNegotiation.Type.PROVIDER)
            },
            DashboardRs::transferProcessesConsumingTotal from {
                DashboardFields.countTransferProcessesWhereType(TransferProcess.Type.CONSUMER)
            },

            DashboardRs::transferProcessesConsumingRunning from {
                DashboardFields.countTransferProcessesInRunningState(TransferProcess.Type.CONSUMER)
            },
            DashboardRs::transferProcessesConsumingOk from {
                DashboardFields.countTransferProcessesInOkState(TransferProcess.Type.CONSUMER)
            },
            DashboardRs::transferProcessesConsumingError from {
                DashboardFields.countTransferProcessesInErrorState(TransferProcess.Type.CONSUMER)
            },

            DashboardRs::transferProcessesProvidingTotal from {
                DashboardFields.countTransferProcessesWhereType(TransferProcess.Type.PROVIDER)
            },
            DashboardRs::transferProcessesProvidingRunning from {
                DashboardFields.countTransferProcessesInRunningState(TransferProcess.Type.PROVIDER)
            },
            DashboardRs::transferProcessesProvidingOk from {
                DashboardFields.countTransferProcessesInOkState(TransferProcess.Type.PROVIDER)
            },

            DashboardRs::transferProcessesProvidingError from {
                DashboardFields.countTransferProcessesInErrorState(TransferProcess.Type.PROVIDER)
            },
        ).fetchOneInto(DashboardRs::class.java)!!
}
