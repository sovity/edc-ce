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
package de.sovity.chatapp.services.persistence

import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MessageStore {
    /**
     * In-Memory store is sufficient for this demo
     */
    private val messages = mutableMapOf<String, MutableList<MessageDbRow>>()

    fun findByParticipantId(participantId: String): List<MessageDbRow> =
        messages[participantId]?.toList() ?: emptyList()

    fun create(
        message: MessageDbRow
    ) {
        messages.computeIfAbsent(message.participantId) { mutableListOf() }
            .add(message)
    }

    fun update(
        participantId: String,
        messageId: String,
        updateFn: (MessageDbRow) -> MessageDbRow
    ): MessageDbRow {
        val old = findByParticipantId(participantId)
            .firstOrNull { it.messageId == messageId }
            ?: error("Message with ID $messageId not found for participant $participantId")
        messages[participantId]?.remove(old)

        val new = updateFn(old)
        messages[participantId]?.add(new)
        return new
    }
}
