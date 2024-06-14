package de.sovity.edc.extension.custommessages.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.eclipse.edc.protocol.dsp.spi.types.HttpMessageProtocol;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;

import java.net.URL;

public record SovityMessageRequest(
    @JsonIgnore
    URL counterPartyAddress,

    @JsonProperty(Prop.SovityMessageExt.HEADER)
    String header,

    @JsonProperty(Prop.SovityMessageExt.BODY)
    String body
) implements RemoteMessage {

    @JsonIgnore
    @Override
    public String getProtocol() {
        return HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP;
    }

    @JsonIgnore
    @Override
    public String getCounterPartyAddress() {
        return counterPartyAddress.toString();
    }
}
