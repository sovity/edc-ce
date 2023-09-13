package de.sovity.edc.utils.catalog.model;

import jakarta.json.JsonObject;
import lombok.Data;

@Data
public class DspContractOffer {
    private final String contractOfferId;
    private final JsonObject policyJsonLd;
}
