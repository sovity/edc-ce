package de.sovity.edc.extension.messenger.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.utils.jsonld.vocab.Prop;

public record SovityMessageResponse(
    @JsonProperty(Prop.SovityMessageExt.HEADER)
    String header,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(Prop.SovityMessageExt.BODY)
    String body
) {
}
