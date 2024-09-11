/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.contacttermination;

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
