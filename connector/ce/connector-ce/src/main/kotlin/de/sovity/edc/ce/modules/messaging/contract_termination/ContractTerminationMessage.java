/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.ce.modules.messaging.messenger.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ContractTerminationMessage implements SovityMessage {

    @JsonProperty("contractAgreementId")
    private String contractAgreementId;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("reason")
    private String reason;

    @Override
    public String getType() {
        return "io.sovity.message.contract.terminate";
    }
}
