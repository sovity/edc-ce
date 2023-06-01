package de.sovity.edc.ext.wrapper.api.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Getter;

@Getter
public class ExpressionDto {

    private Type type;
    private ConstraintDto constraint;
    private List<ExpressionDto> and;
    private List<ExpressionDto> or;
    private List<ExpressionDto> xor;

    public ExpressionDto(@JsonProperty("constraint") ConstraintDto constraint,
                         @JsonProperty("and") List<ExpressionDto> and,
                         @JsonProperty("or") List<ExpressionDto> or,
                         @JsonProperty("xor") List<ExpressionDto> xor) {
        if (constraint != null) {
            this.type = Type.ATOMIC;
            this.constraint = constraint;
        } else if (and != null) {
            this.type = Type.AND;
            this.and = and;
        } else if (or != null) {
            this.type = Type.OR;
            this.or = or;
        } else if (xor != null) {
            this.type = Type.XOR;
            this.xor = xor;
        } else {
            this.type = Type.EMPTY;
        }
    }

    public enum Type {
        EMPTY,
        // Single constraint
        ATOMIC,
        // Several constraints, all of which must be respected
        AND,
        // Several constraints, of which at least one must be respected
        OR,
        // Several constraints, of which exactly one must be respected
        XOR
    }
}
