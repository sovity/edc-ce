package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.sovity.edc.ext.wrapper.api.common.model.utils.CustomDeserializer;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetPropertyJsonLd {

    @JsonProperty(Prop.Edc.ID)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String assetId;

    @JsonProperty(Prop.Dcterms.NAME)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String name;

    @JsonProperty(Prop.Dcterms.LANGUAGE)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String language;

    @JsonProperty(Prop.Dcterms.DESCRIPTION)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String description;

    @JsonProperty(Prop.Dcterms.CREATOR)
    private AssetCreatorOrganizationNameJsonLd creator;

    @JsonProperty(Prop.Dcterms.PUBLISHER)
    private AssetPublisherJsonLd publisher;

    @JsonProperty(Prop.Dcterms.LICENSE)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String license;

    @JsonProperty(Prop.Dcat.VERSION)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String version;

    @JsonProperty(Prop.Dcat.KEYWORDS)
    @JsonDeserialize(using = CustomDeserializer.class)
    private List<String> keywords;

    @JsonProperty(Prop.Dcat.MEDIATYPE)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String mediaType;

    @JsonProperty(Prop.Dcat.LANDING_PAGE)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String landingPage;

    @JsonProperty(Prop.SovityDcatExt.METHOD)
    @JsonDeserialize(using = CustomDeserializer.class)
    private Boolean httpDatasourceHintsProxyMethod;

    @JsonProperty(Prop.SovityDcatExt.PATH)
    @JsonDeserialize(using = CustomDeserializer.class)
    private Boolean httpDatasourceHintsProxyPath;

    @JsonProperty(Prop.SovityDcatExt.QUERY_PARAMS)
    @JsonDeserialize(using = CustomDeserializer.class)
    private Boolean httpDatasourceHintsProxyQueryParams;

    @JsonProperty(Prop.SovityDcatExt.BODY)
    @JsonDeserialize(using = CustomDeserializer.class)
    private Boolean httpDatasourceHintsProxyBody;

    @JsonProperty(Prop.Mds.DATA_CATEGORY)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String dataCategory;

    @JsonProperty(Prop.Mds.DATA_SUBCATEGORY)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String dataSubcategory;

    @JsonProperty(Prop.Mds.DATA_MODEL)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String dataModel;

    @JsonProperty(Prop.Mds.GEO_REFERENCE_METHOD)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String geoReferenceMethod;

    @JsonProperty(Prop.Mds.TRANSPORT_MODE)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String transportMode;
}

