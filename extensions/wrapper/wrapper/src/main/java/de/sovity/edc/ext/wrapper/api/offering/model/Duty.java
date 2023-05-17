package de.sovity.edc.ext.wrapper.api.offering.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.Nullable;

import static java.util.stream.Collectors.joining;

/**
 * An obligation that must be performed if all its constraints are satisfied.
 * TODO: Do we need to support deserializing the parent permission setting?
 */
@JsonDeserialize(builder = Duty.Builder.class)
@JsonTypeName("dataspaceconnector:duty")
@Schema(description = "Test")
public class Duty extends Rule {

    private Permission parentPermission;

    @Nullable
    private Duty consequence;

    public Duty getConsequence() {
        return consequence;
    }

    /**
     * If this duty is part of a permission, returns the parent permission; otherwise returns null.
     */
    @Nullable
    public Permission getParentPermission() {
        return parentPermission;
    }

    void setParentPermission(Permission permission) {
        parentPermission = permission;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitDuty(this);
    }

    @Override
    public String toString() {
        return "Duty constraint: [" + getConstraints().stream().map(Object::toString).collect(joining(",")) + "]";
    }

    /**
     * Returns a copy of this duty with the specified target.
     *
     * @param target the target.
     * @return a copy with the specified target.
     */
    public Duty withTarget(String target) {
        return Duty.Builder.newInstance()
                .uid(this.uid)
                .assigner(this.assigner)
                .assignee(this.assignee)
                .action(this.action)
                .constraints(this.constraints)
                .parentPermission(this.parentPermission)
                .consequence(this.consequence == null ? null : this.consequence.withTarget(target))
                .target(target)
                .build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends Rule.Builder<Duty, Duty.Builder> {

        private Builder() {
            rule = new Duty();
        }

        @JsonCreator
        public static Duty.Builder newInstance() {
            return new Duty.Builder();
        }

        public Duty.Builder uid(String uid) {
            rule.uid = uid;
            return this;
        }

        public Duty.Builder parentPermission(Permission parentPermission) {
            rule.parentPermission = parentPermission;
            return this;
        }

        public Duty.Builder consequence(Duty consequence) {
            rule.consequence = consequence;
            return this;
        }

        public Duty build() {
            return rule;
        }
    }

}
