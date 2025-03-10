/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination;

import java.time.OffsetDateTime;

public record ContractTerminationEvent(
    String contractAgreementId,
    String detail,
    String reason,
    OffsetDateTime timestamp,
    String origin
) {
    public static ContractTerminationEvent from(ContractTerminationParam contractTerminationParam, OffsetDateTime dateTime, String origin) {
        return new ContractTerminationEvent(
            contractTerminationParam.contractAgreementId(),
            contractTerminationParam.detail(),
            contractTerminationParam.reason(),
            dateTime,
            origin
        );
    }
}
