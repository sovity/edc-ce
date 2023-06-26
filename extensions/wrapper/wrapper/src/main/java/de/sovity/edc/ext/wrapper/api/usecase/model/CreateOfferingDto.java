package de.sovity.edc.ext.wrapper.api.usecase.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.edc.connector.api.management.asset.model.AssetEntryDto;
import org.eclipse.edc.connector.api.management.contractdefinition.model.ContractDefinitionRequestDto;

/**
 * DTO containing all data necessary for creating an offer.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Necessary data for creating an offer")
public class CreateOfferingDto {
    private AssetEntryDto assetEntry;
    private PolicyDefinitionRequestDto policyDefinitionRequest;
    private ContractDefinitionRequestDto contractDefinitionRequest;
}
