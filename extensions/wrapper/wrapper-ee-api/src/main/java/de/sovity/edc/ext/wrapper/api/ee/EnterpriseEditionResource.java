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

package de.sovity.edc.ext.wrapper.api.ee;

import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.ee.model.ConnectorLimits;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Our sovity Enterprise Edition EDC API Endpoints to be included in our generated EDC API Wrapper Clients
 */
@Path("wrapper/ee")
@Tag(name = "Enterprise Edition", description = "sovity Enterprise Edition EDC API Endpoints. Requires our sovity Enterprise Edition EDC Extensions.")
public interface EnterpriseEditionResource {
    @GET
    @Path("connector-limits")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Available and used resources of a connector.")
    ConnectorLimits connectorLimits();

    @POST
    @Path("file-upload/blobs")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Requests a Blob for file upload.",
            description = "Requests a Blob URL with a SAS Token so that the UI can directly upload the file to the Azure Blob Storage. Returns the Blob ID / Token."
    )
    String fileUploadRequestSasToken();

    @POST
    @Path("file-upload/blobs/{blobId}/asset")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Create an asset from an uploaded file.",
            description = "Creates an asset using the uploaded file as data source."
    )
    void fileUploadCreateAsset(
            @PathParam("blobId")
            @Parameter(
                    name = "blobId",
                    in = ParameterIn.PATH,
                    description = "The Blob ID / URL the file was uploaded into."
            ) String blobId,

            @Parameter(
                    required = true,
                    description = "Metadata for the Asset. File-related metadata might be overridden."
            ) UiAssetCreateRequest assetCreateRequest
    );
}
