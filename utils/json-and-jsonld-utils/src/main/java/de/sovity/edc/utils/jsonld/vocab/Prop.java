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
    public final String LANGUAGE = "@language";

    @UtilityClass
    public class Edc {
        public final String CTX = "https://w3id.org/edc/v0.0.1/ns/";
        public final String TYPE_ASSET = CTX + "Asset";
        public final String ID = CTX + "id";
        public final String PARTICIPANT_ID = CTX + "participantId";
        public final String PROPERTIES = CTX + "properties";
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
        public final String TITLE = CTX + "TITLE";
        public final String DESCRIPTION = CTX + "DESCRIPTION";
    }

    /**
     * Dcterms Metadata Terms Vocabulary, see <a href="https://semantic.sovity.io/dcat-ext/">DCAT 3 Specification</a>
     */
    @UtilityClass
    public class SovityDcatExt {
        public final String CTX = "https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxy";
        public final String METHOD = CTX + "method/";
        public final String PATH = CTX + "path/";

    }
}
