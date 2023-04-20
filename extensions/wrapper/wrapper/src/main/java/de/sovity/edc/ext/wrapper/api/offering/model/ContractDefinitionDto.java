package de.sovity.edc.ext.wrapper.api.offering.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.eclipse.edc.api.model.CriterionDto;

/**
 * Dto for contract definition. A contract definition serves as a template for contract offers.
 * One offer containing the contract policy is created for each of the assets.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "status-defining Contract-Definitions")
public class ContractDefinitionDto {

    /** ID of the policy that defines which participants may see an offer. */
    @Schema(description = "EDC Contract-Definition access policy")
    private String accessPolicyId;

    /** ID of the policy that defines the usage conditions. */
    @Schema(description = "EDC Contract-Definition contract policy")
    private String contractPolicyId;

    /** Criteria defining which assets this contract definition may apply to. */
    @Schema(description = "EDC Contract-Definition asset criteria")
    private List<CriterionDto> assetCriteria;
}
