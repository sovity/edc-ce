package de.sovity.edc.ext.wrapper.api.offering.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A leaf constraint containing a left expression, right expression, and operator triple that can be evaluated.
 */
@JsonDeserialize(builder = AtomicConstraint.Builder.class)
@Schema(description = "Test")
public class AtomicConstraint extends Constraint {
    private Expression leftExpression;
    private Expression rightExpression;
    private Operator operator = Operator.EQ;

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitAtomicConstraint(this);
    }

    @Override
    public String toString() {
        return "Constraint " + leftExpression + " " + operator.toString() + " " + rightExpression;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private final AtomicConstraint constraint;

        private Builder() {
            constraint = new AtomicConstraint();
        }

        @JsonCreator
        public static AtomicConstraint.Builder newInstance() {
            return new AtomicConstraint.Builder();
        }

        public AtomicConstraint.Builder leftExpression(Expression expression) {
            constraint.leftExpression = expression;
            return this;
        }

        public AtomicConstraint.Builder rightExpression(Expression expression) {
            constraint.rightExpression = expression;
            return this;
        }

        public AtomicConstraint.Builder operator(Operator operator) {
            constraint.operator = operator;
            return this;
        }

        public AtomicConstraint build() {
            return constraint;
        }
    }

}
