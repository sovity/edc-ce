package de.sovity.edc.extension.sovitymessenger.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.custommessages.api.SovityMessage;

public class Answer implements SovityMessage {

    public Answer() {
    }

    public Answer(double answer) {
        this.answer = answer;
    }

    @Override
    public String getType() {
        return "answer";
    }

    @JsonProperty
    private double answer;

    public double getAnswer() {
        return answer;
    }
}
