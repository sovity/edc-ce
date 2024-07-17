package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
    Ui Policy Expression types:
    * `CONSTRAINT` - Expression 'a=b'
    * `AND` - Conjunction of several expressions. All child expressions must be true.
    * `OR` - Disjunction of several expressions. (At least) one child expression must be true.
    * `XONE` - XOR operation, exactly one child expression must be true.
    """, enumAsRef = true)
public enum UiPolicyExpressionType {
    EMPTY,
    CONSTRAINT,
    AND,
    OR,
    XONE
}


