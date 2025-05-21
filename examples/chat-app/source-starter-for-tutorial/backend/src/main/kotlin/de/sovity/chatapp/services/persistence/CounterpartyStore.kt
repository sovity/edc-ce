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
class CounterpartyStore {
    /**
     * In-Memory store is sufficient for this demo
     */
    private val participants = mutableMapOf<String, CounterpartyDbRow>()

    fun findByIdOrNull(participantId: String): CounterpartyDbRow? =
        participants[participantId]

    fun findByIdOrThrow(participantId: String): CounterpartyDbRow =
        findByIdOrNull(participantId) ?: error("Participant with ID $participantId not found")

    fun findByContractAgreementIdOrThrow(contractAgreementId: String): CounterpartyDbRow =
        participants.values.find { it.contractAgreementId == contractAgreementId }
            ?: error("Participant with contract agreement ID $contractAgreementId not found")

    fun findAll(): List<CounterpartyDbRow> =
        participants.values.toList()

    fun create(dto: CounterpartyDbRow) {
        participants[dto.participantId] = dto
    }

    fun update(
        participantId: String,
        updateFn: (CounterpartyDbRow) -> CounterpartyDbRow
    ): CounterpartyDbRow {
        val old = findByIdOrThrow(participantId)
        val new = updateFn(old)
        participants[participantId] = new
        return new
    }
}
