/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.extension.e2e.connector;

import jakarta.json.JsonObject;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface Connector {

    void createAsset(String assetId, Map<String, Object> dataAddressProperties);

    List<String> getAssetIds();

    String createPolicy(JsonObject policyJsonObject);

    void createContractDefinition(
            String assetId,
            String contractDefinitionId,
            String accessPolicyId,
            String contractPolicyId);

    JsonObject getDatasetForAsset(String assetId, URI providerProtocolEndpoint);

    String negotiateContract(
            String providerParticipantId,
            URI providerProtocolEndpoint,
            String offerId,
            String assetId,
            JsonObject policy);

    String getParticipantId();

    String initiateTransfer(
            String contractAgreementId,
            String assetId,
            URI providerProtocolApi,
            JsonObject destination);

    String consumeOffer(
            URI providerProtocolApi,
            String assetId,
            JsonObject destination);

    String getTransferProcessState(String id);

    URI getManagementApiUri();

    URI getProtocolApiUri();
}
