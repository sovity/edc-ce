package de.sovity.edc.ext.wrapper.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "ODRL constraint as supported by the sovity product landscape")
public class UiPolicyExpression {

    @Schema(description = "Either LITERAL or one of the constraint types.")
    private UiPolicyExpressionType expressionType;

    @Schema(description = "Only for types AND, OR, XOR. List of sub-expressions " +
        "to be evaluated according to the expressionType.")
    private List<UiPolicyExpression> expressions;

    @Schema(description = "Only for type CONSTRAINT. A single constraint.")
    private UiPolicyConstraint constraint;

    public static UiPolicyExpression and(List<UiPolicyExpression> expressions) {
        return UiPolicyExpression.builder()
            .expressionType(UiPolicyExpressionType.AND)
            .expressions(expressions)
            .build();
    }

    public static UiPolicyExpression or(List<UiPolicyExpression> expressions) {
        return UiPolicyExpression.builder()
            .expressionType(UiPolicyExpressionType.OR)
            .expressions(expressions)
            .build();
    }

    public static UiPolicyExpression xor(List<UiPolicyExpression> expressions) {
        return UiPolicyExpression.builder()
            .expressionType(UiPolicyExpressionType.XOR)
            .expressions(expressions)
            .build();
    }

    public static UiPolicyExpression constraint(UiPolicyConstraint constraint) {
        return UiPolicyExpression.builder()
            .expressionType(UiPolicyExpressionType.CONSTRAINT)
            .constraint(constraint)
            .build();
    }
}
