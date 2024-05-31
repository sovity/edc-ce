/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.utils.catalog.model;

import jakarta.json.JsonObject;
import lombok.Data;

import java.util.List;

@Data
public class DspDataOffer {
    private final JsonObject assetPropertiesJsonLd;
    private final List<DspContractOffer> contractOffers;
    private final List<JsonObject> distributions;
}
