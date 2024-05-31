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

package de.sovity.edc.extension;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces({MediaType.APPLICATION_JSON})
@Path("/last-commit-info")
public class LastCommitInfoController {
    private final LastCommitInfoService lastCommitInfoService;

    public LastCommitInfoController(LastCommitInfoService lastCommitInfoService) {
        this.lastCommitInfoService = lastCommitInfoService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LastCommitInfo getLastCommitInfo() {
        return lastCommitInfoService.getLastCommitInfo();
    }
}
