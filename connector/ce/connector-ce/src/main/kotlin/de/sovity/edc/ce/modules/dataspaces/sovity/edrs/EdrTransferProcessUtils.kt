/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.edrs

import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess
import org.eclipse.edc.connector.controlplane.transfer.spi.types.command.TerminateTransferCommand
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.web.spi.exception.ServiceResultHandler

@Service
class EdrTransferProcessUtils(
    private val transferProcessService: TransferProcessService,
    private val monitor: Monitor
) {
    fun terminateTransfer(transferId: String, reason: String) {
        transferProcessService.terminate(TerminateTransferCommand(transferId, reason))
            .onSuccess { monitor.debug("Termination requested for TransferProcess with ID $transferId") }
            .orElseThrow(ServiceResultHandler.exceptionMapper(TransferProcess::class.java, transferId))
    }
}
