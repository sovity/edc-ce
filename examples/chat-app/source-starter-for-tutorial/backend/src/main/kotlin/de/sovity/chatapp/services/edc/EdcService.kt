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
        // TODO create asset 'chat-app'
        // TODO publish data offer
    }
    // TODO fun negotiateContract(...) { ... }
    // TODO fun initiateTransfer(...) { ... }
}
