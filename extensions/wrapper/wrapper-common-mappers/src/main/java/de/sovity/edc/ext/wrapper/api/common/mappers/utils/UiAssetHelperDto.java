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
public class UiAssetHelperDto {

    @JsonProperty("https://w3id.org/edc/v0.0.1/ns/")
    private String ns;

    @JsonProperty("http://purl.org/dc/terms/identifier")
    private String identifier;

    @JsonProperty("http://purl.org/dc/terms/title")
    @JsonDeserialize(using = CustomDeserializer.class)
    private String title;

    @JsonProperty("http://purl.org/dc/terms/language")
    private String language;

    @JsonProperty("http://purl.org/dc/terms/description")
    private String description;

    @JsonProperty("http://purl.org/dc/terms/creator")
    private UiAssetCreator creator;

    @JsonProperty("http://purl.org/dc/terms/publisher")
    private UiAssetPublisher publisher;

    @JsonProperty("http://purl.org/dc/terms/license")
    private String license;

    @JsonProperty("http://www.w3.org/ns/dcat#version")
    private String version;

    @JsonProperty("http://www.w3.org/ns/dcat#keyword")
    @JsonDeserialize(using = CustomDeserializer.class)
    private List<String> keywords;

    @JsonProperty("http://www.w3.org/ns/dcat#distribution")
    private UiAssetDistribution distribution;

    @JsonProperty("http://www.w3.org/ns/dcat#landingPage")
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
    private String dataCategory;

    @JsonProperty("http://w3id.org/mds#dataSubcategory")
    private String dataSubcategory;

    @JsonProperty("http://w3id.org/mds#dataModel")
    private String dataModel;

    @JsonProperty("http://w3id.org/mds#geoReferenceMethod")
    private String geoReferenceMethod;

    @JsonProperty("http://w3id.org/mds#transportMode")
    private String transportMode;
}

