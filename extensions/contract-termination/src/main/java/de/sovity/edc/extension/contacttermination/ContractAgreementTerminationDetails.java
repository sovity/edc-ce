/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 */

package de.sovity.edc.extension.contacttermination;

import de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy;
import lombok.Builder;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;

import java.time.OffsetDateTime;

@Builder(toBuilder = true)
public record ContractAgreementTerminationDetails(
    String contractAgreementId,
    String counterpartyId,
    String counterpartyAddress,
    ContractNegotiationStates state,
    ContractNegotiation.Type type,
    String providerAgentId,
    String consumerAgentId,
    String reason,
    String detail,
    OffsetDateTime terminatedAt,
    ContractTerminatedBy terminatedBy
) {
    public boolean isTerminated() {
        return terminatedAt != null;
    }
}
