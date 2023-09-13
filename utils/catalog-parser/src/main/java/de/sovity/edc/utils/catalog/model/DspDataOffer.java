package de.sovity.edc.utils.catalog.model;

import jakarta.json.JsonObject;
import lombok.Data;

import java.util.List;

@Data
public class DspDataOffer {
    private final String endpoint;
    private final String participantId;
    private final JsonObject assetPropertiesJsonLd;
    private final List<DspContractOffer> contractOffers;
}
