/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.utils

import de.sovity.edc.runtime.config.UrlPathUtils
import org.mockserver.client.ForwardChainExpectation
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.RequestDefinition

fun ClientAndServer.getUrl(path: String? = null): String =
    UrlPathUtils.urlPathJoin("http://localhost:$port", path)

fun ClientAndServer.whenever(requestDefinition: RequestDefinition): ForwardChainExpectation =
    `when`(requestDefinition)
