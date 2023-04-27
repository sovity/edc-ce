//package de.sovity.edc.ext.wrapper.api.offering.model;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
//import org.eclipse.edc.policy.model.Constraint;
//import org.eclipse.edc.policy.model.MultiplicityConstraint;
//
//import java.util.List;
//
//import static java.util.stream.Collectors.joining;
//
///**
// * A collection of child constraints where all must be satisfied for the constraint to be satisfied.
// */
//@JsonDeserialize(builder = org.eclipse.edc.policy.model.AndConstraint.Builder.class)
//public class AndConstraint extends MultiplicityConstraint {
//
//    private AndConstraint() {
//    }
//
//    @Override
//    public <R> R accept(Visitor<R> visitor) {
//        return visitor.visitAndConstraint(this);
//    }
//
//    @Override
//    public org.eclipse.edc.policy.model.AndConstraint create(List<Constraint> constraints) {
//        return org.eclipse.edc.policy.model.AndConstraint.Builder.newInstance().constraints(constraints).build();
//    }
//
//    @Override
//    public String toString() {
//        return "And constraint: [" + constraints.stream().map(Object::toString).collect(joining(",")) + "]";
//    }
//
//    @JsonPOJOBuilder(withPrefix = "")
//    public static class Builder extends MultiplicityConstraint.Builder<org.eclipse.edc.policy.model.AndConstraint, org.eclipse.edc.policy.model.AndConstraint.Builder> {
//
//        private Builder() {
//            constraint = new org.eclipse.edc.policy.model.AndConstraint();
//        }
//
//        @JsonCreator
//        public static org.eclipse.edc.policy.model.AndConstraint.Builder newInstance() {
//            return new org.eclipse.edc.policy.model.AndConstraint.Builder();
//        }
//
//        public org.eclipse.edc.policy.model.AndConstraint build() {
//            return constraint;
//        }
//    }
//}
