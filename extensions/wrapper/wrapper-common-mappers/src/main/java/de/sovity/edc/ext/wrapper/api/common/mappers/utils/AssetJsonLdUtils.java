package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssetJsonLdUtils {

    protected static String getId(JsonObject assetJsonLd) {
        return JsonLdUtils.string(assetJsonLd, Prop.ID);
    }

    protected static String getTitle(JsonObject properties) {
        return JsonLdUtils.string(properties, Prop.Dcterms.TITLE);
    }
}
