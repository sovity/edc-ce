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

import de.sovity.edc.ext.wrapper.api.ee.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
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
    @Path("file-storage/blobstore/files")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    FileStorageUploadResult blobStoreUploadFile(FileStorageUploadRequest request);

    @DELETE
    @Path("file-storage/blobstore/file/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    String blobStoreDeleteFile(String id);

    @GET
    @Path("file-storage/blobstore/files")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    LinkedList<FileBlobStorage> blobStoreListFiles(String id);

    @POST
    @Path("file-storage/blobstore/assets")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    String createAsset(FileStorageAssetCreateRequest request);

}
