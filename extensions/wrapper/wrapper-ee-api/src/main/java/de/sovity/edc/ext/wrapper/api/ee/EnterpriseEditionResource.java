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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Upload a file.",
               description = "Upload a file to the file storage. <br> On a successful upload to the file storage " +
                       "a StoredFile object is returned. <br> The assetProperties remain empty and are only added upon " +
                       "a asset create request.")
    @RequestBody(content =
            @Content(mediaType = MediaType.MULTIPART_FORM_DATA,
                     schema = @Schema(type = "object",
                                      requiredProperties = {"fileName", "fileContent"}),
                     schemaProperties = { @SchemaProperty(name = "fileName",
                                                          schema = @Schema(type = "string",
                                                                           format = "string",
                                                                           example = "myFile.txt")
                                                         ),
                                          @SchemaProperty(name = "fileContent",
                                                          schema = @Schema(type = "string",
                                                                           format = "binary",
                                                                           example = "fileContent")
                                                         )
                    }
            )
    )

    @ApiResponses(value ={
            @ApiResponse(responseCode = "200",
                         description = "File successfully uploaded to the file storage.",
                         content = @Content(schema = @Schema(implementation = StoredFile.class))
            )}
    )
    StoredFile uploadStoredFile(
            @Parameter(description = "The name of the file.",
                       required = true,
                       style = ParameterStyle.FORM
            ) String fileName,

            @Parameter(description = "The file content to upload.",
                       required = true,
                       style = ParameterStyle.FORM
            ) byte[] fileContent
    );

    //ToDo: this API endpoint might be extended with query parameters in the future.
    @GET
    @Path("file-storage/stored-files")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all uploaded files.",
               description = "Get all uploaded files from the file storage.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         description = "Successfully processed the list of StoreFile objects.",
                         content = @Content(array = @ArraySchema(schema = @Schema(implementation = StoredFile.class)))
            )}
    )
    List<StoredFile> listStoredFiles();

    @POST
    @Path("file-storage/{storedFileId}/assets")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create the file storage asset.",
               description = "Create the file storage asset for the given stored file id.")
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200",
                         description = "File store asset successfully created.",
                         content = @Content(schema = @Schema(implementation = StoredFile.class))
            )}
    )
    StoredFile createStoredFileAsset(
            @Parameter(name = "storedFileId",
                       in = ParameterIn.PATH,
                       description = "The id of the StoredFile object.",
                       schema = @Schema(example = "toBeDefined")
            ) String storedFileId,

            @Parameter(required = true,
                       description = "Map<String, String> representing a list with the asset properties of the stored file.",
                       schema = @Schema(example = "{\"asset-key-x\": \"assetKeyValueX\",\n \"asset-key-y\": \"assetKeyValueY\"}")
            ) Map<String, String> assetProperties
    );
}
