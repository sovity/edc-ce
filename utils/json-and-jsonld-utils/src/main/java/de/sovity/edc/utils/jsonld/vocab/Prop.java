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

package de.sovity.edc.utils.jsonld.vocab;

import lombok.experimental.UtilityClass;

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
        public final String BASE_URL = CTX + "baseUrl";
        public final String METHOD = CTX + "method";
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
    }

    /**
     * DCAT Vocabulary, see https://www.w3.org/TR/vocab-dcat-3
     */
    @UtilityClass
    public class Dcat {
        /**
         * Context as specified in https://www.w3.org/TR/vocab-dcat-3/#normative-namespaces
         */
        public final String CTX = "http://www.w3.org/ns/dcat#";

        /**
         * Context as used in the Core EDC, or atleast how its output from a DCAT request
         */
        public final String CTX_WRONG_BUT_USED_BY_CORE_EDC = "https://www.w3.org/ns/dcat/";

        public final String DATASET = CTX_WRONG_BUT_USED_BY_CORE_EDC + "dataset";
        public final String DISTRIBUTION = CTX_WRONG_BUT_USED_BY_CORE_EDC + "distribution";
        public final String VERSION = CTX + "version";
        public final String KEYWORDS = CTX + "keyword";
        public final String LANDING_PAGE = CTX + "landingPage";
        public final String MEDIATYPE = CTX + "mediaType";
    }

    /**
     * ODRL Vocabulary, see <a href="https://www.w3.org/TR/vocab-dcat-3/">DCAT 3 Specification</a>
     */
    @UtilityClass
    public class Odrl {
        public final String CTX = "http://www.w3.org/ns/odrl/2/";
        public final String HAS_POLICY = CTX + "hasPolicy";
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
    }

    /**
     * Dcterms Metadata Terms Vocabulary, see <a href="https://semantic.sovity.io/dcat-ext/">DCAT 3 Specification</a>
     */
    @UtilityClass
    public class SovityDcatExt {
        public final String CTX = "https://semantic.sovity.io/dcat-ext#";
        public final String CUSTOM_JSON = CTX + "customJson";
        public final String PRIVATE_CUSTOM_JSON = CTX + "privateCustomJson";

        @UtilityClass
        public class HttpDatasourceHints {
            public final String METHOD = CTX + "httpDatasourceHintsProxyMethod";
            public final String PATH = CTX + "httpDatasourceHintsProxyPath";
            public final String QUERY_PARAMS = CTX + "httpDatasourceHintsProxyQueryParams";
            public final String BODY = CTX + "httpDatasourceHintsProxyBody";
        }
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
     * MDS Vocabulary
     */
    @UtilityClass
    public class Mds {
        public final String CTX = "http://w3id.org/mds#";
        public final String DATA_CATEGORY = CTX + "dataCategory";
        public final String DATA_SUBCATEGORY = CTX + "dataSubcategory";
        public final String DATA_MODEL = CTX + "dataModel";
        public final String GEO_REFERENCE_METHOD = CTX + "geoReferenceMethod";
        public final String TRANSPORT_MODE = CTX + "transportMode";
    }

    /**
     * Temporary Context for all MDS specific fields awaiting proper classification within the mobilityDCAT-AP specification
     */
    @UtilityClass
    public class MdsDcatExt {
        public final String CTX = "https://semantic.sovity.io/mds-dcat-ext#";

        public final String SOVEREIGN = CTX + "sovereign";
        public final String GEO_LOCATION = CTX + "geolocation";
        public final String NUTS_LOCATION = CTX + "nuts-location";
        public final String DATA_SAMPLE_URLS = CTX + "data-sample-urls";
        public final String REFERENCE_FILES = CTX + "reference-files";
        public final String ADDITIONAL_DESCRIPTION = CTX + "additional-description";
        public final String CONDITIONS_FOR_USE = CTX + "conditions-for-use";
        public final String DATA_UPDATE_FREQUENCY = CTX + "data-update-frequency";
        public final String TEMPORAL_COVERAGE_FROM = CTX + "temporal-coverage-from";
        public final String TEMPORAL_COVERAGE_TO = CTX + "temporal-coverage-to";
    }
}
