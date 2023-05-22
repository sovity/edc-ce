package de.sovity.edc.ext.wrapper.api.common.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Possible types of policies. Copied from EDC policy-model.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(description = "Currently supported is type: SET")
public enum PolicyTypeDto {
    SET("set"), OFFER("offer"), CONTRACT("contract");

    @JsonProperty("@policytype")
    private final String type;

    PolicyTypeDto(@JsonProperty("@policytype") String type) {
        this.type = type;
    }

    /**
     * JSON creator for policy type. For details see EDC policy model.
     *
     * @param object Policy type.
     * @return DTO of policy type.
     */
    @JsonCreator
    public static PolicyTypeDto fromObject(Map<String, Object> object) {
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
