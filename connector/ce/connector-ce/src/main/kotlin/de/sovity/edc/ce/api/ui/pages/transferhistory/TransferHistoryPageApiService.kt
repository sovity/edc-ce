/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.transferhistory

import de.sovity.edc.ce.api.ui.model.ContractAgreementDirection
import de.sovity.edc.ce.api.ui.model.TransferHistoryEntry
import de.sovity.edc.ce.api.ui.pages.contract_agreements.services.ContractAgreementDirectionUtils
import de.sovity.edc.ce.api.utils.EdcDateUtils
import de.sovity.edc.ce.api.utils.ServiceException
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.vocab.Prop
import org.apache.commons.lang3.StringUtils
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset
import org.eclipse.edc.connector.controlplane.contract.spi.negotiation.store.ContractNegotiationStore
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService
import org.eclipse.edc.connector.controlplane.services.spi.contractagreement.ContractAgreementService
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess
import org.eclipse.edc.spi.query.QuerySpec

@Service
class TransferHistoryPageApiService(
    private val assetService: AssetService,
    private val contractAgreementService: ContractAgreementService,
    private val contractNegotiationStore: ContractNegotiationStore,
    private val transferProcessService: TransferProcessService,
    private val transferProcessStateService: TransferProcessStateService,
) {

    /**
     * Fetches all Transfer History entries as [TransferHistoryEntry]s.
     *
     * @return [TransferHistoryEntry]s
     */
    fun getTransferHistoryEntries(): List<TransferHistoryEntry> {
        val negotiationsById = getAllContractNegotiations()
            .filter { it.contractAgreement != null }
            .associateBy { it.contractAgreement.id }
        val agreementsById = getAllContractAgreements().associateBy { it.id }
        val assetsById = getAllAssets().associateBy { it.id }
        val transferProcesses = getAllTransferProcesses()

        return transferProcesses.map { process ->
            val agreement = agreementsById[process.contractId]
            val negotiation = negotiationsById[process.contractId]
            val asset = assetLookup(assetsById, process)
            val direction = negotiation?.type?.let { ContractAgreementDirectionUtils.fromType(it) }
            val transferHistoryEntry = TransferHistoryEntry()
            transferHistoryEntry.assetId = asset.id

            transferHistoryEntry.assetName = direction?.let {
                transferHistoryEntry.direction = direction
                when (it) {
                    ContractAgreementDirection.CONSUMING -> {
                        asset.id
                    }

                    ContractAgreementDirection.PROVIDING -> {
                        if (StringUtils.isBlank(asset.properties[Prop.Dcterms.TITLE] as? String))
                            asset.id
                        else
                            asset.properties[Prop.Dcterms.TITLE].toString()
                    }
                }
            }
            negotiation?.let {
                transferHistoryEntry.counterPartyConnectorEndpoint = it.counterPartyAddress
                transferHistoryEntry.counterPartyParticipantId = it.counterPartyId
                transferHistoryEntry.createdDate = EdcDateUtils.utcMillisToOffsetDateTime(it.createdAt)
            }

            agreement?.let {
                transferHistoryEntry.contractAgreementId = it.id
            }

            transferHistoryEntry.transferType = process.transferType
            transferHistoryEntry.errorMessage = process.errorDetail
            transferHistoryEntry.lastUpdatedDate = EdcDateUtils.utcMillisToOffsetDateTime(process.updatedAt)
            transferHistoryEntry.state = transferProcessStateService.buildTransferProcessState(process.state)
            transferHistoryEntry.transferProcessId = process.id
            transferHistoryEntry.isEdrConsumable = transferProcessStateService.isEdrConsumable(
                process.state,
                process.transferType
            )
            transferHistoryEntry
        }.sortedWith(compareByDescending { it.lastUpdatedDate })
    }

    private fun assetLookup(assetsById: Map<String, Asset>, process: TransferProcess): Asset {
        val assetId = process.assetId
        val asset = assetsById[assetId] ?: return Asset.Builder.newInstance().id(assetId).build()
        return asset
    }

    private fun getAllContractNegotiations(): List<ContractNegotiation> =
        contractNegotiationStore.queryNegotiations(QuerySpec.max()).toList()

    private fun getAllContractAgreements(): List<ContractAgreement> =
        contractAgreementService.search(QuerySpec.max()).orElseThrow { ServiceException(it) }
            .toList()

    private fun getAllTransferProcesses(): List<TransferProcess> =
        transferProcessService.search(QuerySpec.max()).orElseThrow { ServiceException(it) }.toList()

    private fun getAllAssets(): List<Asset> =
        assetService.search(QuerySpec.max()).orElseThrow { ServiceException(it) }.toList()
}
