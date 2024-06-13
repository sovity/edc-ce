package de.sovity.edc.extension.custommessage.receiver.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.custommessages.api.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Answer implements SovityMessage {
    @Override
    public String getType() {
        return "answer";
    }

    @JsonProperty
    private double answer;
}
