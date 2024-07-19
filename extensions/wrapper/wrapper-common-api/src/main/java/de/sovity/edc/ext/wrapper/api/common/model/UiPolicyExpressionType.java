package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
    Ui Policy Expression types:
    * `CONSTRAINT` - Expression 'a=b'
    * `AND` - Conjunction of several expressions. Evaluates to true iff all child expressions are true.
    * `OR` - Disjunction of several expressions. Evaluates to true iff at least one child expression is true.
    * `XONE` - XONE operation. Evaluates to true iff exactly one child expression is true.
    """, enumAsRef = true)
public enum UiPolicyExpressionType {
    EMPTY,
    CONSTRAINT,
    AND,
    OR,
    XONE
}


