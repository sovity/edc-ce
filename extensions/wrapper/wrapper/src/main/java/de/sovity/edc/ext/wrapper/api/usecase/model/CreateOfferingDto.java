package de.sovity.edc.ext.wrapper.api.usecase.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.eclipse.edc.connector.api.management.asset.model.AssetEntryDto;
import org.eclipse.edc.connector.api.management.contractdefinition.model.ContractDefinitionRequestDto;

/**
 * DTO containing all data necessary for creating an offer.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Getter
public class CreateOfferingDto {

    private AssetEntryDto assetEntry;
    private PolicyDefinitionRequestDto policyDefinitionRequest;
    private ContractDefinitionRequestDto contractDefinitionRequest;

    /**
     * JSON Parser for the DTO.
     *
     * @param assetEntry                JSON object containing data for an asset Entry.
     * @param policyDefinitionRequest   JSON object containing data for a policy definition
     *                                     request.
     * @param contractDefinitionRequest JSON object containing data for a contract definition
     *                                     request.
     */
    @JsonCreator
    public CreateOfferingDto(
            @JsonProperty(value = "assetEntry", required = true) AssetEntryDto assetEntry,
            @JsonProperty(value = "policyDefinitionRequest", required = true)
            PolicyDefinitionRequestDto policyDefinitionRequest,
            @JsonProperty(value = "contractDefinitionRequest", required = true)
            ContractDefinitionRequestDto contractDefinitionRequest) {
        this.assetEntry = assetEntry;
        this.policyDefinitionRequest = policyDefinitionRequest;
        this.contractDefinitionRequest = contractDefinitionRequest;
    }

}
