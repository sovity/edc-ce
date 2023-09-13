package de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations;


import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractNegotiationRequest;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;

;


@RequiredArgsConstructor
public class ContractOfferMapper {
    private final PolicyMapper policyMapper;

    public ContractOffer buildContractOffer(ContractNegotiationRequest contractRequest) {
        return ContractOffer.Builder.newInstance()
                .id(contractRequest.getContractOfferId())
                .policy(policyMapper.buildPolicy(contractRequest.getPolicyJsonLd()))
                .assetId(contractRequest.getAssetId())
                .build();
    }
}
