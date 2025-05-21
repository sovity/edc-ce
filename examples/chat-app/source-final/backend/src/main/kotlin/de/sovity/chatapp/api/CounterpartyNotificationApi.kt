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
package de.sovity.chatapp.api

import de.sovity.chatapp.api.model.MessageNotificationDto
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.core.MediaType

interface CounterpartyNotificationApi {
    /**
     * Will hit [NotificationResource.onReceiveMessage] via EDR
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun onReceiveMessage(
        messageNotificationDto: MessageNotificationDto,

        @HeaderParam("Authorization")
        authorizationHeaderValue: String,
    )
}
