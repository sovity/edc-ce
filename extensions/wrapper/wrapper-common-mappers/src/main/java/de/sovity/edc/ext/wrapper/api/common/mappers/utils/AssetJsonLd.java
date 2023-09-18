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


@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetJsonLd {

    @JsonProperty(Prop.ID)
    @JsonDeserialize(using = CustomDeserializer.class)
    private String assetId;

    @JsonProperty(Prop.PROPERTIES)
    @JsonDeserialize(using = CustomDeserializer.class)
    private AssetPropertyJsonLd properties;
}

