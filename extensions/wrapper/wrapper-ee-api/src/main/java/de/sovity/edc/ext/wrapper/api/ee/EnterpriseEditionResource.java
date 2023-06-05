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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

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

    @GET
    @Path("file-storage/tokens")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get a token to upload a stored file.",
            description = "Get a temporarily token that can be used to upload a stored file in a file storage."
    )
    StoredFile fileUploadToken();

    @GET
    @Path("file-storage/stored-files")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get all stored files.",
            description = "Get all files stored in a file storage."
    )
    List<StoredFile> listStoredFiles();

    @POST
    @Path("file-storage/{storedFileId}/assets")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Create a file storage asset.",
            description = "Create an asset using the stored file as data source."
    )
    StoredFile createStoredFileAsset(
            @PathParam("storedFileId")
            @Parameter(
                    name = "storedFileId",
                    in = ParameterIn.PATH,
                    description = "The id of the StoredFile object.",
                    schema = @Schema(example = "stored-file-001")
            ) String storedFileId,

            @Parameter(
                    required = true,
                    description = "Map<String, String> representing a list with the asset properties of the stored file.",
                    schema = @Schema(example = "{\"asset:prop:id\": \"some-asset-1\",\n \"asset:prop:originator\": \"http://my-example-connector/api/v1/ids\"}")
            ) Map<String, String> assetProperties
    );
}
