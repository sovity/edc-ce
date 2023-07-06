package de.sovity.edc.ext.wrapper.api.usecase.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.api.management.transferprocess.model.TransferProcessDto;

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
    String id;
    ConsumeInputDto input;
    List<String> errors;
    ContractNegotiationOutputDto contractNegotiation;
    TransferProcessDto transferProcess;
}
