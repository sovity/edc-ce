package de.sovity.edc.utils.catalog.model;

import lombok.Data;

@Data
public class ContractOffer {
    private final String contractOfferId;
    private final String policyJsonLd;
}
