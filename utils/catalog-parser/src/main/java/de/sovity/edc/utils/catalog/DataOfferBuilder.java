package de.sovity.edc.utils.catalog;

import de.sovity.edc.utils.catalog.model.ContractOffer;
import de.sovity.edc.utils.catalog.model.DataOffer;
import de.sovity.edc.utils.catalog.utils.JsonLdUtils;
import de.sovity.edc.utils.catalog.utils.JsonUtils;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public class DataOfferBuilder {
    private static final String DCAT = "https://www.w3.org/ns/dcat/";
    private static final String EDC = "https://w3id.org/edc/v0.0.1/ns/";
    private static final String ODRL = "http://www.w3.org/ns/odrl/2/";

    private static final String HAS_POLICY = ODRL + "hasPolicy";
    private static final String PARTICIPANT_ID = EDC + "participantId";
    private static final String DATASET = DCAT + "dataset";

    private final JsonLd jsonLd;

    public List<DataOffer> buildDataOffers(String endpoint, JsonObject json) {
        json = jsonLd.expand(json).getContent();
        String participantId = JsonLdUtils.string(json.get(PARTICIPANT_ID));

        return JsonLdUtils.arrayOfObjects(json.get(DATASET)).stream()
                .map(dataset -> buildDataOffer(endpoint, participantId, dataset))
                .toList();
    }

    private DataOffer buildDataOffer(String endpoint, String participantId, JsonObject dataset) {
        var contractOffers = JsonLdUtils.arrayOfObjects(dataset.get(HAS_POLICY)).stream()
                .map(this::buildContractOffer)
                .toList();

        var asset = Json.createObjectBuilder(dataset).remove(HAS_POLICY).build();
        String assetPropertiesJsonLd = JsonUtils.toJson(asset);

        return new DataOffer(
                endpoint,
                participantId,
                assetPropertiesJsonLd,
                contractOffers
        );
    }

    @NotNull
    private ContractOffer buildContractOffer(JsonObject json) {
        return new ContractOffer(JsonLdUtils.id(json), JsonUtils.toJson(json));
    }
}
