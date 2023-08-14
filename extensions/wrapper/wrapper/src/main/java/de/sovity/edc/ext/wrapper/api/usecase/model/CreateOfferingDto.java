package de.sovity.edc.ext.wrapper.api.usecase.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
@Builder(toBuilder = true)
@Schema(description = "Necessary data for creating an offer")
public class CreateOfferingDto {

    private AssetEntryDto assetEntry;
    private PolicyDefinitionRequestDto policyDefinitionRequest;
    private ContractDefinitionRequestDto contractDefinitionRequest;
}
