package de.sovity.edc.extension.custommessage.receiver.message;


import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.custommessages.api.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Sqrt implements SovityMessage {

    @Override
    public String getType() {
        return "sqrt";
    }

    @JsonProperty("value")
    private double a;
}
