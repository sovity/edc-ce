package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.ContractAgreementDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.eclipse.edc.transform.spi.TypeTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContractAgreementToContractAgreementDtoTransformer implements TypeTransformer<ContractAgreement, ContractAgreementDto> {

    @Override
    public Class<ContractAgreement> getInputType() {
        return ContractAgreement.class;
    }

    @Override
    public Class<ContractAgreementDto> getOutputType() {
        return ContractAgreementDto.class;
    }

    @Override
    public @Nullable ContractAgreementDto transform(@NotNull ContractAgreement contractAgreement, @NotNull TransformerContext context) {
        var policy = context.transform(contractAgreement.getPolicy(), PolicyDto.class);

        if (policy == null) {
            context.problem().nullProperty().type(ContractAgreement.class).property("Policy").report();
            return null;
        }

        return ContractAgreementDto.builder()
                .id(contractAgreement.getId())
                .providerId(contractAgreement.getProviderId())
                .consumerId(contractAgreement.getConsumerId())
                .assetId(contractAgreement.getAssetId())
                .contractSigningDate(contractAgreement.getContractSigningDate())
                .policy(policy)
                .build();
    }
}
