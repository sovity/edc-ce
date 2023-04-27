package de.sovity.edc.ext.wrapper.api.offering.model;

import io.swagger.v3.oas.annotations.media.Schema;

import static java.util.stream.Collectors.joining;

/**
 * Disallows an action if its constraints are satisfied.
 */
@Schema(description = "Test")
public class Prohibition extends Rule {

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitProhibition(this);
    }

    @Override
    public String toString() {
        return "Prohibition constraints: [" + getConstraints().stream().map(Object::toString).collect(joining(",")) + "]";
    }

    /**
     * Returns a copy of this prohibition with the specified target.
     *
     * @param target the target.
     * @return a copy with the specified target.
     */
    public Prohibition withTarget(String target) {
        return Prohibition.Builder.newInstance()
                .uid(this.uid)
                .assigner(this.assigner)
                .assignee(this.assignee)
                .action(this.action)
                .constraints(this.constraints)
                .target(target)
                .build();
    }

    public static class Builder extends Rule.Builder<Prohibition, Prohibition.Builder> {

        private Builder() {
            rule = new Prohibition();
        }

        public static Prohibition.Builder newInstance() {
            return new Prohibition.Builder();
        }

        public Prohibition.Builder uid(String uid) {
            rule.uid = uid;
            return this;
        }

        public Prohibition build() {
            return rule;
        }
    }
}
