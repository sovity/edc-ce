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

package de.sovity.edc.ext.wrapper.api.ui.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Schema(description = "Transfer History Entry for Transfer History Page")
@JsonDeserialize(builder = TransferHistoryEntry.Builder.class)
public class TransferHistoryEntry {
    @Schema(description = "Transfer Process ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String transferProcessId;

    @Schema(description = "Created Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdDate;

    @Schema(description = "Last Change Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime lastUpdatedDate;

    @Schema(description = "Transfer History State", requiredMode = Schema.RequiredMode.REQUIRED)
    private TransferProcessState state;

    @Schema(description = "Contract Agreement ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractAgreementId;

    @Schema(description = "Incoming vs Outgoing", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractAgreementDirection direction;

    @Schema(description = "Other Connector's Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String counterPartyConnectorEndpoint;

    @Schema(description = "Asset Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetName;

    @Schema(description = "Asset ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetId;

    @Schema(description = "Error Message")
    private String errorMessage;

    public TransferHistoryEntry(String transferProcessId, OffsetDateTime createdDate, OffsetDateTime lastUpdatedDate, TransferProcessState state, String contractAgreementId, ContractAgreementDirection direction, String counterPartyConnectorEndpoint, String assetName, String assetId, String errorMessage) {
        this.assetId = assetId;
        this.assetName = assetName;
        this.contractAgreementId = contractAgreementId;
        this.counterPartyConnectorEndpoint = counterPartyConnectorEndpoint;
        this.createdDate = createdDate;
        this.direction = direction;
        this.errorMessage = errorMessage;
        this.lastUpdatedDate = lastUpdatedDate;
        this.state = state;
        this.transferProcessId = transferProcessId;
    }

    public TransferHistoryEntry() {

    }


    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private String transferProcessId;
        private OffsetDateTime createdDate;
        private OffsetDateTime lastUpdatedDate;
        private TransferProcessState state;
        private String contractAgreementId;
        private ContractAgreementDirection direction;
        private String counterPartyConnectorEndpoint;
        private String assetName;
        private String assetId;
        private String errorMessage;


        @JsonCreator
        public static TransferHistoryEntry.Builder newInstance() {
            return new TransferHistoryEntry.Builder();
        }

        public TransferHistoryEntry.Builder transferProcessId(String id) {
            this.transferProcessId = transferProcessId;
            return this;
        }

        public TransferHistoryEntry.Builder createdDate(OffsetDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public TransferHistoryEntry.Builder lastUpdatedDate(OffsetDateTime lastUpdatedDate) {
            this.lastUpdatedDate = lastUpdatedDate;
            return this;
        }

        public TransferHistoryEntry.Builder state(TransferProcessState state) {
            this.state = state;
            return this;
        }

        public TransferHistoryEntry.Builder contractAgreementId(String contractAgreementId) {
            this.contractAgreementId = contractAgreementId;
            return this;
        }

        public TransferHistoryEntry.Builder direction(ContractAgreementDirection direction) {
            this.direction = direction;
            return this;
        }

        public TransferHistoryEntry.Builder counterPartyConnectorEndpoint(String counterPartyConnectorEndpoint) {
            this.counterPartyConnectorEndpoint = counterPartyConnectorEndpoint;
            return this;
        }

        public TransferHistoryEntry.Builder assetName(String assetName) {
            this.assetName = assetName;
            return this;
        }

        public TransferHistoryEntry.Builder assetId(String assetId) {
            this.assetId = assetId;
            return this;
        }

        public TransferHistoryEntry.Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public TransferHistoryEntry build() {
            return new TransferHistoryEntry(transferProcessId, createdDate, lastUpdatedDate, state, contractAgreementId, direction, counterPartyConnectorEndpoint, assetName, assetId, errorMessage);
        }
    }

}
