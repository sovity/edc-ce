package de.sovity.edc.ext.wrapper.api.usecase.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;

import java.util.List;

@AllArgsConstructor
@Getter
public class ConsumeOutputDto {
    String id;
    ConsumeInputDto input;
    List<String> errors;
    ContractNegotiation contractNegotiation; //TODO dto
    TransferProcess transferProcess; //TODO dto
}
