package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Sum type enum.
 */
@Schema(description = """
        Expression types:
        * `ATOMIC_CONSTRAINT` - A single constraint for the policy
        * `AND` - Several constraints, all of which must be respected
        * `OR` - Several constraints, of which at least one must be respected
        * `XOR` - Several constraints, of which exactly one must be respected
        """, enumAsRef = true)
public enum ExpressionType {
    ATOMIC_CONSTRAINT, AND, OR, XOR
}
