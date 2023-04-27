package de.sovity.edc.ext.wrapper.api.offering.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

/**
 * A literal value used as an expression.
 */
@JsonTypeName("dataspaceconnector:literalexpression")
@Schema(description = "Test")
public class LiteralExpression extends Expression {
    private final Object value;

    public LiteralExpression(@JsonProperty("value") Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value + "'";
    }

    @NotNull
    public String asString() {
        return value == null ? "null" : value.toString();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitLiteralExpression(this);
    }


    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LiteralExpression) {
            return ((LiteralExpression) obj).value.equals(value);
        }

        return false;
    }
}
