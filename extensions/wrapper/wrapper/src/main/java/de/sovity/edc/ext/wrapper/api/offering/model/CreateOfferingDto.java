package de.sovity.edc.ext.wrapper.api.offering.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.eclipse.edc.connector.api.management.asset.model.AssetEntryDto;
import org.eclipse.edc.connector.api.management.contractdefinition.model.ContractDefinitionRequestDto;

/**
 * DTO containing all data necessary for creating an offer.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Getter
@Schema(description = "Test")
public class CreateOfferingDto {

    private final AssetEntryDto assetEntryDto;
    private final PolicyDefinitionRequestDto policyDefinitionRequestDto;
    private final ContractDefinitionRequestDto contractDefinitionRequestDto;

    /**
     * JSON Parser for the DTO.
     *
     * @param assetEntryDto                JSON object containing data for an asset Entry.
     * @param policyDefinitionRequestDto   JSON object containing data for a policy definition
     *                                     request.
     * @param contractDefinitionRequestDto JSON object containing data for a contract definition
     *                                     request.
     */
    @JsonCreator
    public CreateOfferingDto(
            @JsonProperty(value = "assetEntryDto", required = true) AssetEntryDto assetEntryDto,
            @JsonProperty(value = "policyDefinitionRequestDto", required = true)
            PolicyDefinitionRequestDto policyDefinitionRequestDto,
            @JsonProperty(value = "contractDefinitionRequestDto", required = true)
            ContractDefinitionRequestDto contractDefinitionRequestDto) {
        this.assetEntryDto = assetEntryDto;
        this.policyDefinitionRequestDto = policyDefinitionRequestDto;
        this.contractDefinitionRequestDto = contractDefinitionRequestDto;
    }

}
