/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination;

import de.sovity.edc.ce.db.jooq.enums.ContractTerminatedBy;
import lombok.Builder;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;

import java.time.OffsetDateTime;

@Builder(toBuilder = true)
public record ContractAgreementTerminationDetails(
    String contractAgreementId,
    String counterpartyId,
    String counterpartyAddress,
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

    boolean thisEdcIsTheConsumer() {
        return type.equals(ContractNegotiation.Type.CONSUMER);
    }

    boolean thisEdcIsTheProvider() {
        return type.equals(ContractNegotiation.Type.PROVIDER);
    }
}
