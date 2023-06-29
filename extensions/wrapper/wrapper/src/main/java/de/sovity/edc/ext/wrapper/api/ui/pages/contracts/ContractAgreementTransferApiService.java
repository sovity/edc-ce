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
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.TransformerRegistryUtils;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.api.model.IdResponseDto;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;

import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;

@RequiredArgsConstructor
public class ContractAgreementTransferApiService {
    private final TransferRequestBuilder transferRequestBuilder;
    private final TransferProcessService transferProcessService;
    private final TransformerRegistryUtils transformerRegistryUtils;

    @NotNull
    public IdResponseDto initiateTransfer(
            ContractAgreementTransferRequest request
    ) {
        var transferRequestDto = transferRequestBuilder.buildTransferRequestDto(request);
        var dataRequest = transformerRegistryUtils.transformOrThrow(transferRequestDto, DataRequest.class);
        var transferProcessId = transferProcessService.initiateTransfer(dataRequest)
                .orElseThrow(exceptionMapper(TransferProcess.class, transferRequestDto.getId()));
        return IdResponseDto.Builder.newInstance()
                .id(transferProcessId)
                .createdAt(Clock.systemUTC().millis())
                .build();
    }
}
