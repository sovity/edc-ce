package de.sovity.edc.utils.jsonld.vocab;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Prop {
    public final String ID = "@id";

    @UtilityClass
    public class Edc {
        public final String CTX = "https://w3id.org/edc/v0.0.1/ns/";
        public final String ID = CTX + "id";
        public final String PARTICIPANT_ID = CTX + "participantId";
    }

    /**
     * DCAT Vocabulary, see <a href="https://www.w3.org/TR/vocab-dcat-3/">DCAT 3 Specification</a>
     */
    @UtilityClass
    public class Dcat {
        public final String CTX = "https://www.w3.org/ns/dcat/";
        public final String DATASET = CTX + "dataset";
    }

    /**
     * ODRL Vocabulary, see <a href="https://www.w3.org/TR/vocab-dcat-3/">DCAT 3 Specification</a>
     */
    @UtilityClass
    public class Odrl {
        public final String CTX = "http://www.w3.org/ns/odrl/2/";
        public final String HAS_POLICY = CTX + "hasPolicy";
    }
}
