package de.sovity.edc.ext.wrapper.api.offering.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.eclipse.edc.connector.api.management.asset.model.AssetEntryDto;
import org.eclipse.edc.connector.api.management.contractdefinition.model.ContractDefinitionRequestDto;

@Getter
@Schema(description = "Test")
public class CreateOfferingDto {

    private final AssetEntryDto assetEntryDto;
    private final PolicyDefinitionRequestDto policyDefinitionRequestDto;
    private final ContractDefinitionRequestDto contractDefinitionRequestDto;

    @JsonCreator
    public CreateOfferingDto(
            @JsonProperty(value = "assetEntryDto", required = true) AssetEntryDto assetEntryDto,
            @JsonProperty(value = "policyDefinitionRequestDto", required = true) PolicyDefinitionRequestDto policyDefinitionRequestDto,
            @JsonProperty(value = "contractDefinitionRequestDto", required = true) ContractDefinitionRequestDto contractDefinitionRequestDto) {
        this.assetEntryDto = assetEntryDto;
        this.policyDefinitionRequestDto = policyDefinitionRequestDto;
        this.contractDefinitionRequestDto = contractDefinitionRequestDto;
    }

}
