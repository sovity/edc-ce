package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Expression constraints for policies.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionDto {

    @Schema(description = """
            Expression types:
            * `EMPTY` - No constraints for the policy
            * `ATOMIC_CONSTRAINT` - A single constraint for the policy
            * `AND` - Several constraints, all of which must be respected
            * `OR` - Several constraints, of which at least one must be respected
            * `XOR` - Several constraints, of which exactly one must be respected
            """
    )
    private Type type;
    private AtomicConstraintDto atomicConstraint;
    private List<ExpressionDto> and;
    private List<ExpressionDto> or;
    private List<ExpressionDto> xor;

    /**
     * Sum type enum.
     */
    public enum Type {
        EMPTY, ATOMIC_CONSTRAINT, AND, OR, XOR
    }
}
