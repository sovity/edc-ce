package de.sovity.edc.ext.wrapper.api.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static de.sovity.edc.ext.wrapper.TestUtils.createConfiguration;
import static de.sovity.edc.ext.wrapper.TestUtils.givenManagementEndpoint;

@ApiTest
@ExtendWith(EdcExtension.class)
class OfferingResourceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static JsonNode contractOfferValid;
    private ObjectNode contractOffer;

    private static final String ASSET_ENTRY_KEY = "assetEntry";
    private static final String POLICY_DEFINITION_REQUEST = "policyDefinitionRequest";
    private static final String CONTRACT_DEFINITION_REQUEST = "contractDefinitionRequest";
    private static final String NO_REQUEST_BODY_ERROR = "No CreateOfferingDto provided";

    @BeforeAll
    static void init() throws IOException {
        ClassLoader classLoader = OfferingResourceTest.class.getClassLoader();
        contractOfferValid = MAPPER.readTree(new File(
                classLoader.getResource("usecase/contract-offer-valid.json").getFile()));
    }

    @BeforeEach
    void setUp(EdcExtension extension) throws JsonProcessingException {
        extension.setConfiguration(createConfiguration());
        contractOffer = (ObjectNode) MAPPER.readTree(contractOfferValid.toString());
    }

    ValidatableResponse whenCreateOfferingEndpoint(String body) {
        return givenManagementEndpoint()
                .when()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/wrapper/use-case-api/contract-offer")
                .then()
                .log()
                .all();
    }

    ValidatableResponse whenJsonPropertyMissing(String property) {
        contractOffer.remove(property);
        return whenCreateOfferingEndpoint(contractOffer.toString());
    }

    @Test
    void shouldCreateOffer() {
        whenCreateOfferingEndpoint(contractOfferValid.toString())
                .assertThat()
                .statusCode(204);
    }

    @Test
    void shouldNotCreateOfferMissingAssetEntry() {
        whenJsonPropertyMissing(ASSET_ENTRY_KEY)
                .assertThat()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body(Matchers.containsStringIgnoringCase(ASSET_ENTRY_KEY));
    }

    @Test
    void shouldNotCreateOfferMissingPolicyDefinitionRequest() {
        whenJsonPropertyMissing(POLICY_DEFINITION_REQUEST)
                .assertThat()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body(Matchers.containsStringIgnoringCase(POLICY_DEFINITION_REQUEST));
    }

    @Test
    void shouldNotCreateOfferMissingContractDefinitionRequest() {
        whenJsonPropertyMissing(CONTRACT_DEFINITION_REQUEST)
                .assertThat()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body(Matchers.containsStringIgnoringCase(CONTRACT_DEFINITION_REQUEST));
    }

    @Test
    void shouldNotCreateOfferEmptyRequestBody() {
        whenCreateOfferingEndpoint("")
                .assertThat()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body(Matchers.containsStringIgnoringCase(NO_REQUEST_BODY_ERROR));
    }

    @Test
    void shouldNotCreateOfferGarbageRequestBody() {
        whenCreateOfferingEndpoint("asdf")
                .assertThat()
                .statusCode(400);
    }

}
