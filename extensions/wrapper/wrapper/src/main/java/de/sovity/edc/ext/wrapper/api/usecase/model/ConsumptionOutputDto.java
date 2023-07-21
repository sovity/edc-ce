package de.sovity.edc.ext.wrapper.api.usecase.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO holding information about a consumption process.
 *
 * @author Steffen Biehs, Ronja Quensel
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ConsumptionOutputDto {
    /** ID of the process. */
    private String id;

    /** The input that was used to start this process. */
    private ConsumptionInputDto input;

    /** Collection of errors that may have occurred during this process. */
    private List<String> errors;

    /** Information about the contract negotiation associated with this process. */
    private ContractNegotiationOutputDto contractNegotiation;

    /** Information about the transfer process associated with this process. */
    private TransferProcessOutputDto transferProcess;
}
