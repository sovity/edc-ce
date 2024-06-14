package de.sovity.edc.extension.sovitymessenger.demo.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.messenger.api.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static de.sovity.edc.extension.sovitymessenger.demo.message.Common.ROOT;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Addition implements SovityMessage {

    @Override
    public String getType() {
        return ROOT + "add";
    }

    @JsonProperty
    public int op1;

    @JsonProperty
    public int op2;

}
