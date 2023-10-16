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

package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RequiredArgsConstructor
public class AssetJsonLdUtils {

    public String getId(JsonObject assetJsonLd) {
        return JsonLdUtils.string(assetJsonLd, Prop.ID);
    }

    public String getTitle(JsonObject assetJsonLd) {
        var properties = JsonLdUtils.object(assetJsonLd, Prop.Edc.PROPERTIES);
        var title = JsonLdUtils.string(properties, Prop.Dcterms.TITLE);
        return isBlank(title) ? getId(assetJsonLd) : title;
    }
}
