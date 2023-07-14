package de.sovity.edc.ext.wrapper.api.common.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A DTO Class for ContractAgreement
 *
 * @author Haydar Qarawlus
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContractAgreementDto {

    private String id;
    private String providerId;
    private String consumerId;
    private long contractSigningDate;
    private String assetId;
    private PolicyDto policy;
}
