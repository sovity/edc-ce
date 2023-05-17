package de.sovity.edc.ext.wrapper.api.offering.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An expression that can be evaluated.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "edctype")
//@Schema(description = "Test")
public abstract class Expression {

    public interface Visitor<R> {

        R visitLiteralExpression(LiteralExpression expression);

    }

    public abstract <R> R accept(Expression.Visitor<R> visitor);

}
