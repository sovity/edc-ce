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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.utils.jsonld.vocab;

import lombok.experimental.UtilityClass;

/**
 * Constants for used JSON-LD Vocabulary.
 * <p>
 * Please note, that due to how JSON-LD / ontologies are defined, all property names of a namespace are just
 * mixed together on the same level. A property, e.g. type, might be used in multiple classes, which is an
 * abstraction leak by design.
 */
@UtilityClass
public class Prop {
    public final String ID = "@id";
    public final String TYPE = "@type";
    public final String VALUE = "@value";
    public final String CONTEXT = "@context";
    public final String LANGUAGE = "@language";
    public final String PROPERTIES = "properties";

    @UtilityClass
    public class Edc {
        public final String CTX = "https://w3id.org/edc/v0.0.1/ns/";
        public final String CTX_ALIAS = "edc";
        public final String TYPE_ASSET = CTX + "Asset";
        public final String TYPE_DATA_ADDRESS = CTX + "DataAddress";
        public final String ID = CTX + "id";
        public final String PARTICIPANT_ID = CTX + "participantId";
        public final String PROPERTIES = CTX + "properties";
        public final String PRIVATE_PROPERTIES = CTX + "privateProperties";
        public final String DATA_ADDRESS = CTX + "dataAddress";
        public final String TYPE = CTX + "type";
        public final String DATA_ADDRESS_TYPE_HTTP_DATA = "HttpData";
        public final String DATA_ADDRESS_TYPE_HTTP_PROXY = "HttpProxy";
        public final String BASE_URL = CTX + "baseUrl";
        public final String BODY = CTX + "body";
        public final String METHOD = CTX + "method";
        public final String CONTENT_TYPE = CTX + "contentType";
        public final String QUERY_PARAMS = CTX + "queryParams";
        public final String AUTH_KEY = CTX + "authKey";
        public final String AUTH_CODE = CTX + "authCode";
        public final String SECRET_NAME = CTX + "secretName";
        public final String PROXY_METHOD = CTX + "proxyMethod";
        public final String PROXY_PATH = CTX + "proxyPath";
        public final String PROXY_QUERY_PARAMS = CTX + "proxyQueryParams";
        public final String PROXY_BODY = CTX + "proxyBody";

        // Transfer Request Related
        public static String TYPE_TRANSFER_REQUEST = CTX + "TransferRequest";
        public final String CONNECTOR_ADDRESS = CTX + "connectorAddress";
        public final String CONTRACT_ID = CTX + "contractId";
        public final String CONNECTOR_ID = CTX + "connectorId";
        public final String ASSET_ID = CTX + "assetId";
        public final String DATA_DESTINATION = CTX + "dataDestination";
        public final String RECEIVER_HTTP_ENDPOINT = CTX + "receiverHttpEndpoint";
        public final String MEDIA_TYPE = CTX + "mediaType";
        public final String PATH = CTX + "path";
    }

    /**
     * DCAT Vocabulary, see https://www.w3.org/TR/vocab-dcat-3
     */
    @UtilityClass
    public class Dcat {
        /**
         * Context as specified in https://www.w3.org/TR/vocab-dcat-3/#normative-namespaces
         */
        // TODO: get a sample from the catalog in v0.7 and validate that this is the correct URI and update `catalogResponse.json`
        public final String CTX = "http://www.w3.org/ns/dcat#";

        public final String DATASET = CTX + "dataset";
        public final String DISTRIBUTION = CTX + "distribution";
        public final String DISTRIBUTION_AS_USED_BY_CORE_EDC = CTX + "distribution";
        public final String VERSION = CTX + "version";
        public final String KEYWORDS = CTX + "keyword";
        public final String LANDING_PAGE = CTX + "landingPage";
        public final String MEDIATYPE = CTX + "mediaType";
        public final String START_DATE = CTX + "startDate";
        public final String END_DATE = CTX + "endDate";
        public final String DOWNLOAD_URL = CTX + "downloadURL";
    }

    /**
     * ODRL Vocabulary, see <a href="https://www.w3.org/TR/vocab-dcat-3/">DCAT 3 Specification</a>
     */
    @UtilityClass
    public class Odrl {
        public final String CTX = "http://www.w3.org/ns/odrl/2/";
        public final String HAS_POLICY = CTX + "hasPolicy";
        public final String ACTION = CTX + "action";
        public final String TYPE = CTX + "type";
        public final String CONSTRAINT = CTX + "constraint";
        public final String AND = CTX + "and";
        public final String PERMISSION = CTX + "permission";
        public final String LEFT_OPERAND = CTX + "leftOperand";
        public final String RIGHT_OPERAND = CTX + "rightOperand";
        public final String USE = "USE";
    }

    /**
     * Dcterms Metadata Terms Vocabulary, see <a href="http://purl.org/dc/terms">DCMI Metadata Terms</a>
     */
    @UtilityClass
    public class Dcterms {
        public final String CTX = "http://purl.org/dc/terms/";
        public final String IDENTIFIER = CTX + "identifier";
        public final String TITLE = CTX + "title";
        public final String DESCRIPTION = CTX + "description";
        public final String LANGUAGE = CTX + "language";
        public final String CREATOR = CTX + "creator";
        public final String PUBLISHER = CTX + "publisher";
        public final String LICENSE = CTX + "license";
        public final String TEMPORAL = CTX + "temporal";
        public final String ACCRUAL_PERIODICITY = CTX + "accrualPeriodicity";
        public final String SPATIAL = CTX + "spatial";
        public final String RIGHTS_HOLDER = CTX + "rightsHolder";
        public final String RIGHTS = CTX + "rights";
        public final String RIGHTS_STATEMENT = CTX + "RightsStatement";
    }

    /**
     * Dcterms Metadata Terms Vocabulary, see <a href="https://semantic.sovity.io/dcat-ext/">DCAT 3 Specification</a>
     */
    @UtilityClass
    public class SovityDcatExt {
        public final String CTX = "https://semantic.sovity.io/dcat-ext#";
        public final String CUSTOM_JSON = CTX + "customJson";
        public final String PRIVATE_CUSTOM_JSON = CTX + "privateCustomJson";
        public final String DATA_SOURCE_AVAILABILITY = CTX + "dataSourceAvailability";
        public final String DATA_SOURCE_AVAILABILITY_ON_REQUEST = "ON_REQUEST";
        public final String CONTACT_EMAIL = CTX + "contactEmail";
        public final String CONTACT_PREFERRED_EMAIL_SUBJECT = CTX + "contactPreferredEmailSubject";

        @UtilityClass
        public class HttpDatasourceHints {
            public final String METHOD = CTX + "httpDatasourceHintsProxyMethod";
            public final String PATH = CTX + "httpDatasourceHintsProxyPath";
            public final String QUERY_PARAMS = CTX + "httpDatasourceHintsProxyQueryParams";
            public final String BODY = CTX + "httpDatasourceHintsProxyBody";
        }
    }

    @UtilityClass
    public class SovityMessageExt {
        public final String CTX = "https://semantic.sovity.io/message/generic/";
        public final String REQUEST = CTX + "request";
        public final String RESPONSE = CTX + "response";
        public final String ERROR_MESSAGE = CTX + "errorMessage";
        public final String HEADER = CTX + "header";
        public final String BODY = CTX + "body";
    }

    /**
     * FOAF Vocabulary
     */
    @UtilityClass
    public class Foaf {
        public final String CTX = "http://xmlns.com/foaf/0.1/";
        public final String ORGANIZATION = CTX + "Organization";
        public final String NAME = CTX + "name";
        public final String HOMEPAGE = CTX + "homepage";
    }

    /**
     * Namespace mobilitydcatap as specified in
     * <a href="https://mobilitydcat-ap.github.io/mobilityDCAT-AP/releases/index.html#namespaces">mobilityDCAT-AP</a>
     */
    @UtilityClass
    public class MobilityDcatAp {
        public final String CTX = "https://w3id.org/mobilitydcat-ap/";
        public final String MOBILITY_THEME = CTX + "mobilityTheme";

        @UtilityClass
        public class DataCategoryProps {
            public final String CTX = "https://w3id.org/mobilitydcat-ap/mobility-theme/";
            public final String DATA_CATEGORY = CTX + "data-content-category";
            public final String DATA_SUBCATEGORY = CTX + "data-content-sub-category";
        }

        public final String TRANSPORT_MODE = CTX + "transportMode";
        public final String GEO_REFERENCE_METHOD = CTX + "georeferencingMethod";
        public final String MOBILITY_DATA_STANDARD = CTX + "mobilityDataStandard";

        // Optional property of mobilitydcatap:mobilityDataStandard
        public final String SCHEMA = CTX + "schema";
    }

    /**
     * Namespace skos as specified in
     * <a href="https://mobilitydcat-ap.github.io/mobilityDCAT-AP/releases/index.html#namespaces">mobilityDCAT-AP</a>
     */
    @UtilityClass
    public class Skos {
        public final String CTX = "http://www.w3.org/2004/02/skos/core#";
        public final String PREF_LABEL = CTX + "prefLabel";
    }

    /**
     * Namespace adms as specified in
     * <a href="https://mobilitydcat-ap.github.io/mobilityDCAT-AP/releases/index.html#namespaces">mobilityDCAT-AP</a>
     */
    @UtilityClass
    public class Adms {
        public final String CTX = "http://www.w3.org/ns/adms#";
        public final String SAMPLE = CTX + "sample";
    }

    /**
     * Namespace rdfs as specified in
     * <a href="https://mobilitydcat-ap.github.io/mobilityDCAT-AP/releases/index.html#namespaces">mobilityDCAT-AP</a>
     */
    @UtilityClass
    public class Rdfs {
        public final String CTX = "http://www.w3.org/2000/01/rdf-schema#";
        public final String LITERAL = CTX + "Literal";
        public final String LABEL = CTX + "label";
    }
}
