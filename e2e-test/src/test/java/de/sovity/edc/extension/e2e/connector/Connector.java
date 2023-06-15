package de.sovity.edc.extension.e2e.connector;

import de.sovity.edc.extension.e2e.connector.config.EdcApiType;
import jakarta.json.JsonObject;

import java.net.URI;
import java.util.Map;

public interface Connector {

    void createAsset(String assetId, Map<String, Object> dataAddressProperties);

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

    Map<String, String> getConfig();

    URI getUriForApi(EdcApiType apiType);

    String getParticipantId();

    String initiateTransfer(
            String contractAgreementId,
            String assetId,
            URI providerProtocolApi,
            JsonObject destination);

    String getTransferProcessState(String id);
}
