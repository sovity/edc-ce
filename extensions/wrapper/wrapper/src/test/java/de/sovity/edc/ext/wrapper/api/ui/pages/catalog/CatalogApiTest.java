package de.sovity.edc.ext.wrapper.api.ui.pages.catalog;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.ext.wrapper.TestUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
public class CatalogApiTest {
    private EdcClient client;

    @BeforeEach
    void setUp(EdcExtension extension) {
        message("Setup");
        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();
    }

    /**
     * There used to be issues with the Prop.DISTRIBUTION field being occupied by core EDC.
     * This test verifies that the field can be used by us.
     */
    @Test
    void test_Distribution_Key() {
        message("Testing");
        var catalogPageDataOffers = client.uiApi().getCatalogPageDataOffers("http://localhost:34003/api/dsp");
    }

    private void message(String message) {
        System.err.println(message);
    }

}
