/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.test_utils.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TestBackendOAuth2TokenResponse(
    @field:JsonProperty("access_token")
    val accessToken: String
)
