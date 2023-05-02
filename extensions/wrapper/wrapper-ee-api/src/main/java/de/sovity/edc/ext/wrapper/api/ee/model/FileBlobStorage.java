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

package de.sovity.edc.ext.wrapper.api.ee.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.LinkedList;

@Data
@Schema(description = "Available and used resources of a connector.")
final public class FileBlobStorage extends FileStorage
{
    @Schema(description = "List of tags created for the file on the remote blob store.")
    LinkedList<String> tags;

    @Schema(description = "Is the object encrypted on the remote blob store.")
    Boolean isEncrypted;
}
