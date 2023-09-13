package de.sovity.edc.utils.catalog.mapper;

import de.sovity.edc.utils.catalog.model.DspContractOffer;
import de.sovity.edc.utils.catalog.model.DspDataOffer;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public class DspDataOfferBuilder {

    private final JsonLd jsonLd;

    public List<DspDataOffer> buildDataOffers(String endpoint, JsonObject json) {
        json = jsonLd.expand(json).getContent();
        String participantId = JsonLdUtils.string(json.get(Prop.Edc.PARTICIPANT_ID));

        return JsonLdUtils.arrayOfObjects(json.get(Prop.Dcat.DATASET)).stream()
                .map(dataset -> buildDataOffer(endpoint, participantId, dataset))
                .toList();
    }

    private DspDataOffer buildDataOffer(String endpoint, String participantId, JsonObject dataset) {
        var contractOffers = JsonLdUtils.arrayOfObjects(dataset.get(Prop.Odrl.HAS_POLICY)).stream()
                .map(this::buildContractOffer)
                .toList();

        var assetProperties = Json.createObjectBuilder(dataset).remove(Prop.Odrl.HAS_POLICY).build();

        return new DspDataOffer(
                endpoint,
                participantId,
                assetProperties,
                contractOffers
        );
    }

    @NotNull
    private DspContractOffer buildContractOffer(JsonObject json) {
        return new DspContractOffer(JsonLdUtils.id(json), json);
    }
}
