/*
 * Copyright 2025 sovity GmbH
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer ISST - initial implementation of an unified workflow for creating data offerings
 */
package de.sovity.edc.ce.api.usecase;

import de.sovity.edc.ce.api.common.model.CatalogQueryV2;
import de.sovity.edc.ce.api.common.model.EdrDto;
import de.sovity.edc.ce.api.ui.model.IdResponseDto;
import de.sovity.edc.ce.api.ui.model.UiDataOffer;
import de.sovity.edc.ce.api.usecase.model.CatalogQuery;
import de.sovity.edc.ce.api.usecase.model.ContractNegotiationStateResult;
import de.sovity.edc.ce.api.usecase.model.KpiResult;
import de.sovity.edc.ce.api.usecase.model.NegotiateAllQuery;
import de.sovity.edc.ce.api.usecase.model.NegotiateAllResult;
import de.sovity.edc.ce.api.usecase.model.TransferProcessStateResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;


/**
 * Provides the endpoints for use-case specific requests.
 */
@Path("wrapper/use-case-api")
@Tag(name = "Use Case", description = "Generic Use Case Application API Endpoints.")
public interface UseCaseResource {

    @GET
    @Path("kpis")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Basic KPIs about the running EDC Connector.")
    KpiResult getKpis();

    @GET
    @Path("supported-policy-functions")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "List available functions in policies, prohibitions and obligations.")
    List<String> getSupportedFunctions();

    @POST
    @Path("catalog")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Fetch a connector's data offers", deprecated = true)
    @Deprecated
    List<UiDataOffer> queryCatalog(
        @NotNull
        CatalogQuery catalogQuery
    );

    @POST
    @Path("catalog-v2")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Fetch a connector's data offers filtering the catalog by nested asset properties")
    List<UiDataOffer> queryCatalogV2(
        @NotNull
        CatalogQueryV2 catalogQuery
    );

    @POST
    @Path("negotiations/quick-initiate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Negotiate all assets with the given nested asset property filter. Only negotiates assets without active agreements. Returns existing agreements if found.")
    List<NegotiateAllResult> negotiateAll(
        @NotNull
        NegotiateAllQuery negotiateAllQuery
    );

    @POST
    @Path("contract-negotiations/states")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Fetch contract negotiation states as batch")
    List<ContractNegotiationStateResult> getContractNegotiationStates(
        @NotNull
        List<String> negotiationIds
    );

    @POST
    @Path("transfers/states")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Fetch transfer process states as batch")
    List<TransferProcessStateResult> getTransferProcessStates(
        @NotNull
        List<String> transferIds
    );

    @POST
    @Path("transfers/{transferId}/edr")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Fetch the EDR for a given transfer process. Refreshes EDR if expired. The transfer must have been started as HttpData-PROXY.")
    EdrDto getTransferProcessEdr(
        @NotNull
        @PathParam("transferId")
        String transferId
    );

    @POST
    @Path("transfers/{transferId}/terminate")
    @Operation(description = "Delete the EDR and terminate the given transfer process.")
    @Produces(MediaType.APPLICATION_JSON)
    IdResponseDto terminateTransferProcess(
        @NotNull
        @PathParam("transferId")
        String transferId
    );
}
