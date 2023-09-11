package de.sovity.edc.utils.catalog.model;

import lombok.Data;

import java.util.List;

@Data
public class DataOffer {
    private final String endpoint;
    private final String participantId;
    private final String assetPropertiesJsonLd;
    private final List<ContractOffer> contractOffers;
}
