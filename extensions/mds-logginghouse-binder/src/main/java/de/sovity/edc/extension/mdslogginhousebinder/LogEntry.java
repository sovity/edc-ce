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

package de.sovity.edc.extension.mdslogginhousebinder;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.contacttermination.ContractTerminationEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class LogEntry {

    @JsonProperty("contractAgreementId")
    private String contractAgreementId;

    @JsonProperty("event")
    private String event;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("timestamp")
    private OffsetDateTime timestamp;

    public static LogEntry from(String event, ContractTerminationEvent contractTerminationEvent) {
        return LogEntry.builder()
            .event(event)
            .contractAgreementId(contractTerminationEvent.contractAgreementId())
            .detail(contractTerminationEvent.detail())
            .reason(contractTerminationEvent.reason())
            .timestamp(contractTerminationEvent.timestamp())
            .build();
    }
}
