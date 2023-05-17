package de.sovity.edc.ext.wrapper.api.offering.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of child constraints. Subclasses define the semantics for when this constraint is satisfied.
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "edctype")
@Schema(description = "Test")
public abstract class MultiplicityConstraint extends Constraint {
    protected List<Constraint> constraints = new ArrayList<>();

    public List<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * Creates another instance of the constraint with the given child constraints.
     */
    public abstract MultiplicityConstraint create(List<Constraint> constraints);

    protected abstract static class Builder<T extends MultiplicityConstraint, B extends MultiplicityConstraint.Builder<T, B>> {
        protected T constraint;

        public B constraint(Constraint constraint) {
            this.constraint.constraints.add(constraint);
            return (B) this;
        }

        public B constraints(List<Constraint> constraints) {
            constraint.constraints.addAll(constraints);
            return (B) this;
        }

    }

}
