package de.sovity.edc.ext.wrapper.api.usecase.services;

import de.sovity.edc.ext.wrapper.api.usecase.model.ConsumptionDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ConsumptionInputDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ConsumptionOutputDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractNegotiationOutputDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.TransferProcessOutputDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractRequest;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractRequestData;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.connector.transfer.spi.types.TransferRequest;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;

@RequiredArgsConstructor
@Slf4j
public class ConsumptionService {

    private final Map<String, ConsumptionDto> consumptionProcesses = new HashMap<>(); //TODO persist?

    private final ContractNegotiationService contractNegotiationService;
    private final TransferProcessService transferProcessService;
    private final ContractNegotiationStore contractNegotiationStore;
    private final TransferProcessStore transferProcessStore;
    private final TypeTransformerRegistry transformerRegistry;
    private final PolicyMappingService policyMappingService;

    public String startConsumptionProcess(ConsumptionInputDto consumptionInputDto) {
        //TODO generate ID
        var id = "id";

        var consumeDto = new ConsumptionDto(consumptionInputDto);
        consumptionProcesses.put(id, consumeDto);

        validateInput(consumptionInputDto);

        var policy = policyMappingService.policyDtoToPolicy(consumptionInputDto.getPolicy())
                .withTarget(consumptionInputDto.getAssetId());

        var contractOffer = ContractOffer.Builder.newInstance()
                .id(consumptionInputDto.getOfferId())
                .assetId(consumptionInputDto.getAssetId())
                .policy(policy)
                .providerId("urn:connector:" + consumptionInputDto.getConnectorId())
                .build();

        var requestData = ContractRequestData.Builder.newInstance()
                .contractOffer(contractOffer)
                .dataSet(consumptionInputDto.getAssetId())
                .protocol("dataspace-protocol-http")
                .counterPartyAddress(consumptionInputDto.getConnectorAddress())
                .connectorId(consumptionInputDto.getConnectorId())
                .build();

        var contractRequest = ContractRequest.Builder.newInstance()
                .requestData(requestData)
                .build();

        var contractNegotiation = contractNegotiationService.initiateNegotiation(
                contractRequest);
        consumeDto.setContractNegotiationId(contractNegotiation.getId());

        return id;
    }

    public void negotiationConfirmed(ContractNegotiation contractNegotiation) {
        var process = findByNegotiation(contractNegotiation);

        if (process != null) {
            var agreementId = contractNegotiation.getContractAgreement().getId();

            var dataRequest = DataRequest.Builder.newInstance()
                    .id(randomUUID().toString())
                    .connectorId(process.getInput().getConnectorId())
                    .connectorAddress(process.getInput().getConnectorAddress())
                    .protocol("dataspace-protocol-http")
                    .dataDestination(process.getInput().getDataDestination())
                    .assetId(process.getInput().getAssetId())
                    .contractId(agreementId)
                    .build();

            var transferRequest = TransferRequest.Builder.newInstance()
                    .dataRequest(dataRequest)
                    .build();

            var result = transferProcessService.initiateTransfer(transferRequest);
            if (result.failed()) {
                process.getErrors().add(result.getFailureDetail());
            }

            process.setTransferProcessId(result.getContent().getId());
        }
    }

    public ConsumptionOutputDto getConsumptionProcess(String id) {
        var process = consumptionProcesses.get(id);
        if (process == null) {
            return null;
        }

        var negotiationDto = Optional.ofNullable(process.getContractNegotiationId())
                .map(contractNegotiationStore::findById)
                .map(cn -> transformerRegistry.transform(cn, ContractNegotiationOutputDto.class))
                .map(this::logIfFailedResult)
                .filter(Result::succeeded)
                .map(Result::getContent)
                .orElse(null);

        var transferProcessDto = Optional.ofNullable(process.getTransferProcessId())
                .map(transferProcessStore::findById)
                .map(tp -> transformerRegistry.transform(tp, TransferProcessOutputDto.class))
                .map(this::logIfFailedResult)
                .filter(Result::succeeded)
                .map(Result::getContent)
                .orElse(null);

        //TODO error detail
        return new ConsumptionOutputDto(id, process.getInput(), process.getErrors(),
                negotiationDto, transferProcessDto);
    }

    private void validateInput(ConsumptionInputDto input) {
        var message = "%s must not be null";

        if (input.getConnectorId() == null)
            throw new InvalidRequestException(format(message, "connectorId"));

        if (input.getConnectorAddress() == null)
            throw new InvalidRequestException(format(message, "connectorAddress"));

        if (input.getAssetId() == null)
            throw new InvalidRequestException(format(message, "assetId"));

        if (input.getOfferId() == null)
            throw new InvalidRequestException(format(message, "offerId"));

        if (input.getPolicy() == null)
            throw new InvalidRequestException(format(message, "policy"));

        if (input.getDataDestination() == null)
            throw new InvalidRequestException(format(message, "dataDestination"));
    }

    private ConsumptionDto findByNegotiation(ContractNegotiation contractNegotiation) {
        var id = contractNegotiation.getId();
        return consumptionProcesses.entrySet().stream()
                .filter(entry -> entry.getValue().getContractNegotiationId().equals(id))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private <T> Result<T> logIfFailedResult(Result<T> result) {
        if (result.failed()) {
            log.error(format("Failed to transform contract negotiation: %s",
                    result.getFailureDetail()));
        }
        return result;
    }
}
