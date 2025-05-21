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

import de.sovity.chatapp.api.model.CounterpartyAddDto
import de.sovity.chatapp.api.model.CounterpartyDto
import de.sovity.chatapp.api.model.MessageDto
import de.sovity.chatapp.api.model.MessageSendDto
import de.sovity.chatapp.services.CounterpartyService
import de.sovity.chatapp.services.MessageService
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("api/ui")
class UiResource(
    val counterpartyService: CounterpartyService,
    val messageService: MessageService
) {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("counterparties")
    fun listCounterparties(): List<CounterpartyDto> =
        counterpartyService.listCounterparties()

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("counterparties")
    fun addCounterparty(request: CounterpartyAddDto): CounterpartyDto =
        counterpartyService.create(request)

    @DELETE
    @Path("counterparties/{participantId}")
    fun removeCounterparty(
        @PathParam("participantId") participantId: String
    ): Response {
        counterpartyService.remove(participantId)
        return Response.noContent().build()
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("counterparties/{participantId}/messages")
    fun getAllMessages(
        @PathParam("participantId") participantId: String
    ): List<MessageDto> =
        messageService.getMessages(participantId)

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("counterparties/{participantId}/messages")
    fun sendMessage(
        @PathParam("participantId") participantId: String,
        request: MessageSendDto
    ): MessageDto =
        messageService.sendMessage(participantId, request.message)
}
