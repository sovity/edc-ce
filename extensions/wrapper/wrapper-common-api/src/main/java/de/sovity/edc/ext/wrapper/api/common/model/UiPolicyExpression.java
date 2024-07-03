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
@Schema(description =
    "Represents a single Ui Policy Literal or a List of Ui Policy Expressions. The Literal" +
    " will be evaluated if the expressionType is LITERAL.")
public class UiPolicyExpression {

    @Schema(description = "Either LITERAL or one of the constraint types.")
    private UiPolicyExpressionType expressionType;

    @Schema(description =
        "List of policy elements that are evaluated according the expressionType.")
    private List<UiPolicyExpression> expressions;

    @Schema(description =
        "A single literal. Will be evaluated if the expressionType is set to " +
        "LITERAL.")
    private UiPolicyConstraint constraint;

}
