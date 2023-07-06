package de.sovity.edc.ext.wrapper.api.usecase.model;

import de.sovity.edc.ext.wrapper.api.common.model.ContractAgreementDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * DTO Class for ContractNegotiationOutput
 *
 * @author Haydar Qarawlus
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContractNegotiationOutputDto {
    private String correlationId;
    private String counterPartyId;
    private String counterPartyAddress;
    private String protocol;
    private ContractAgreementDto contractAgreement;

}
