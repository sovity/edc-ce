package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Sum type enum.
 */
@Schema(enumAsRef = true)
public enum ExpressionType {
    EMPTY, ATOMIC_CONSTRAINT, AND, OR, XOR
}
