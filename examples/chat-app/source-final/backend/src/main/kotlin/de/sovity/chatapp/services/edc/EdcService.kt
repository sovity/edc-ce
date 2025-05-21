/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.chatapp.services.edc

import de.sovity.chatapp.api.CounterpartyNotificationApiClient
import de.sovity.chatapp.api.NotificationCallbackUrls
import de.sovity.chatapp.api.model.MessageNotificationDto
import de.sovity.chatapp.services.persistence.CounterpartyStore
import de.sovity.edc.client.EdcClient
import de.sovity.edc.client.gen.model.AssetFilterConstraint
import de.sovity.edc.client.gen.model.AssetFilterConstraintOperator
import de.sovity.edc.client.gen.model.CallbackAddressDto
import de.sovity.edc.client.gen.model.CallbackAddressEventType
import de.sovity.edc.client.gen.model.ContractDefinitionRequest
import de.sovity.edc.client.gen.model.DataSourceType
import de.sovity.edc.client.gen.model.NegotiateAllQuery
import de.sovity.edc.client.gen.model.UiAssetCreateRequest
import de.sovity.edc.client.gen.model.UiAssetEditRequest
import de.sovity.edc.client.gen.model.UiCriterion
import de.sovity.edc.client.gen.model.UiCriterionLiteral
import de.sovity.edc.client.gen.model.UiCriterionLiteralType
import de.sovity.edc.client.gen.model.UiCriterionOperator
import de.sovity.edc.client.gen.model.UiDataSource
import de.sovity.edc.client.gen.model.UiDataSourceHttpData
import de.sovity.edc.client.gen.model.UiDataSourceHttpDataMethod
import de.sovity.edc.client.gen.model.UiInitiateTransferRequest
import de.sovity.edc.client.gen.model.UiInitiateTransferType
import de.sovity.edc.utils.jsonld.vocab.Prop
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class EdcService(
    private val edcClient: EdcClient,
    private val notificationCallbackUrls: NotificationCallbackUrls,
    private val counterpartyStore: CounterpartyStore,
    private val edrCache: EdrCache,
    private val counterpartyNotificationApiClient: CounterpartyNotificationApiClient,

    @ConfigProperty(name = "chat-app.edc.connector-endpoint")
    private val ownConnectorEndpoint: String
) {
    fun configureAsset() {
        val onMessageReceiveNotification = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(
                UiDataSourceHttpData.builder()
                    .method(UiDataSourceHttpDataMethod.POST)
                    .baseUrl(notificationCallbackUrls.getOnMessageReceivedUrl())
                    .enableBodyParameterization(true)
                    .build()
            )
            .build()

        if (edcClient.uiApi().isAssetIdAvailable("chat-app").available!!) {
            Log.info("Creating asset 'chat-app'")
            edcClient.uiApi().createAsset(
                UiAssetCreateRequest.builder()
                    .id("chat-app")
                    .dataSource(onMessageReceiveNotification)
                    .build()
            )
        } else {
            Log.info("Updating asset 'chat-app'")
            edcClient.uiApi().editAsset(
                "chat-app",
                UiAssetEditRequest.builder()
                    .dataSourceOverrideOrNull(onMessageReceiveNotification)
                    .build()
            )
        }
        if (edcClient.uiApi().isContractDefinitionIdAvailable("chat-app").available!!) {
            Log.info("Creating contract definition 'chat-app'")
            edcClient.uiApi().createContractDefinition(
                ContractDefinitionRequest.builder()
                    .contractDefinitionId("chat-app")
                    .accessPolicyId("always-true") // unrestricted access
                    .contractPolicyId("always-true") // unrestricted access
                    .assetSelector(
                        listOf(
                            UiCriterion.builder()
                                .operandLeft(Prop.Edc.ID)
                                .operator(UiCriterionOperator.EQ)
                                .operandRight(
                                    UiCriterionLiteral.builder()
                                        .type(UiCriterionLiteralType.VALUE)
                                        .value("chat-app")
                                        .build()
                                )
                                .build()
                        )
                    )
                    .build()
            )
        } else {
            Log.info("Already existing contract definition 'chat-app'. Skipping creation")
        }
    }
    fun negotiateContract(participantId: String, connectorEndpoint: String) {
        val negotiations = edcClient.useCaseApi().negotiateAll(
            NegotiateAllQuery.builder()
                .participantId(participantId)
                .connectorEndpoint(connectorEndpoint)
                .filter(
                    listOf(
                        AssetFilterConstraint.builder()
                            .assetPropertyPath(listOf(Prop.Edc.ID))
                            .operator(AssetFilterConstraintOperator.EQ)
                            .value("chat-app")
                            .build()
                    )
                )
                .callbackAddresses(
                    listOf(
                        CallbackAddressDto.builder()
                            .url(notificationCallbackUrls.getContractNegotiationFinalizedUrl())
                            .events(listOf(CallbackAddressEventType.CONTRACT_NEGOTIATION_FINALIZED))
                            .build(),
                        CallbackAddressDto.builder()
                            .url(notificationCallbackUrls.getContractNegotiationTerminatedUrl())
                            .events(listOf(CallbackAddressEventType.CONTRACT_NEGOTIATION_TERMINATED))
                            .build(),
                    )
                )
                .build()
        )
        require(negotiations.size >= 1) {
            "No asset 'chat-app' found."
        }
    }
    fun initiateTransfer(participantId: String) {
        val counterparty = counterpartyStore.findByIdOrThrow(participantId)
        edcClient.uiApi().initiateTransferV2(
            UiInitiateTransferRequest.builder()
                .contractAgreementId(counterparty.contractAgreementId!!)
                .type(UiInitiateTransferType.HTTP_DATA_PROXY)
                .callbackAddresses(
                    listOf(
                        CallbackAddressDto.builder()
                            .url(notificationCallbackUrls.getTransferStartedUrl())
                            .events(listOf(CallbackAddressEventType.TRANSFER_PROCESS_STARTED))
                            .build()
                    )
                )
                .build()
        )
    }
    fun sendMessage(participantId: String, message: String) {
        val messageNotificationDto = MessageNotificationDto(
            message = message,
            senderConnectorEndpoint = ownConnectorEndpoint
        )
        val edr = edrCache.getEdr(participantId)
        counterpartyNotificationApiClient.sendMessage(edr, messageNotificationDto)
    }
}
