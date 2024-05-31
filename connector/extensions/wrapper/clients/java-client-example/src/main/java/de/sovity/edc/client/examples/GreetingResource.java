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

package de.sovity.edc.client.examples;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.KpiResult;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class GreetingResource {
    @Inject
    EdcClient edcClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public KpiResult getKpis() {
        return edcClient.useCaseApi().getKpis();
    }

    @GET
    @Path("supported-policy-functions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getSupportedPolicyFunctions() {
        return edcClient.useCaseApi().getSupportedFunctions();
    }
}
