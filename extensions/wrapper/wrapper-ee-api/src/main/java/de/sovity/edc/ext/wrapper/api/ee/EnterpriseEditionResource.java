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
import de.sovity.edc.ext.wrapper.api.ee.model.FileStorageUploadResult;
import de.sovity.edc.ext.wrapper.api.ee.model.FileBlobStorage;
import de.sovity.edc.ext.wrapper.api.ee.model.FileStorageUploadRequest;
import de.sovity.edc.ext.wrapper.api.ee.model.FileStorageAssetCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;

import java.util.LinkedList;

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
    @Path("file-storage/blob-store/files")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Upload a file to the remote blob store.")
    FileStorageUploadResult blobStoreUploadFile(FileStorageUploadRequest request);

    @GET
    @Path("file-storage/blob-store/files")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get all uploaded files stored on the remote blob store.")
    LinkedList<FileBlobStorage> blobStoreListFiles();

    @POST
    @Path("file-storage/blob-store/assets")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Create the blob store file storage asset.")
    FileBlobStorage createAsset(FileStorageAssetCreateRequest request);
}
