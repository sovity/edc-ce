package de.sovity.edc.ext.wrapper.api.offering.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A collection of permissions, prohibitions, and obligations. Subtypes are defined by
 * {@link PolicyType}.
 * This is a value object. In order to have it identifiable and individually addressable, consider the use of PolicyDefinition.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Test")
public class Policy {

    private final List<Permission> permissions = new ArrayList<>();
    private final List<Prohibition> prohibitions = new ArrayList<>();
    private final List<Duty> obligations = new ArrayList<>();
    private final Map<String, Object> extensibleProperties = new HashMap<>();
    private String inheritsFrom;
    private String assigner;
    private String assignee;
    private String target;
    @JsonProperty("@type")
    private PolicyType type = PolicyType.SET;


    @Override
    public int hashCode() {
        return Objects.hash(permissions, prohibitions, obligations, extensibleProperties, inheritsFrom, assigner, assignee, target, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Policy policy = (Policy) o;
        return permissions.equals(policy.permissions) && prohibitions.equals(policy.prohibitions) && obligations.equals(policy.obligations) && extensibleProperties.equals(policy.extensibleProperties) &&
                Objects.equals(inheritsFrom, policy.inheritsFrom) && Objects.equals(assigner, policy.assigner) && Objects.equals(assignee, policy.assignee) && Objects.equals(target, policy.target) && type == policy.type;
    }
}
