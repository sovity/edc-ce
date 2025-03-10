/*
 * Copyright 2025 sovity GmbH
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
 */
package de.sovity.edc.ce.api.ee;

import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ce.api.ee.model.ConnectorLimits;
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
