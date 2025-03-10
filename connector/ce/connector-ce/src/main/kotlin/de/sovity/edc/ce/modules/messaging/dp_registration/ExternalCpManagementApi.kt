/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.dp_registration

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.config.UrlPathUtils
import jakarta.json.JsonValue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.eclipse.edc.http.spi.EdcHttpClient
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.system.configuration.Config

class ExternalCpManagementApi(
    val config: Config,
    val monitor: Monitor,
    val httpClient: EdcHttpClient
) {
    /**
     * Call the given [path] with POST on the external Control Plane's Management API
     */
    fun callPost(path: String, body: JsonValue) {
        val apiUrl = CeConfigProps.SOVITY_INTERNAL_CP_MANAGEMENT_API_URL.getStringOrThrow(config)
        val apiKeyHeader = CeConfigProps.SOVITY_INTERNAL_CP_MANAGEMENT_API_KEY_HEADER.getStringOrThrow(config)
        val apiKey = CeConfigProps.SOVITY_INTERNAL_CP_MANAGEMENT_API_KEY.getStringOrThrow(config)

        val url = UrlPathUtils.urlPathJoin(apiUrl, path)
        val requestBody = body.toString()

        monitor.info("Registering DataPlaneInstance. Calling POST $url with body $requestBody")

        val request = Request.Builder()
            .url(url)
            .addHeader(apiKeyHeader, apiKey)
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        httpClient.execute(request).use { response ->
            require(response.isSuccessful) {
                val bodyContent = response.body?.string() ?: "No content"
                "DataPlaneInstance registration failed. ${response.code} $url $bodyContent"
            }
        }
    }
}
