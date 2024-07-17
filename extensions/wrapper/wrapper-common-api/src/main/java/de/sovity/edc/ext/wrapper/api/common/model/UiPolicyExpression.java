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

    @Schema(description = "Expression type")
    private UiPolicyExpressionType type;

    @Schema(description = "Only for types AND, OR, XOR. List of sub-expressions " +
        "to be evaluated according to the expressionType.")
    private List<UiPolicyExpression> expressions;

    @Schema(description = "Only for type CONSTRAINT. A single constraint.")
    private UiPolicyConstraint constraint;

    public static UiPolicyExpression empty() {
        return UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.EMPTY)
            .build();
    }

    public static UiPolicyExpression constraint(UiPolicyConstraint constraint) {
        return UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.CONSTRAINT)
            .constraint(constraint)
            .build();
    }

    public static UiPolicyExpression and(List<UiPolicyExpression> expressions) {
        return UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.AND)
            .expressions(expressions)
            .build();
    }

    public static UiPolicyExpression or(List<UiPolicyExpression> expressions) {
        return UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.OR)
            .expressions(expressions)
            .build();
    }

    public static UiPolicyExpression xone(List<UiPolicyExpression> expressions) {
        return UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.XONE)
            .expressions(expressions)
            .build();
    }
}
