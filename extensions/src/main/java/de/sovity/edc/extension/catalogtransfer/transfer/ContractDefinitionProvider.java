/*
 *  Copyright (c) 2020 - 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *       Fraunhofer Institute for Software and Systems Engineering - Improvements
 *       Microsoft Corporation - Use IDS Webhook address for JWT audience claim
 *
 */

package de.sovity.edc.extension.catalogtransfer.transfer;

import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;

import java.util.List;

@ExtensionPoint
public interface ContractDefinitionProvider {
    List<ContractDefinition> pollCreatedContractDefinitions();

    List<String> pollDeletedContractDefinitionIds();

}
