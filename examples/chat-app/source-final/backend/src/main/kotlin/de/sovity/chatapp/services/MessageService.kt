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
package de.sovity.chatapp.services

import de.sovity.chatapp.api.model.CounterpartyAddDto
import de.sovity.chatapp.api.model.MessageDirectionDto
import de.sovity.chatapp.api.model.MessageDto
import de.sovity.chatapp.api.model.MessageNotificationDto
import de.sovity.chatapp.api.model.MessageStatusDto
import de.sovity.chatapp.services.edc.EdcService
import de.sovity.chatapp.services.persistence.CounterpartyStore
import de.sovity.chatapp.services.persistence.MessageDbRow
import de.sovity.chatapp.services.persistence.MessageStore
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import java.time.OffsetDateTime
import java.util.UUID

@ApplicationScoped
class MessageService(
    private val messageStore: MessageStore,
    private val counterpartyStore: CounterpartyStore,
    private val counterpartyService: CounterpartyService,
    private var edcService: EdcService
) {
    fun onMessageReceived(participantId: String, notification: MessageNotificationDto) {
        Log.info("Message received from $participantId: ${notification.message}")
        // establish connection if not already done
        if (counterpartyStore.findByIdOrNull(participantId) == null) {
            counterpartyService.create(
                CounterpartyAddDto(
                    participantId = participantId,
                    connectorEndpoint = notification.senderConnectorEndpoint
                )
            )
        }
        // save message
        messageStore.create(
            MessageDbRow(
                messageId = UUID.randomUUID().toString(),
                participantId = participantId,
                createdAt = OffsetDateTime.now(),
                message = notification.message,
                messageDirection = MessageDirectionDto.INCOMING,
                status = MessageStatusDto.OK
            )
        )
        counterpartyStore.update(participantId) {
            it.copy(lastUpdate = OffsetDateTime.now())
        }
    }

    fun sendMessage(
        participantId: String,
        message: String
    ): MessageDto {
        val counterparty = counterpartyStore.findByIdOrThrow(participantId)
        require(counterparty.transferProcessId != null) {
            "Participant is not online. No running transfer process for participant $participantId"
        }

        val messageId = UUID.randomUUID().toString()
        messageStore.create(
            MessageDbRow(
                messageId = messageId,
                participantId = participantId,
                createdAt = OffsetDateTime.now(),
                message = message,
                messageDirection = MessageDirectionDto.OUTGOING,
                status = MessageStatusDto.SENDING
            )
        )

        val messageResult = try {
            // Wrap in error handling
            edcService.sendMessage(
                participantId = participantId,
                message = message
            )

            // Message succeeded
            messageStore.update(participantId, messageId) {
                it.copy(status = MessageStatusDto.OK)
            }
        } catch (e: Exception) {
            Log.error("Failed sending message", e)

            // Message failed
            messageStore.update(participantId, messageId) {
                it.copy(status = MessageStatusDto.ERROR)
            }
        }

        return buildMessageDto(messageResult)
    }

    fun getMessages(participantId: String): List<MessageDto> =
        messageStore.findByParticipantId(participantId)
            .sortedBy { it.createdAt }
            .map { buildMessageDto(it) }

    private fun buildMessageDto(message: MessageDbRow): MessageDto {
        return MessageDto(
            messageId = message.messageId,
            createdAt = message.createdAt,
            message = message.message,
            messageDirection = message.messageDirection,
            status = message.status
        )
    }
}
