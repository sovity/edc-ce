/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.model

import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess
import org.jooq.DSLContext

@Service
class KpiDao {
    fun getKpis(
        dsl: DSLContext
    ): KpiRs =
        dsl.select(
            KpiRs::assetsCount from {
                KpiFields.getAssetsCount()
            },
            KpiRs::policiesCount from {
                KpiFields.getPoliciesCount()
            },
            KpiRs::contractDefinitionsCount from {
                KpiFields.getContractDefinitionsCount()
            },
            KpiRs::contractAgreementsCount from {
                KpiFields.getContractAgreementCount()
            },
            KpiRs::incomingTransferProcessRunningCount from {
                KpiFields.getTransferProcessesInRunningStateCount(TransferProcess.Type.CONSUMER)
            },
            KpiRs::incomingTransferProcessOkCount from {
                KpiFields.getTransferProcessesInOkStateCount(TransferProcess.Type.CONSUMER)
            },
            KpiRs::incomingTransferProcessErrorCount from {
                KpiFields.getTransferProcessesInErrorStateCount(TransferProcess.Type.CONSUMER)
            },
            KpiRs::outgoingTransferProcessRunningCount from {
                KpiFields.getTransferProcessesInRunningStateCount(TransferProcess.Type.PROVIDER)
            },
            KpiRs::outgoingTransferProcessOkCount from {
                KpiFields.getTransferProcessesInOkStateCount(TransferProcess.Type.PROVIDER)
            },
            KpiRs::outgoingTransferProcessErrorCount from {
                KpiFields.getTransferProcessesInErrorStateCount(TransferProcess.Type.PROVIDER)
            },
        ).fetchOneInto(KpiRs::class.java)!!
}
