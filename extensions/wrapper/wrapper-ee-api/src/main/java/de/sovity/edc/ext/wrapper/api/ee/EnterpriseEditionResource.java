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

import de.sovity.edc.ext.wrapper.api.ee.model.ConnectorLimits;
import de.sovity.edc.ext.wrapper.api.ee.model.StoredFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Map;

/**
 * Our EDC Enterprise Edition API Endpoints to be included in our generated EDC API Wrapper Clients
 */
@Path("wrapper/ee")
@Tag(name = "Enterprise Edition", description = "EDC Enterprise Edition API Endpoints. Included here for ease of use.")
public interface EnterpriseEditionResource {
    @GET
    @Path("connector-limits")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Available and used resources of a connector.")
    ConnectorLimits connectorLimits();

    @POST
    @Path("file-storage/files}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Upload a file to the file storage.")
    StoredFile blobStoreUploadFile(String fileName, byte[] data);

    @GET
    @Path("file-storage/files")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get all uploaded files from the file storage.")
    ArrayList<StoredFile> listStoredFiles();

    @POST
    @Path("file-storage/{storedFileId}/assets")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Create the file storage asset.")
    StoredFile createAsset(@PathParam("storedFileId") String id, Map<String, String> assetProperties);
}
