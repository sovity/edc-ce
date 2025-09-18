/*
 * Copyright 2025 sovity GmbH
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
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
 *     Fraunhofer ISST - contributions to the Eclipse EDC 0.2.0 migration
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements.services

import de.sovity.edc.ce.api.ui.pages.asset.AssetRs.Companion.fetchAsset
import de.sovity.edc.ce.api.utils.ServiceException
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.ce.db.jooq.tables.records.SovityContractTerminationRecord
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset
import org.eclipse.edc.connector.controlplane.contract.spi.negotiation.store.ContractNegotiationStore
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.eclipse.edc.connector.controlplane.services.spi.contractagreement.ContractAgreementService
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess
import org.eclipse.edc.spi.query.QuerySpec
import org.jooq.DSLContext
import kotlin.streams.asSequence

@Service
class ContractAgreementDataFetcher(
    val contractAgreementService: ContractAgreementService,
    val contractNegotiationStore: ContractNegotiationStore,
    val transferProcessService: TransferProcessService,
) {
    /**
     * Fetches all contract agreements as [ContractAgreementData]s.
     *
     * @return [ContractAgreementData]s
     */
    fun getContractAgreements(dsl: DSLContext): List<ContractAgreementData> {
        val agreements = allContractAgreements

        val negotiations = allContractNegotiations
            .filter { it.contractAgreement != null }
            .groupBy { it.contractAgreement!!.id }

        val transfers = allTransferProcesses
            .groupBy { obj: TransferProcess -> obj.contractId }

        val agreementIds = agreements.map { obj: ContractAgreement -> obj.id }

        val terminations = fetchTerminations(dsl, agreementIds)

        // A ContractAgreement has multiple ContractNegotiations when doing a loopback consumption
        return agreements.flatMap { agreement ->
            negotiations.getOrDefault(agreement.id, listOf())
                .map { negotiation ->
                    val asset = getAsset(dsl, agreement, negotiation)
                    val contractTransfers = transfers.getOrDefault(agreement.id, listOf())
                    ContractAgreementData(
                        agreement,
                        negotiation,
                        asset,
                        contractTransfers,
                        terminations[agreement.id]
                    )
                }
        }.toList()
    }

    fun getContractAgreement(dsl: DSLContext, contractAgreementId: String): ContractAgreementData {
        val agreement = getContractAgreementById(contractAgreementId)

        val negotiationQuery = QuerySpec.max()
        val negotiation =
            contractNegotiationStore.queryNegotiations(negotiationQuery)
                .asSequence()
                .filter { it.contractAgreement != null }
                .filter { it.contractAgreement!!.id == contractAgreementId }
                .firstOrNull()

        check(negotiation != null) {
            "Can't find any negotiation for contract agreement id $contractAgreementId"
        }

        val transfers = allTransferProcesses.groupBy { it.contractId }
        val terminations = fetchTerminations(dsl, agreement.id)
        val asset = getAsset(dsl, agreement, negotiation)

        return ContractAgreementData(
            agreement,
            negotiation,
            asset,
            transfers.getOrDefault(agreement.id, listOf()),
            terminations[agreement.id]
        )
    }

    private fun getContractAgreementById(id: String): ContractAgreement =
        contractAgreementService.findById(id)

    private fun fetchTerminations(dsl: DSLContext, agreementIds: String): Map<String, SovityContractTerminationRecord> =
        fetchTerminations(dsl, java.util.List.of(agreementIds))

    private fun fetchTerminations(
        dsl: DSLContext,
        agreementIds: List<String?>
    ): Map<String, SovityContractTerminationRecord> {
        val t = Tables.SOVITY_CONTRACT_TERMINATION

        return dsl.select()
            .from(t)
            .where(t.CONTRACT_AGREEMENT_ID.`in`(agreementIds))
            .fetch()
            .into(t)
            .map { it.contractAgreementId to it }
            .toMap()
    }

    private fun getAsset(dsl: DSLContext, agreement: ContractAgreement, negotiation: ContractNegotiation): Asset {
        val assetId = agreement.assetId

        if (negotiation.type == ContractNegotiation.Type.CONSUMER) {
            return dummyAsset(assetId)
        }

        val asset = fetchAsset(dsl, assetId)
        return asset?.toAsset() ?: dummyAsset(assetId)
    }

    private fun dummyAsset(assetId: String): Asset =
        Asset.Builder.newInstance().id(assetId).build()

    private val allContractNegotiations: List<ContractNegotiation>
        get() = contractNegotiationStore.queryNegotiations(QuerySpec.max()).toList()

    private val allContractAgreements: List<ContractAgreement>
        get() = contractAgreementService.search(QuerySpec.max()).orElseThrow { ServiceException(it) }

    private val allTransferProcesses: List<TransferProcess>
        get() = transferProcessService.search(QuerySpec.max()).orElseThrow { ServiceException(it) }
}
