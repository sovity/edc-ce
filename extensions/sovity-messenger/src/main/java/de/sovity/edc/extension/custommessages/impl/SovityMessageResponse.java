package de.sovity.edc.extension.custommessages.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.utils.jsonld.vocab.Prop;

import java.net.URL;

public record SovityMessageResponse(
    @JsonProperty(Prop.SovityMessageExt.HEADER)
    String header,

    @JsonProperty(Prop.SovityMessageExt.BODY)
    String body
) {
}
