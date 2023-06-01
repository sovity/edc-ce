package de.sovity.edc.ext.wrapper.api.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;

/**
 * Expression constraints for policies.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Getter
public class ExpressionDto {

    @Schema(description = """
            Expression types:
            * `EMPTY` - No constraints for the policy
            * `ATOMIC` - A single constraint for the policy
            * `AND` - Several constraints, all of which must be respected
            * `OR` - Several constraints, of which at least one must be respected
            * `XOR` - Several constraints, of which exactly one must be respected
            """
    )
    private final Type type;
    private ConstraintDto constraint;
    private List<ExpressionDto> and;
    private List<ExpressionDto> or;
    private List<ExpressionDto> xor;

    /**
     * Sum type constructor. First non-null parameter is set as type.
     *
     * @param constraint Case ATOMIC
     * @param and        Case AND
     * @param or         Case OR
     * @param xor        Case XOR
     */
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

    /**
     * Sum type enum.
     */
    public enum Type {
        EMPTY, ATOMIC, AND, OR, XOR
    }
}
