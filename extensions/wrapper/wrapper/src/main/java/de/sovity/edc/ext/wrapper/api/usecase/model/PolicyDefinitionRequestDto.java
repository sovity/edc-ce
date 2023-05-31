package de.sovity.edc.ext.wrapper.api.usecase.model;

import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO containing the minimal data necessary for a policy definition.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Data for creating a policy definition request")
public class PolicyDefinitionRequestDto {
    @Schema(description = "ID chosen by the user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;
    @Schema(description = "Data for the policy", requiredMode = Schema.RequiredMode.REQUIRED)
    private PolicyDto policy;
}
