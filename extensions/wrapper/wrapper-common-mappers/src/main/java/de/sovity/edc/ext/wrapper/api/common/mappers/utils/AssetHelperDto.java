package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.sovity.edc.ext.wrapper.api.common.model.utils.CustomDeserializer;
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
public class AssetHelperDto {

    @JsonProperty("http://purl.org/dc/terms/identifier")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String identifier;

    @JsonProperty("http://purl.org/dc/terms/title")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String title;

    @JsonProperty("http://purl.org/dc/terms/language")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String language;

    @JsonProperty("http://purl.org/dc/terms/description")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String description;

    @JsonProperty("http://purl.org/dc/terms/creator")
    private AssetCreatorOrganizationNameJsonLd creator;

    @JsonProperty("http://purl.org/dc/terms/publisher")
    private AssetPublisherJsonLd publisher;

    @JsonProperty("http://purl.org/dc/terms/license")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String license;

    @JsonProperty("http://www.w3.org/ns/dcat#version")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String version;

    @JsonProperty("http://www.w3.org/ns/dcat#keyword")
    @JsonDeserialize(using = CustomDeserializer.class)
    private List<String> keywords;

    @JsonProperty("http://www.w3.org/ns/dcat#distribution")
    private AssetDistributionJsonLd distribution;

    @JsonProperty("http://www.w3.org/ns/dcat#landingPage")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String landingPage;

    @JsonProperty("https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyMethod")
    @JsonDeserialize(using = CustomDeserializer.class)
    private Boolean httpDatasourceHintsProxyMethod;

    @JsonProperty("https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyPath")
    @JsonDeserialize(using = CustomDeserializer.class)
    private Boolean httpDatasourceHintsProxyPath;

    @JsonProperty("https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyQueryParams")
    @JsonDeserialize(using = CustomDeserializer.class)
    private Boolean httpDatasourceHintsProxyQueryParams;

    @JsonProperty("https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyBody")
    @JsonDeserialize(using = CustomDeserializer.class)
    private Boolean httpDatasourceHintsProxyBody;

    @JsonProperty("http://w3id.org/mds#dataCategory")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String dataCategory;

    @JsonProperty("http://w3id.org/mds#dataSubcategory")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String dataSubcategory;

    @JsonProperty("http://w3id.org/mds#dataModel")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String dataModel;

    @JsonProperty("http://w3id.org/mds#geoReferenceMethod")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String geoReferenceMethod;

    @JsonProperty("http://w3id.org/mds#transportMode")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String transportMode;
}

