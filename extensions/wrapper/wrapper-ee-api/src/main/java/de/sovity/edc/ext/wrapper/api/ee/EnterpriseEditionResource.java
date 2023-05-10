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
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
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
    @Path("file-storage/stored-files")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(
            summary = "Upload a file.",
            description = "Upload a file to the file storage. <br> On a successful upload to the file storage " +
                "a StoredFile object is returned. <br> The assetProperties remain empty and are only added upon " +
                "a asset create request."
    )
    @RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA,
                    schema = @Schema(
                            type = "object",
                            requiredProperties = {"fileName", "fileContent"}),
                    schemaProperties = {
                            @SchemaProperty(
                                    name = "fileName",
                                    schema = @Schema(
                                            type = "string",
                                            format = "string",
                                            example = "myFile.txt")
                            ),
                            @SchemaProperty(
                                    name = "fileContent",
                                    schema = @Schema(
                                            type = "string",
                                            format = "binary",
                                            example = "fileContent")
                            )
                    }
            )
    )

    StoredFile uploadStoredFile(
            @Parameter(
                    description = "The name of the file.",
                    required = true,
                    style = ParameterStyle.FORM
            ) String fileName,

            @Parameter(
                    description = "The file content to upload.",
                    required = true,
                    style = ParameterStyle.FORM
            ) byte[] fileContent
    );

    @GET
    @Path("file-storage/stored-files")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get all stored files.",
            description = "Get all files stored in file storage"
    )
    List<StoredFile> listStoredFiles();

    @POST
    @Path("file-storage/{storedFileId}/assets")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Create the file storage asset.",
            description = "Create an asset using the stored file as data source."
    )
    StoredFile createStoredFileAsset(
            @Parameter(
                    name = "storedFileId",
                    in = ParameterIn.PATH,
                    description = "The id of the StoredFile object.",
                    schema = @Schema(example = "a-stored-file-001")
            ) String storedFileId,

            @Parameter(
                    required = true,
                    description = "Map<String, String> representing a list with the asset properties of the stored file.",
                    schema = @Schema(example = "{\"asset:prop:id\": \"some-asset-1\",\n \"asset:prop:originator\": \"http://my-example-connector/api/v1/ids\"}")
            ) Map<String, String> assetProperties
    );
}
