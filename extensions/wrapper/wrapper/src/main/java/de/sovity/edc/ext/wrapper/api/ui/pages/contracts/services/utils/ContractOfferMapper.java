package de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.ConstraintExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.MappingErrors;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.PolicyValidator;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyDto;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractOfferDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;

import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractRequest;;


@RequiredArgsConstructor
public class ContractOfferMapper {

    /**
     * This Object Mapper must be able to handle JSON-LD serialization / deserialization.
     */
    private final ObjectMapper objectMapper;

    public ContractOffer buildContractOffer(ContractOfferDto contractRequest) throws JsonProcessingException {

        return ContractOffer.Builder.newInstance()
                .policy(objectMapper.readValue(contractRequest.getPolicyJsonLd(), Policy.class))
                .assetId(contractRequest.getAssetId())
                .build();
    }
}
