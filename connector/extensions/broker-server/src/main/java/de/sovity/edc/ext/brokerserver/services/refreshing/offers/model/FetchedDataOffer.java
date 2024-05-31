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

package de.sovity.edc.ext.brokerserver.services.refreshing.offers.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Contains data offer response as required for writing into DB.
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FetchedDataOffer {
    String assetId;
    String assetTitle;
    String description;
    String curatorOrganizationName;
    String dataCategory;
    String dataSubcategory;
    String dataModel;
    String transportMode;
    String geoReferenceMethod;
    List<String> keywords;
    String assetJsonLd;
    List<FetchedContractOffer> contractOffers;
}
