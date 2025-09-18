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
package de.sovity.edc.utils.jsonld.vocab

import lombok.experimental.UtilityClass

/**
 * Constants for used JSON-LD Vocabulary.
 *
 *
 * Please note, that due to how JSON-LD / ontologies are defined, all property names of a namespace are just
 * mixed together on the same level. A property, e.g. type, might be used in multiple classes, which is an
 * abstraction leak by design.
 */
object Prop {
    const val ID: String = "@id"
    const val TYPE: String = "@type"
    const val VALUE: String = "@value"
    const val CONTEXT: String = "@context"
    const val LANGUAGE: String = "@language"
    const val PROPERTIES: String = "properties"

    object Edc {
        const val DSPACE: String = "https://w3id.org/dspace/v0.8/"
        const val CTX: String = "https://w3id.org/edc/v0.0.1/ns/"
        const val CTX_ALIAS: String = "edc"

        const val TYPE_ASSET: String = CTX + "Asset"
        const val TYPE_DATA_ADDRESS: String = CTX + "DataAddress"

        const val ASSET_ID: String = CTX + "assetId"
        const val AUTH_CODE: String = CTX + "authCode"
        const val AUTH_KEY: String = CTX + "authKey"
        const val AUTHORIZATION: String = CTX + "authorization"
        const val BASE_URL: String = CTX + "baseUrl"
        const val BODY: String = CTX + "body"
        const val CONNECTOR_ADDRESS: String = CTX + "connectorAddress"
        const val CONNECTOR_ID: String = CTX + "connectorId"
        const val CONTENT_TYPE: String = CTX + "contentType"
        const val CONTRACT_ID: String = CTX + "contractId"
        const val COUNTER_PARTY_ADDRESS: String = CTX + "counterPartyAddress"
        const val DATA_ADDRESS: String = CTX + "dataAddress"
        const val DATA_ADDRESS_TYPE_HTTP_DATA: String = "HttpData"
        const val DATA_ADDRESS_TYPE_HTTP_PROXY: String = "HttpProxy"
        const val DATA_DESTINATION: String = CTX + "dataDestination"
        const val ENDPOINT: String = CTX + "endpoint"
        const val ID: String = CTX + "id"
        const val MEDIA_TYPE: String = CTX + "mediaType"
        const val METHOD: String = CTX + "method"
        const val PARTICIPANT_ID: String = DSPACE + "participantId"
        const val PATH: String = CTX + "path"
        const val PRIVATE_PROPERTIES: String = CTX + "privateProperties"
        const val PROPERTIES: String = CTX + "properties"
        const val PROXY_BODY: String = CTX + "proxyBody"
        const val PROXY_METHOD: String = CTX + "proxyMethod"
        const val PROXY_PATH: String = CTX + "proxyPath"
        const val PROXY_QUERY_PARAMS: String = CTX + "proxyQueryParams"
        const val QUERY_PARAMS: String = CTX + "queryParams"
        const val RECEIVER_HTTP_ENDPOINT: String = CTX + "receiverHttpEndpoint"
        const val SECRET_NAME: String = CTX + "secretName"
        const val TRANSFER_REQUEST_TRANSFER_TYPE: String = CTX + "transferType"
        const val TYPE: String = CTX + "type"
        const val TYPE_TRANSFER_REQUEST: String = CTX + "TransferRequest"
        const val URL: String = CTX + "url"
        const val ALLOWED_SOURCE_TYPES: String = CTX + "allowedSourceTypes"
        const val ALLOWED_DEST_TYPES: String = CTX + "allowedDestTypes"
        const val ALLOWED_TRANSFER_TYPES: String = CTX + "allowedTransferTypes"
        const val PUBLIC_API_URL: String = CTX + "publicApiUrl"

        object Version07 {
            /**
             * How elements were referenced before the migration to core EDC 0.7.x
             * Preserved for backward compatibility.
             */
            const val CTX: String = "https://w3id.org/edc/v0.0.1/ns/"

            const val PARTICIPANT_ID: String = CTX + "participantId"
        }
    }

    /**
     * DCAT Vocabulary, see https://www.w3.org/TR/vocab-dcat-3
     */
    @UtilityClass
    object Dcat {
        /**
         * Context as specified in https://www.w3.org/TR/vocab-dcat-3/#normative-namespaces
         */
        // TODO: get a sample from the catalog in v0.7 and validate that this is the correct URI and update `catalogResponse.json`
        const val CTX: String = "http://www.w3.org/ns/dcat#"

        const val DATASET: String = CTX + "dataset"

        /**
         * TODO Explain what happen and link code
         */
        const val DISTRIBUTION_WILL_BE_OVERWRITTEN_BY_CATALOG: String = CTX + "distribution"
        const val VERSION: String = CTX + "version"
        const val KEYWORDS: String = CTX + "keyword"
        const val LANDING_PAGE: String = CTX + "landingPage"
        const val MEDIATYPE: String = CTX + "mediaType"
        const val START_DATE: String = CTX + "startDate"
        const val END_DATE: String = CTX + "endDate"
        const val DOWNLOAD_URL: String = CTX + "downloadURL"
    }

    /**
     * ODRL Vocabulary, see [DCAT 3 Specification](https://www.w3.org/TR/vocab-dcat-3/)
     */
    @UtilityClass
    object Odrl {
        const val CTX: String = "http://www.w3.org/ns/odrl/2/"
        const val HAS_POLICY: String = CTX + "hasPolicy"
        const val TYPE: String = CTX + "type"
        const val CONSTRAINT: String = CTX + "constraint"
        const val AND: String = CTX + "and"
        const val USE: String = "USE"
    }

    /**
     * Dcterms Metadata Terms Vocabulary, see [DCMI Metadata Terms](http://purl.org/dc/terms)
     */
    @UtilityClass
    object Dcterms {
        const val CTX: String = "http://purl.org/dc/terms/"
        const val IDENTIFIER: String = CTX + "identifier"
        const val TITLE: String = CTX + "title"
        const val DESCRIPTION: String = CTX + "description"
        const val LANGUAGE: String = CTX + "language"
        const val CREATOR: String = CTX + "creator"
        const val PUBLISHER: String = CTX + "publisher"
        const val LICENSE: String = CTX + "license"
        const val TEMPORAL: String = CTX + "temporal"
        const val ACCRUAL_PERIODICITY: String = CTX + "accrualPeriodicity"
        const val RIGHTS_HOLDER: String = CTX + "rightsHolder"
        const val RIGHTS: String = CTX + "rights"
        const val RIGHTS_STATEMENT: String = CTX + "RightsStatement"
    }

    /**
     * Dcterms Metadata Terms Vocabulary, see [DCAT 3 Specification](https://semantic.sovity.io/dcat-ext/)
     */
    @UtilityClass
    object SovityDcatExt {
        const val CTX: String = "https://semantic.sovity.io/dcat-ext#"
        const val CUSTOM_JSON: String = CTX + "customJson"
        const val PRIVATE_CUSTOM_JSON: String = CTX + "privateCustomJson"
        const val DATA_SOURCE_AVAILABILITY: String = CTX + "dataSourceAvailability"
        const val DATA_SOURCE_AVAILABILITY_ON_REQUEST: String = "ON_REQUEST"
        const val CONTACT_EMAIL: String = CTX + "contactEmail"
        const val CONTACT_PREFERRED_EMAIL_SUBJECT: String = CTX + "contactPreferredEmailSubject"
        const val DISTRIBUTION: String = CTX + "distribution"

        @UtilityClass
        object HttpDatasourceHints {
            const val METHOD: String = CTX + "httpDatasourceHintsProxyMethod"
            const val PATH: String = CTX + "httpDatasourceHintsProxyPath"
            const val QUERY_PARAMS: String = CTX + "httpDatasourceHintsProxyQueryParams"
            const val BODY: String = CTX + "httpDatasourceHintsProxyBody"
        }
    }

    @UtilityClass
    object SovityMessageExt {
        const val CTX: String = "https://semantic.sovity.io/message/generic/"
        const val REQUEST: String = CTX + "request"
        const val RESPONSE: String = CTX + "response"
        const val ERROR_MESSAGE: String = CTX + "errorMessage"
        const val HEADER: String = CTX + "header"
        const val BODY: String = CTX + "body"
    }

    /**
     * FOAF Vocabulary
     */
    @UtilityClass
    object Foaf {
        const val CTX: String = "http://xmlns.com/foaf/0.1/"
        const val ORGANIZATION: String = CTX + "Organization"
        const val NAME: String = CTX + "name"
        const val HOMEPAGE: String = CTX + "homepage"
    }

    /**
     * Namespace mobilitydcatap as specified in
     * [mobilityDCAT-AP](https://mobilitydcat-ap.github.io/mobilityDCAT-AP/releases/index.html#namespaces)
     */
    @UtilityClass
    object MobilityDcatAp {
        const val CTX: String = "https://w3id.org/mobilitydcat-ap/"
        const val MOBILITY_DATA_STANDARD: String = CTX + "mobilityDataStandard"

        // Optional property of mobilitydcatap:mobilityDataStandard
        const val SCHEMA: String = CTX + "schema"
    }

    /**
     * Namespace adms as specified in
     * [mobilityDCAT-AP](https://mobilitydcat-ap.github.io/mobilityDCAT-AP/releases/index.html#namespaces)
     */
    @UtilityClass
    object Adms {
        const val CTX: String = "http://www.w3.org/ns/adms#"
        const val SAMPLE: String = CTX + "sample"
    }

    /**
     * Namespace rdfs as specified in
     * [mobilityDCAT-AP](https://mobilitydcat-ap.github.io/mobilityDCAT-AP/releases/index.html#namespaces)
     */
    @UtilityClass
    object Rdfs {
        const val CTX: String = "http://www.w3.org/2000/01/rdf-schema#"
        const val LITERAL: String = CTX + "Literal"
        const val LABEL: String = CTX + "label"
    }

    @UtilityClass
    object Sphinx {
        const val CTX: String = "https://semantic.sphin-x.de/dcat-ext/"
        const val DATA_MODEL_NAME: String = CTX + "dataModelName"
    }
}
