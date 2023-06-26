package de.sovity.edc.ext.wrapper.api.usecase.model;

import de.sovity.edc.ext.wrapper.api.common.model.CriterionDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ContractDefinitionRequestDto {

    /**
     * Default validity is set to one year.
     */
    private static final long DEFAULT_VALIDITY = TimeUnit.DAYS.toSeconds(365);

    private String id;
    @NotNull(message = "accessPolicyId cannot be null")
    private String accessPolicyId;
    @NotNull(message = "contractPolicyId cannot be null")
    private String contractPolicyId;
    @Valid
    @NotNull(message = "criteria cannot be null")
    private List<CriterionDto> assetsSelector;

}
