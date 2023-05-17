//package de.sovity.edc.ext.wrapper.api.offering.model;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonTypeName;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
//
//import java.util.List;
//
//import static java.util.stream.Collectors.joining;
//
///**
// * A collection of child constraints where exactly one must be satisfied for the constraint to be satisfied.
// */
//@JsonDeserialize(builder = XoneConstraint.Builder.class)
//@JsonTypeName("dataspaceconnector:xone")
//public class XoneConstraint extends MultiplicityConstraint {
//
//    @Override
//    public List<Constraint> getConstraints() {
//        return constraints;
//    }
//
//    @Override
//    public <R> R accept(Visitor<R> visitor) {
//        return visitor.visitXoneConstraint(this);
//    }
//
//    @Override
//    public XoneConstraint create(List<Constraint> constraints) {
//        return XoneConstraint.Builder.newInstance().constraints(constraints).build();
//    }
//
//    @Override
//    public String toString() {
//        return "Xone constraint: [" + constraints.stream().map(Object::toString).collect(joining(",")) + "]";
//    }
//
//    @JsonPOJOBuilder(withPrefix = "")
//    public static class Builder extends MultiplicityConstraint.Builder<XoneConstraint, XoneConstraint.Builder> {
//
//        @JsonCreator
//        public static XoneConstraint.Builder newInstance() {
//            return new XoneConstraint.Builder();
//        }
//
//        public XoneConstraint build() {
//            return constraint;
//        }
//
//        private Builder() {
//            constraint = new XoneConstraint();
//        }
//    }
//
//}
