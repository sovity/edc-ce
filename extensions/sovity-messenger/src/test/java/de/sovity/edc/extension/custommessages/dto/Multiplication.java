package de.sovity.edc.extension.custommessages.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.custommessages.api.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Multiplication implements SovityMessage {
    @Override
    public String getType() {
        return "mul";
    }

    @JsonProperty("a")
    private int a;
    @JsonProperty("b")
    private int b;
}
