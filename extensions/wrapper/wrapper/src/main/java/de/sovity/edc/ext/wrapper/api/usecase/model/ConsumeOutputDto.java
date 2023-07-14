package de.sovity.edc.ext.wrapper.api.usecase.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO Class for ConsumerOutput
 *
 * @author Steffen Biehs
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ConsumeOutputDto {
    private String id;
    private ConsumeInputDto input;
    private List<String> errors;
    private ContractNegotiationOutputDto contractNegotiation;
    private TransferProcessOutputDto transferProcess;
}
