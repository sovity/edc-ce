package de.sovity.edc.extension.sovitymessenger.demo.message;


import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.messenger.api.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static de.sovity.edc.extension.sovitymessenger.demo.message.Common.ROOT;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Sqrt implements SovityMessage {

    private static final String TYPE = ROOT + "sqrt";

    @Override
    public String getType() {
        return TYPE;
    }

    @JsonProperty("value")
    private double value;

}
