/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements;

import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.ext.wrapper.api.ui.model.InitiateCustomTransferRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.InitiateTransferRequest;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.TransferRequestBuilder;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.transfer.spi.types.TransferRequest;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;

@RequiredArgsConstructor
public class ContractAgreementTransferApiService {
    private final TransferRequestBuilder transferRequestBuilder;
    private final TransferProcessService transferProcessService;

    @NotNull
    public IdResponseDto initiateTransfer(InitiateTransferRequest request) {
        var transferRequest = transferRequestBuilder.buildCustomTransferRequest(request);
        return initiate(transferRequest);
    }

    @NotNull
    public IdResponseDto initiateCustomTransfer(InitiateCustomTransferRequest request) {
        var transferRequest = transferRequestBuilder.buildCustomTransferRequest(request);
        return initiate(transferRequest);
    }

    @NotNull
    private IdResponseDto initiate(TransferRequest transferRequest) {
        var transferProcess = transferProcessService.initiateTransfer(transferRequest)
                .orElseThrow(exceptionMapper(TransferProcess.class, transferRequest.getId()));
        return new IdResponseDto(transferProcess.getId());
    }
}
