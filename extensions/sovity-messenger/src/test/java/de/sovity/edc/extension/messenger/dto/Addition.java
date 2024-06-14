package de.sovity.edc.extension.messenger.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.messenger.api.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Addition implements SovityMessage {
    @Override
    public String getType() {
        return "add";
    }

    @JsonProperty
    private int op1;
    @JsonProperty
    private int op2;
}
