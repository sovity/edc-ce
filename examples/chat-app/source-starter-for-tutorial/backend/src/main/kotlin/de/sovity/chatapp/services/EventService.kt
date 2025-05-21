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
import de.sovity.chatapp.api.model.edc.EdcEventContractNegotiationFinalized
import de.sovity.chatapp.api.model.edc.EdcEventContractNegotiationTerminated
import de.sovity.chatapp.api.model.edc.EdcEventTransferProcessStarted
import de.sovity.chatapp.services.edc.EdcService
import de.sovity.chatapp.services.persistence.CounterpartyStore
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import java.time.OffsetDateTime

@ApplicationScoped
class EventService(
    val counterpartyStore: CounterpartyStore,
    val edcService: EdcService
) {
    fun onContractNegotiationTerminated(event: EdcEventContractNegotiationTerminated) {
        Log.info("Contract negotiation terminated for participant ${event.counterPartyId}")
        counterpartyStore.update(event.counterPartyId) {
            it.copy(
                status = ConnectionStatusDto.ERROR,
                lastUpdate = OffsetDateTime.now()
            )
        }
    }

    fun onContractNegotiationFinalized(event: EdcEventContractNegotiationFinalized) {
        Log.info(
            "Contract negotiation finalized for participant ${event.counterPartyId}. " +
                "Agreement ID ${event.contractAgreement.id}. Starting transfer."
        )

        // Update Contract Agreement ID
        counterpartyStore.update(event.counterPartyId) {
            it.copy(
                contractAgreementId = event.contractAgreement.id,
                lastUpdate = OffsetDateTime.now()
            )
        }

        // Initiate transfer process
        try {
            // TODO call EDC to initiate transfer
        } catch (e: Exception) {
            Log.error("Failed to initiate transfer for participant ${event.counterPartyId}", e)
            counterpartyStore.update(event.counterPartyId) {
                it.copy(
                    status = ConnectionStatusDto.ERROR,
                    lastUpdate = OffsetDateTime.now()
                )
            }
        }
    }
}
