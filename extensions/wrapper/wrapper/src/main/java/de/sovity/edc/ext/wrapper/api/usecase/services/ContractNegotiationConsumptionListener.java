package de.sovity.edc.ext.wrapper.api.usecase.services;

import lombok.AllArgsConstructor;
import org.eclipse.edc.connector.contract.spi.negotiation.observe.ContractNegotiationListener;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;

@AllArgsConstructor
public class ContractNegotiationConsumptionListener implements ContractNegotiationListener {

    private ConsumptionService consumptionService;

    @Override
    public void finalized(ContractNegotiation contractNegotiation) {
        consumptionService.negotiationConfirmed(contractNegotiation);
    }

}
