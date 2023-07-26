/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.ui.pages.contracts;

import de.sovity.edc.ext.wrapper.api.ui.model.ContractDefinitionEntry;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.ContractAgreementData;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@RequiredArgsConstructor
public class ContractDefinitionApiService {


    private final ContractDefinitionService contractDefinitionService;
    public List<ContractDefinitionEntry> getContractDefinitions() {
        // Obtain all contract definitions
        var definitions = getAllContractDefinitons();
        return definitions.stream()
                .map(definition -> {
                    var entry = new ContractDefinitionEntry();
                    entry.setContractDefinitionId(definition.getId());
                    entry.setAccessPolicyId(definition.getAccessPolicyId());
                    entry.setContractPolicyId(definition.getContractPolicyId());
                    entry.setCriteria(definition.getSelectorExpression().getCriteria()); // This depends on how the criteria are stored in your system
                    return entry;
                })
                .collect(Collectors.toList());
    }
    private List<ContractDefinition> getAllContractDefinitons() {
        return contractDefinitionService.query(QuerySpec.max()).getContent().toList();
    }

}
