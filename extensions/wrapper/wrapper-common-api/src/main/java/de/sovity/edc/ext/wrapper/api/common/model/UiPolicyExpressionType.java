package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Ui Policy Expression types:
        * `AND` - Several constraints, all of which must be respected
        * `OR` - Several constraints, of which at least one must be respected
        * `XOR` - Several constraints, of which exactly one must be respected
        * `CONSTRAINT` - A single constraint for the policy
        """, enumAsRef = true)
public enum UiPolicyExpressionType {
    AND, OR, XOR, CONSTRAINT
}


