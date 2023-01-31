/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.extension.contractagreementtransferapi.controller;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.api.model.IdResponseDto;
import org.eclipse.edc.api.transformer.DtoTransformerRegistry;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;

import java.time.Clock;

@Produces({MediaType.APPLICATION_JSON})
@Path("/transfer")
public class ContractAgreementTransferApiController {
    private final Monitor monitor;
    private final TransferProcessService transferProcessService;
    private final DtoTransformerRegistry transformerRegistry;

    public ContractAgreementTransferApiController(
            Monitor monitor,
            TransferProcessService transferProcessService,
            DtoTransformerRegistry transformerRegistry) {
        this.monitor = monitor;
        this.transferProcessService = transferProcessService;
        this.transformerRegistry = transformerRegistry;
    }

    @POST
    public IdResponseDto initiateTransfer(ContractAgreementTransferRequestDto transferRequest) {
        var transformResult = transformerRegistry.transform(transferRequest, DataRequest.class);
        if (transformResult.failed()) {
            throw new InvalidRequestException(transformResult.getFailureMessages());
        }
        var dataRequest = transformResult.getContent();
        monitor.debug("Starting transfer for asset " + dataRequest.getAssetId());

        var result = transferProcessService.initiateTransfer(dataRequest);
        if (result.succeeded()) {
            monitor.debug(String.format("Transfer process initialised %s", result.getContent()));
            return IdResponseDto.Builder.newInstance()
                    .id(result.getContent())
                    //To be accurate createdAt should come from the transfer object
                    .createdAt(Clock.systemUTC().millis())
                    .build();
        } else {
            throw new InvalidRequestException(result.getFailureMessages());
        }
    }

}
