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
package de.sovity.edc.ce.libs.mappers.asset;

import de.sovity.edc.ce.api.common.model.DataSourceAvailability;
import de.sovity.edc.ce.api.common.model.UiAsset;
import de.sovity.edc.ce.libs.mappers.asset.utils.AssetJsonLdUtils;
import de.sovity.edc.ce.libs.mappers.asset.utils.ShortDescriptionBuilder;
import de.sovity.edc.runtime.simple_di.Service;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import de.sovity.edc.utils.jsonld.vocab.Prop.SovityDcatExt.HttpDatasourceHints;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RequiredArgsConstructor
@Service
public class AssetJsonLdParser {
    private final AssetJsonLdUtils assetJsonLdUtils;
    private final ShortDescriptionBuilder shortDescriptionBuilder;
    private final OwnConnectorEndpointService ownConnectorEndpointService;

    public UiAsset buildUiAsset(JsonObject assetJsonLd, String connectorEndpoint, String participantId) {
        var properties = JsonLdUtils.object(assetJsonLd, Prop.Edc.PROPERTIES);

        var uiAsset = new UiAsset();
        uiAsset.setAssetJsonLd(JsonUtils.toJson(JsonLdUtils.tryCompact(assetJsonLd)));

        var id = assetJsonLdUtils.getId(assetJsonLd);
        var title = assetJsonLdUtils.getTitle(assetJsonLd);

        var distribution = JsonLdUtils.object(properties, Prop.SovityDcatExt.DISTRIBUTION);
        uiAsset.setMediaType(JsonLdUtils.string(distribution, Prop.Dcat.MEDIATYPE));
        uiAsset.setDataSampleUrls(JsonLdUtils.stringList(distribution, Prop.Adms.SAMPLE));
        var rights = JsonLdUtils.object(distribution, Prop.Dcterms.RIGHTS);
        uiAsset.setConditionsForUse(JsonLdUtils.string(rights, Prop.Rdfs.LABEL));
        var mobilityDataStandard = JsonLdUtils.object(distribution, Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD);
        uiAsset.setDataModel(JsonLdUtils.string(mobilityDataStandard, Prop.ID));
        var referenceFiles = JsonLdUtils.object(mobilityDataStandard, Prop.MobilityDcatAp.SCHEMA);
        uiAsset.setReferenceFileUrls(JsonLdUtils.stringList(referenceFiles, Prop.Dcat.DOWNLOAD_URL));
        uiAsset.setReferenceFilesDescription(JsonLdUtils.string(referenceFiles, Prop.Rdfs.LITERAL));


        var temporalCoverage = JsonLdUtils.object(properties, Prop.Dcterms.TEMPORAL);
        uiAsset.setTemporalCoverageFrom(JsonLdUtils.localDate(temporalCoverage, Prop.Dcat.START_DATE));
        uiAsset.setTemporalCoverageToInclusive(JsonLdUtils.localDate(temporalCoverage, Prop.Dcat.END_DATE));

        var description = JsonLdUtils.string(properties, Prop.Dcterms.DESCRIPTION);
        uiAsset.setDataSourceAvailability(getDataSourceAvailability(properties));
        uiAsset.setAssetId(id);
        uiAsset.setConnectorEndpoint(connectorEndpoint);
        uiAsset.setParticipantId(participantId);
        uiAsset.setTitle(title);
        uiAsset.setOnRequestContactEmail(JsonLdUtils.string(properties, Prop.SovityDcatExt.CONTACT_EMAIL));
        uiAsset.setOnRequestContactEmailSubject(
            JsonLdUtils.string(properties, Prop.SovityDcatExt.CONTACT_PREFERRED_EMAIL_SUBJECT)
        );
        uiAsset.setLicenseUrl(JsonLdUtils.string(properties, Prop.Dcterms.LICENSE));
        uiAsset.setDescription(description);
        uiAsset.setDescriptionShortText(shortDescriptionBuilder.buildShortDescription(description));
        uiAsset.setIsOwnConnector(ownConnectorEndpointService.isOwnConnectorEndpoint(connectorEndpoint));
        uiAsset.setLanguage(JsonLdUtils.string(properties, Prop.Dcterms.LANGUAGE));
        uiAsset.setVersion(JsonLdUtils.string(properties, Prop.Dcat.VERSION));
        uiAsset.setLandingPageUrl(JsonLdUtils.string(properties, Prop.Dcat.LANDING_PAGE));
        uiAsset.setSovereignLegalName(JsonLdUtils.string(properties, Prop.Dcterms.RIGHTS_HOLDER));
        uiAsset.setDataUpdateFrequency(JsonLdUtils.string(properties, Prop.Dcterms.ACCRUAL_PERIODICITY));
        uiAsset.setKeywords(JsonLdUtils.stringList(properties, Prop.Dcat.KEYWORDS));

        uiAsset.setHttpDatasourceHintsProxyMethod(JsonLdUtils.bool(properties, HttpDatasourceHints.METHOD));
        uiAsset.setHttpDatasourceHintsProxyPath(JsonLdUtils.bool(properties, HttpDatasourceHints.PATH));
        uiAsset.setHttpDatasourceHintsProxyQueryParams(JsonLdUtils.bool(properties, HttpDatasourceHints.QUERY_PARAMS));
        uiAsset.setHttpDatasourceHintsProxyBody(JsonLdUtils.bool(properties, HttpDatasourceHints.BODY));

        var publisher = JsonLdUtils.object(properties, Prop.Dcterms.PUBLISHER);
        uiAsset.setPublisherHomepage(JsonLdUtils.string(publisher, Prop.Foaf.HOMEPAGE));

        uiAsset.setCustomJsonAsString(JsonLdUtils.string(properties, Prop.SovityDcatExt.CUSTOM_JSON));

        uiAsset.setCreatorOrganizationName(ifBlank(getCreatorOrganizationName(assetJsonLd), participantId));

        // Additional / Remaining Properties
        uiAsset.setCustomJsonLdAsString(getCustomJsonLd(properties));

        // Private Properties
        val privateProperties = getPrivateProperties(assetJsonLd);
        uiAsset.setPrivateCustomJsonAsString(JsonLdUtils.string(privateProperties, Prop.SovityDcatExt.PRIVATE_CUSTOM_JSON));
        uiAsset.setPrivateCustomJsonLdAsString(getPrivateCustomJsonLd(privateProperties));

        return uiAsset;
    }

    private String getCustomJsonLd(JsonObject properties) {
        // TODO: diff nested objects
        val remaining = removeHandledProperties(properties, List.of(
            // Implicitly handled / should be skipped if found
            Prop.ID,
            Prop.TYPE,
            Prop.CONTEXT,
            Prop.Edc.ID,
            Prop.Dcterms.IDENTIFIER,

            // Explicitly handled
            Prop.Dcat.DISTRIBUTION_WILL_BE_OVERWRITTEN_BY_CATALOG,
            Prop.Dcat.KEYWORDS,
            Prop.Dcat.LANDING_PAGE,
            Prop.Dcat.VERSION,
            Prop.Dcterms.CREATOR,
            Prop.Dcterms.DESCRIPTION,
            Prop.Dcterms.LANGUAGE,
            Prop.Dcterms.LICENSE,
            Prop.Dcterms.PUBLISHER,
            Prop.Dcterms.TITLE,
            Prop.Dcterms.TEMPORAL,
            Prop.Dcterms.RIGHTS_HOLDER,
            Prop.Dcterms.ACCRUAL_PERIODICITY,

            HttpDatasourceHints.BODY,
            HttpDatasourceHints.METHOD,
            HttpDatasourceHints.PATH,
            HttpDatasourceHints.QUERY_PARAMS,

            Prop.SovityDcatExt.CUSTOM_JSON,
            Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY,
            Prop.SovityDcatExt.CONTACT_EMAIL,
            Prop.SovityDcatExt.CONTACT_PREFERRED_EMAIL_SUBJECT,
            Prop.SovityDcatExt.DISTRIBUTION
        ));

        // custom properties
        return packAndSerializeJsonLd(remaining);
    }

    private String getPrivateCustomJsonLd(JsonObject privateProperties) {
        var remaining = removeHandledProperties(privateProperties, List.of(
            Prop.SovityDcatExt.PRIVATE_CUSTOM_JSON
        ));
        return packAndSerializeJsonLd(remaining);
    }

    public String getCreatorOrganizationName(JsonObject assetJsonLd) {
        var properties = JsonLdUtils.object(assetJsonLd, Prop.Edc.PROPERTIES);
        var creator = JsonLdUtils.object(properties, Prop.Dcterms.CREATOR);
        return JsonLdUtils.string(creator, Prop.Foaf.NAME);
    }

    private String packAndSerializeJsonLd(JsonObject remaining) {
        val customJsonLd = Json.createObjectBuilder();
        remaining.entrySet().stream()
            .filter(it -> !JsonLdUtils.isEmptyArray(it.getValue()) || !JsonLdUtils.isEmptyObject(it.getValue()))
            .forEach(it -> customJsonLd.add(it.getKey(), it.getValue()));
        val compacted = JsonLdUtils.tryCompact(customJsonLd.build());
        return JsonUtils.toJson(compacted);
    }


    private JsonObject removeHandledProperties(JsonObject properties, List<String> handledProperties) {
        var remaining = Json.createObjectBuilder(JsonLdUtils.tryCompact(properties));
        handledProperties.forEach(remaining::remove);
        return remaining.build();
    }

    private JsonObject getPrivateProperties(JsonObject assetJsonLd) {
        if (assetJsonLd.containsKey(Prop.Edc.PRIVATE_PROPERTIES)) {
            return JsonLdUtils.object(assetJsonLd, Prop.Edc.PRIVATE_PROPERTIES);
        } else if (assetJsonLd.containsKey("privateProperties")) {
            // Tests claim this path exists
            return JsonLdUtils.object(assetJsonLd, "privateProperties");
        } else {
            return JsonValue.EMPTY_JSON_OBJECT;
        }
    }

    private DataSourceAvailability getDataSourceAvailability(JsonObject properties) {
        var typeValue = JsonLdUtils.string(properties, Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY);
        if (Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY_ON_REQUEST.equalsIgnoreCase(typeValue)) {
            return DataSourceAvailability.ON_REQUEST;
        }

        return DataSourceAvailability.LIVE;
    }

    private String ifBlank(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }
}
