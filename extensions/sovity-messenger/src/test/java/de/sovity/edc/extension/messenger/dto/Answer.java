package de.sovity.edc.extension.messenger.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.messenger.api.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Answer implements SovityMessage {
    @Override
    public String getType() {
        return getClass().getCanonicalName();
    }

    @JsonProperty("answer")
    private int answer;
}
