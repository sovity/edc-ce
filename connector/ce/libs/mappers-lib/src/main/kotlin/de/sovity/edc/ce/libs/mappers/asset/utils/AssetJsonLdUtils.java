/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers.asset.utils;

import de.sovity.edc.runtime.simple_di.Service;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RequiredArgsConstructor
@Service
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
