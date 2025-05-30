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
package de.sovity.edc.ce.api.ui.pages.contract_agreements.services;

import de.sovity.edc.ce.api.utils.MapUtils;
import de.sovity.edc.ce.api.utils.ServiceException;
import de.sovity.edc.ce.db.jooq.tables.records.SovityContractTerminationRecord;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.asset.spi.index.AssetIndex;
import org.eclipse.edc.connector.controlplane.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.controlplane.services.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static de.sovity.edc.ce.db.jooq.Tables.SOVITY_CONTRACT_TERMINATION;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Service
public class ContractAgreementDataFetcher {
    private final ContractAgreementService contractAgreementService;
    private final ContractNegotiationStore contractNegotiationStore;
    private final TransferProcessService transferProcessService;
    private final AssetIndex assetIndex;

    /**
     * Fetches all contract agreements as {@link ContractAgreementData}s.
     *
     * @return {@link ContractAgreementData}s
     */
    @NotNull
    public List<ContractAgreementData> getContractAgreements(DSLContext dsl) {
        var agreements = getAllContractAgreements();
        var assets = MapUtils.associateBy(getAllAssets(), Asset::getId);

        var negotiations = getAllContractNegotiations().stream()
            .filter(it -> it.getContractAgreement() != null)
            .collect(groupingBy(it -> it.getContractAgreement().getId()));

        var transfers = getAllTransferProcesses().stream()
            .collect(groupingBy(TransferProcess::getContractId));

        var agreementIds = agreements.stream().map(ContractAgreement::getId).toList();

        var terminations = fetchTerminations(dsl, agreementIds);

        // A ContractAgreement has multiple ContractNegotiations when doing a loopback consumption
        return agreements.stream()
            .flatMap(agreement -> negotiations.getOrDefault(agreement.getId(), List.of())
                .stream()
                .map(negotiation -> {
                    var asset = getAsset(agreement, negotiation, assets::get);
                    var contractTransfers = transfers.getOrDefault(agreement.getId(), List.of());
                    return new ContractAgreementData(agreement, negotiation, asset, contractTransfers, terminations.get(agreement.getId()));
                }))
            .toList();
    }

    @NotNull
    public ContractAgreementData getContractAgreement(DSLContext dsl, String contractAgreementId) {
        val agreement = getContractAgreementById(contractAgreementId);

        val negotiationQuery = QuerySpec.max();
        val negotiation = contractNegotiationStore.queryNegotiations(negotiationQuery)
            .filter(it -> it.getContractAgreement().getId().equals(contractAgreementId))
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("Can't find any negotiation for contract agreement id %s".formatted(contractAgreementId)));

        val transfers = getAllTransferProcesses().stream().collect(groupingBy(TransferProcess::getContractId));

        val terminations = fetchTerminations(dsl, agreement.getId());

        val asset = getAsset(agreement, negotiation, (it) -> assetIndex.findById(agreement.getAssetId()));

        return new ContractAgreementData(
            agreement,
            negotiation,
            asset,
            transfers.getOrDefault(agreement.getId(), List.of()),
            terminations.get(agreement.getId())
        );
    }

    private ContractAgreement getContractAgreementById(String id) {
        return contractAgreementService.findById(id);
    }

    @NotNull
    private Map<String, SovityContractTerminationRecord> fetchTerminations(DSLContext dsl, String agreementIds) {
        return fetchTerminations(dsl, List.of(agreementIds));
    }

    @NotNull
    private Map<String, SovityContractTerminationRecord> fetchTerminations(DSLContext dsl, List<String> agreementIds) {
        var t = SOVITY_CONTRACT_TERMINATION;

        return dsl.select()
            .from(t)
            .where(t.CONTRACT_AGREEMENT_ID.in(agreementIds))
            .fetch()
            .into(t)
            .stream()
            .collect(toMap(SovityContractTerminationRecord::getContractAgreementId, identity()));
    }

    private Asset getAsset(ContractAgreement agreement, ContractNegotiation negotiation, Function<String, Asset> selector) {
        var assetId = agreement.getAssetId();

        if (negotiation.getType() == ContractNegotiation.Type.CONSUMER) {
            return dummyAsset(assetId);
        }

        var asset = selector.apply(assetId);
        return asset == null ? dummyAsset(assetId) : asset;
    }

    private Asset dummyAsset(String assetId) {
        return Asset.Builder.newInstance().id(assetId).build();
    }

    private List<Asset> getAllAssets() {
        return assetIndex.queryAssets(QuerySpec.max()).toList();
    }

    @NotNull
    private List<ContractNegotiation> getAllContractNegotiations() {
        return contractNegotiationStore.queryNegotiations(QuerySpec.max()).toList();
    }

    @NotNull
    private List<ContractAgreement> getAllContractAgreements() {
        return contractAgreementService.search(QuerySpec.max()).orElseThrow(ServiceException::new);
    }

    @NotNull
    private List<TransferProcess> getAllTransferProcesses() {
        return transferProcessService.search(QuerySpec.max()).orElseThrow(ServiceException::new);
    }
}
