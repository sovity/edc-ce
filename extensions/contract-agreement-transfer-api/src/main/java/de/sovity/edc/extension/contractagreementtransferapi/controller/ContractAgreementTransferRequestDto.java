/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.extension.contractagreementtransferapi.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.validation.constraints.NotNull;
import org.eclipse.edc.spi.types.domain.DataAddress;

@JsonDeserialize(builder = ContractAgreementTransferRequestDto.Builder.class)
public class ContractAgreementTransferRequestDto {

    private String id;
    @NotNull(message = "contractId cannot be null")
    private String contractAgreementId;
    @NotNull(message = "dataDestination cannot be null")
    private DataAddress dataDestination;

    public String getId() {
        return id;
    }

    public String getContractAgreementId() {
        return contractAgreementId;
    }

    public DataAddress getDataDestination() {
        return dataDestination;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private final ContractAgreementTransferRequestDto request;

        private Builder() {
            request = new ContractAgreementTransferRequestDto();
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        public Builder id(String id) {
            request.id = id;
            return this;
        }

        public Builder contractAgreementId(String contractAgreementId) {
            request.contractAgreementId = contractAgreementId;
            return this;
        }

        public Builder dataDestination(DataAddress dataDestination) {
            request.dataDestination = dataDestination;
            return this;
        }

        public ContractAgreementTransferRequestDto build() {
            return request;
        }
    }
}
