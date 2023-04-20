package de.sovity.edc.ext.wrapper.api.offering.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.api.management.asset.model.AssetEntryDto;
import org.eclipse.edc.connector.api.management.contractdefinition.model.ContractDefinitionRequestDto;
import org.eclipse.edc.connector.api.management.policy.model.PolicyDefinitionRequestDto;

@RequiredArgsConstructor
@Getter
public class CreateOfferingDto {

    private final AssetEntryDto assetEntryDto;
    private final PolicyDefinitionRequestDto policyDefinitionRequestDto;
    private final ContractDefinitionRequestDto contractDefinitionRequestDto;
}
