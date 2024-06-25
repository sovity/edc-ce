/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 */

package de.sovity.edc.extension.contactcancellation.controller;

import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.extension.db.directaccess.DirectDatabaseAccess;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.val;

import static de.sovity.edc.extension.contactcancellation.controller.ContractCancellationController.PATH;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@RequiredArgsConstructor
@Produces({APPLICATION_JSON})
@Path(PATH)
public class ContractCancellationController {

    // TODO: what would be an appropriate path?
    public static final String PATH = "/sovity/contract";

    private final DirectDatabaseAccess dda;

    @POST
    @Path("/cancel")
    public Response cancel(ContractCancellationDto cancellation) {
//        val cancellationExists = dda.getDslContext().select(Tables.SOVITY_CONTRACT_CANCELLATION)
//            .where(Tables.SOVITY_CONTRACT_CANCELLATION.CONTRACT_ID.eq(cancellation.contractId()))
//            .fetchOne();

        return Response.ok().build();
    }
}
