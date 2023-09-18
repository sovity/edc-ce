package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class AssetCreatorOrganizationNameJsonLd {
    @JsonProperty("@type")
    private String type;

    @JsonProperty("http://xmlns.com/foaf/0.1/name")
    private String name;
}
