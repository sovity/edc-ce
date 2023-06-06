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

import de.sovity.edc.extension.contractagreementtransferapi.service.TransferRequestService;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.api.model.DataAddressDto;
import org.eclipse.edc.api.model.IdResponseDto;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;

import java.time.Clock;

import static java.lang.String.format;
import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.mapToException;

@Produces({MediaType.APPLICATION_JSON})
@Path("/contract-agreements-transfer/contractagreements")
public class ContractAgreementTransferApiController {
    private final Monitor monitor;
    private final TransferProcessService transferProcessService;
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final TransferRequestService dataRequestService;

    public ContractAgreementTransferApiController(
            Monitor monitor,
            TransferProcessService transferProcessService,
            TypeTransformerRegistry typeTransformerRegistry,
            TransferRequestService dataRequestService) {
        this.monitor = monitor;
        this.transferProcessService = transferProcessService;
        this.typeTransformerRegistry = typeTransformerRegistry;
        this.dataRequestService = dataRequestService;
    }

    @POST
    @Path("/{contractAgreementId}/transfer")
    public IdResponseDto initiateTransfer(@PathParam("contractAgreementId") @NotNull String contractAgreementId, DataAddressDto dataAddressDto) {
        var transformResult = typeTransformerRegistry.transform(dataAddressDto, DataAddress.class);
        if (transformResult.failed()) {
            throw new InvalidRequestException(transformResult.getFailureMessages());
        }
        var dataRequest = dataRequestService.buildTransferRequest(contractAgreementId,
                transformResult.getContent());
        monitor.debug("Starting transfer for asset " + dataRequest.getDataRequest().getAssetId());

        var createdTransferProcess = transferProcessService.initiateTransfer(dataRequest)
                .onSuccess(tp -> monitor.debug(format("Transfer Process created %s", tp.getId())))
                .orElseThrow(failure -> mapToException(failure, TransferProcess.class));

        return IdResponseDto.Builder.newInstance()
                .id(createdTransferProcess.getId())
                .createdAt(createdTransferProcess.getCreatedAt())
                .build();
    }


}
