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

package de.sovity.edc.ext.wrapper.api.ui.pages.contracts;

import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementTransferRequest;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.TransferRequestBuilder;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.api.model.IdResponse;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;

@RequiredArgsConstructor
public class ContractAgreementTransferApiService {
    private final TransferRequestBuilder transferRequestBuilder;
    private final TransferProcessService transferProcessService;

    @NotNull
    public IdResponse initiateTransfer(
            ContractAgreementTransferRequest request
    ) {
        var transferRequest = transferRequestBuilder.buildTransferRequest(request);
        var transferProcess = transferProcessService.initiateTransfer(transferRequest)
                .orElseThrow(exceptionMapper(TransferProcess.class, transferRequest.getId()));
        return IdResponse.Builder.newInstance()
                .id(transferProcess.getId())
                .createdAt(transferProcess.getCreatedAt())
                .build();
    }
}
