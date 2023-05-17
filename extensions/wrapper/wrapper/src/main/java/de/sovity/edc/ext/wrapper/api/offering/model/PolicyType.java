package de.sovity.edc.ext.wrapper.api.offering.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * The types of {@link Policy}.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(description = "Test")
public enum PolicyType {
    SET("set"), OFFER("offer"), CONTRACT("contract");

    @JsonProperty("@policytype")
    private String type;

    PolicyType(@JsonProperty("@policytype") String type) {
        this.type = type;
    }

    @JsonCreator
    public static PolicyType fromObject(Map<String, Object> object) {
        if (SET.type.equals(object.get("@policytype"))) {
            return SET;
        } else if (OFFER.type.equals(object.get("@policytype"))) {
            return OFFER;
        } else if (CONTRACT.type.equals(object.get("@policytype"))) {
            return CONTRACT;
        }
        throw new IllegalArgumentException("Invalid policy type");
    }

    public String getType() {
        return type;
    }
}
