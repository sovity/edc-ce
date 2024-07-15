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

package de.sovity.edc.extension.contactcancellation;

import lombok.Builder;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;

import java.time.OffsetDateTime;

// TODO: this is probably duplicating elements between provider/consumer/counterparty/negotiation type
//  remove the duplicates
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
    OffsetDateTime terminatedAt
) {
    public boolean isTerminated() {
        return terminatedAt != null;
    }
}
