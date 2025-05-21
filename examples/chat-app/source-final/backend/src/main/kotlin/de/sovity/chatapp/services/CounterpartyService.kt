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

import de.sovity.chatapp.api.model.ConnectionStatusDto
import de.sovity.chatapp.api.model.CounterpartyAddDto
import de.sovity.chatapp.api.model.CounterpartyDto
import de.sovity.chatapp.services.edc.EdcService
import de.sovity.chatapp.services.persistence.CounterpartyDbRow
import de.sovity.chatapp.services.persistence.CounterpartyStore
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import java.time.OffsetDateTime

@ApplicationScoped
class CounterpartyService(
    val counterpartyStore: CounterpartyStore,
    val edcService: EdcService
) {
    fun listCounterparties(): List<CounterpartyDto> {
        return counterpartyStore.findAll()
            .sortedByDescending { it.lastUpdate }
            .map { buildDto(it) }
    }

    fun create(dto: CounterpartyAddDto): CounterpartyDto {
        require(counterpartyStore.findByIdOrNull(dto.participantId) == null) {
            "Participant with ID ${dto.participantId} already exists"
        }
        Log.info("Establishing connection with ${dto.participantId}")

        // save participant in-memory
        counterpartyStore.create(
            CounterpartyDbRow(
                participantId = dto.participantId,
                connectorEndpoint = dto.connectorEndpoint,
                status = ConnectionStatusDto.CONNECTING,
                lastUpdate = OffsetDateTime.now(),
                contractNegotiationId = null,
                contractAgreementId = null,
                transferProcessId = null,
                edr = null
            )
        )

        try {
            // connect via EDC
            // callback will continue the connection establishment process
            edcService.negotiateContract(
                participantId = dto.participantId,
                connectorEndpoint = dto.connectorEndpoint,
            )
        } catch (ex: Exception) {
            Log.error("Failed establishing connection", ex)

            // failed to connect
            counterpartyStore.update(dto.participantId) {
                it.copy(
                    status = ConnectionStatusDto.ERROR,
                    lastUpdate = OffsetDateTime.now()
                )
            }
        }

        return buildDto(counterpartyStore.findByIdOrThrow(dto.participantId))
    }

    fun remove(participantId: String) {
        TODO("Not yet implemented")
    }

    private fun buildDto(dbRow: CounterpartyDbRow) =
        CounterpartyDto(
            participantId = dbRow.participantId,
            connectorEndpoint = dbRow.connectorEndpoint,
            status = dbRow.status,
            lastUpdate = dbRow.lastUpdate
        )
}
