/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.brokerserver.services.refreshing.offers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOfferContractOffer;
import de.sovity.edc.ext.brokerserver.utils.StreamUtils2;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class DataOfferBuilder {
    private final ObjectMapper objectMapper;

    /**
     * De-duplicates {@link ContractOffer}s into {@link FetchedDataOffer}s.
     * <p>
     * Also de-duplicates {@link ContractOffer}s into {@link FetchedDataOfferContractOffer}s.
     *
     * @param contractOffers {@link ContractOffer}s
     * @return {@link FetchedDataOffer}s
     */
    public Collection<FetchedDataOffer> deduplicateContractOffers(Collection<ContractOffer> contractOffers) {
        return groupByAssetId(contractOffers)
                .stream()
                .map(offers -> buildFetchedDataOffer(offers.get(0).getAsset(), offers))
                .toList();
    }

    @NotNull
    private FetchedDataOffer buildFetchedDataOffer(Asset asset, List<ContractOffer> offers) {
        var dataOffer = new FetchedDataOffer();
        dataOffer.setAssetId(asset.getId());
        dataOffer.setAssetPropertiesJson(getAssetPropertiesJson(asset));
        dataOffer.setContractOffers(buildFetchedDataOfferContractOffers(offers));
        return dataOffer;
    }

    @NotNull
    private List<FetchedDataOfferContractOffer> buildFetchedDataOfferContractOffers(List<ContractOffer> offers) {
        return offers.stream()
                .map(this::buildFetchedDataOfferContractOffer)
                .filter(StreamUtils2.distinctByKey(FetchedDataOfferContractOffer::getContractOfferId))
                .toList();
    }

    @NotNull
    private FetchedDataOfferContractOffer buildFetchedDataOfferContractOffer(ContractOffer offer) {
        var contractOffer = new FetchedDataOfferContractOffer();
        contractOffer.setContractOfferId(offer.getId());
        contractOffer.setPolicyJson(getPolicyJson(offer));
        return contractOffer;
    }

    private Collection<List<ContractOffer>> groupByAssetId(Collection<ContractOffer> contractOffers) {
        return contractOffers.stream().collect(groupingBy(offer -> offer.getAsset().getId())).values();
    }

    @NotNull
    @SneakyThrows
    private String getAssetPropertiesJson(Asset asset) {
        return objectMapper.writeValueAsString(asset.getProperties());
    }

    @NotNull
    @SneakyThrows
    private String getPolicyJson(ContractOffer offer) {
        return objectMapper.writeValueAsString(offer.getPolicy());
    }
}
