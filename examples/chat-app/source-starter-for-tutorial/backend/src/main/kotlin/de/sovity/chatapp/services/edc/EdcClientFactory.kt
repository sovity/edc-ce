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
package de.sovity.chatapp.services.edc

import de.sovity.edc.client.EdcClient
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.lang.Thread.sleep
import java.time.Duration

@ApplicationScoped
class EdcClientFactory(
    @ConfigProperty(name = "chat-app.edc.management-api.url")
    private val managementApiUrl: String,

    @ConfigProperty(name = "chat-app.edc.management-api.key")
    private val managementApiKey: String
) {

    @Produces
    fun edcClient(): EdcClient {
        val client = EdcClient.builder()
            .managementApiUrl(managementApiUrl)
            .managementApiKey(managementApiKey)
            .build()

        retry(
            times = 60,
            delay = Duration.ofSeconds(1),
            messageOnFailure = "Failed to connect the EDC Management API at $managementApiUrl"
        ) {
            client.testConnection()
            Log.info("Successfully connected to EDC")
        }

        return client
    }

    private fun retry(
        times: Int,
        delay: Duration,
        messageOnFailure: String,
        block: () -> Unit
    ) {
        repeat(times) {
            try {
                block()
                return
            } catch (e: Exception) {
                if (it == times - 1) {
                    throw RuntimeException(messageOnFailure, e)
                }
                try {
                    sleep(delay.toMillis())
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }
    }
}

