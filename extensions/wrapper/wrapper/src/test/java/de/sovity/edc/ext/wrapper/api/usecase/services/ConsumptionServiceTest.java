package de.sovity.edc.ext.wrapper.api.usecase.services;

import de.sovity.edc.ext.wrapper.api.usecase.model.ConsumptionInputDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ConsumptionDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractNegotiationOutputDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.TransferProcessOutputDto;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractRequest;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.transfer.spi.types.TransferRequest;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.service.spi.result.ServiceResult;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ConsumptionServiceTest {

    private static final String counterPartyId = "counterPartyId";
    private static final String counterPartyAddress = "http://localhost";
    private static final String assetId = "assetId";
    private static final String offerId = "offerId";
    private static final String negotiationId = "negotiationId";
    private static final String transferProcessId = "transferProcessId";
    private static final String agreementId = "agreementId";

    private final ContractNegotiationService negotiationService = mock(ContractNegotiationService.class);
    private final TransferProcessService transferProcessService = mock(TransferProcessService.class);
    private final ContractNegotiationStore negotiationStore = mock(ContractNegotiationStore.class);
    private final TransferProcessStore transferProcessStore = mock(TransferProcessStore.class);
    private final TypeTransformerRegistry transformerRegistry = mock(TypeTransformerRegistry.class);

    private ConsumptionService consumptionService;

    @BeforeEach
    void setUp() {
        consumptionService = new ConsumptionService(negotiationService, transferProcessService,
                negotiationStore, transferProcessStore, transformerRegistry);
    }

    @Test
    void startConsumptionProcess_shouldInitiateNegotiation_whenValidInput() {
        // ARRANGE
        var captor = ArgumentCaptor.forClass(ContractRequest.class);
        when(negotiationService.initiateNegotiation(captor.capture()))
                .thenReturn(negotiation());

        var input = ConsumptionInputDto.builder()
                .connectorId(counterPartyId)
                .connectorAddress(counterPartyAddress)
                .assetId(assetId)
                .offerId(offerId)
                .policy(policy())
                .dataDestination(dataAddress())
                .build();

        // ACT
        var processId = consumptionService.startConsumptionProcess(input);

        // ASSERT
        assertThat(processId).isNotNull();

        var requestData = captor.getValue().getRequestData();
        assertThat(requestData.getCounterPartyAddress()).isEqualTo(counterPartyAddress);
        assertThat(requestData.getDataSet()).isEqualTo(assetId);
        assertThat(requestData.getContractOffer().getPolicy()).isEqualTo(input.getPolicy());
        assertThat(requestData.getContractOffer().getAssetId()).isEqualTo(assetId);
        assertThat(requestData.getContractOffer().getProviderId()).contains(counterPartyId);
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidInputArgumentsProvider.class)
    void startConsumptionProcess_shouldThrowException_whenInvalidInput(String connectorId,
                                                                       String connectorAddress,
                                                                       String requestedAssetId,
                                                                       String contractOfferId,
                                                                       Policy policy,
                                                                       DataAddress destination) {
        // ARRANGE
        var input = ConsumptionInputDto.builder()
                .connectorId(connectorId)
                .connectorAddress(connectorAddress)
                .assetId(requestedAssetId)
                .offerId(contractOfferId)
                .policy(policy)
                .dataDestination(destination)
                .build();

        // ACT & ASSERT
        assertThatThrownBy(() -> consumptionService.startConsumptionProcess(input))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void negotiationConfirmed_shouldInitiateTransfer_whenProcessExists() throws Exception {
        // ARRANGE
        var process = consumptionDto(negotiationId, null);
        insertConsumptionProcess("id", process);

        var captor = ArgumentCaptor.forClass(TransferRequest.class);
        when(transferProcessService.initiateTransfer(captor.capture()))
                .thenReturn(ServiceResult.success(transferProcess()));

        // ACT
        consumptionService.negotiationConfirmed(negotiation());

        // ASSERT
        verify(transferProcessService, times(1)).initiateTransfer(any());

        var dataRequest = captor.getValue().getDataRequest();
        assertThat(dataRequest.getAssetId()).isEqualTo(assetId);
        assertThat(dataRequest.getContractId()).isEqualTo(agreementId);
        assertThat(dataRequest.getConnectorId()).isEqualTo(counterPartyId);
        assertThat(dataRequest.getConnectorAddress()).isEqualTo(counterPartyAddress);
        assertThat(dataRequest.getDataDestination())
                .isEqualTo(process.getInput().getDataDestination());

        assertThat(process.getTransferProcessId())
                .isNotNull()
                .isEqualTo(transferProcessId);
    }

    @Test
    void negotiationConfirmed_shouldDoNothing_whenProcessNotFound() {
        // ACT
        consumptionService.negotiationConfirmed(negotiation());

        // ASSERT
        verifyNoInteractions(transferProcessService);
    }

    @Test
    void getConsumptionProcess_shouldReturnNull_whenProcessNotFound() {
        // ACT
        var output = consumptionService.getConsumptionProcess("id");

        // ASSERT
        assertThat(output).isNull();
    }

    @Test
    void getConsumptionProcess_shouldReturnProcess_whenNoIdsSet() throws Exception {
        // ARRANGE
        var id = "id";

        var process = consumptionDto(null, null);
        insertConsumptionProcess(id, process);

        // ACT
        var output = consumptionService.getConsumptionProcess(id);

        // ASSERT
        assertThat(output).isNotNull();
        assertThat(output.getContractNegotiation()).isNull();
        assertThat(output.getTransferProcess()).isNull();
    }

    @Test
    void getConsumptionProcess_shouldReturnProcess_whenIdsSet() throws Exception {
        // ARRANGE
        var id = "id";

        var process = consumptionDto(negotiationId, transferProcessId);
        insertConsumptionProcess(id, process);

        var negotiationOutput = negotiationOutputDto();
        var transferProcessOutput = transferProcessOutputDto();

        when(negotiationStore.findById(any())).thenReturn(negotiation());
        when(transferProcessStore.findById(any())).thenReturn(transferProcess());
        when(transformerRegistry.transform(any(ContractNegotiation.class),
                eq(ContractNegotiationOutputDto.class)))
                .thenReturn(Result.success(negotiationOutput));
        when(transformerRegistry.transform(any(TransferProcess.class),
                eq(TransferProcessOutputDto.class)))
                .thenReturn(Result.success(transferProcessOutput));

        // ACT
        var output = consumptionService.getConsumptionProcess(id);

        // ASSERT
        assertThat(output).isNotNull();
        assertThat(output.getContractNegotiation())
                .isNotNull()
                .isEqualTo(negotiationOutput);
        assertThat(output.getTransferProcess())
                .isNotNull()
                .isEqualTo(transferProcessOutput);
    }

    @Test
    void getConsumptionProcess_shouldReturnProcess_whenNegotiationNotFound() throws Exception {
        // ARRANGE
        var id = "id";

        var process = consumptionDto(negotiationId, transferProcessId);
        insertConsumptionProcess(id, process);

        var transferProcessOutput = transferProcessOutputDto();

        when(negotiationStore.findById(any())).thenReturn(null);
        when(transferProcessStore.findById(any())).thenReturn(transferProcess());
        when(transformerRegistry.transform(any(TransferProcess.class),
                eq(TransferProcessOutputDto.class)))
                .thenReturn(Result.success(transferProcessOutput));

        // ACT
        var output = consumptionService.getConsumptionProcess(id);

        // ASSERT
        assertThat(output).isNotNull();
        assertThat(output.getContractNegotiation()).isNull();
        assertThat(output.getTransferProcess())
                .isNotNull()
                .isEqualTo(transferProcessOutput);
    }

    @Test
    void getConsumptionProcess_shouldReturnProcess_whenTransferNotFound() throws Exception {
        // ARRANGE
        var id = "id";

        var process = consumptionDto(negotiationId, transferProcessId);
        insertConsumptionProcess(id, process);

        var negotiationOutput = negotiationOutputDto();

        when(negotiationStore.findById(any())).thenReturn(negotiation());
        when(transferProcessStore.findById(any())).thenReturn(null);
        when(transformerRegistry.transform(any(ContractNegotiation.class),
                eq(ContractNegotiationOutputDto.class)))
                .thenReturn(Result.success(negotiationOutput));

        // ACT
        var output = consumptionService.getConsumptionProcess(id);

        // ASSERT
        assertThat(output).isNotNull();
        assertThat(output.getContractNegotiation())
                .isNotNull()
                .isEqualTo(negotiationOutput);
        assertThat(output.getTransferProcess()).isNull();
    }

    @Test
    void getConsumptionProcess_shouldReturnProcess_whenNegotiationTransformationFails()
            throws Exception {
        // ARRANGE
        var id = "id";

        var process = consumptionDto(negotiationId, transferProcessId);
        insertConsumptionProcess(id, process);

        var transferProcessOutput = transferProcessOutputDto();

        when(negotiationStore.findById(any())).thenReturn(negotiation());
        when(transferProcessStore.findById(any())).thenReturn(transferProcess());
        when(transformerRegistry.transform(any(ContractNegotiation.class),
                eq(ContractNegotiationOutputDto.class))).thenReturn(Result.failure("error"));
        when(transformerRegistry.transform(any(TransferProcess.class),
                eq(TransferProcessOutputDto.class)))
                .thenReturn(Result.success(transferProcessOutput));

        // ACT
        var output = consumptionService.getConsumptionProcess(id);

        // ASSERT
        assertThat(output).isNotNull();
        assertThat(output.getContractNegotiation()).isNull();
        assertThat(output.getTransferProcess())
                .isNotNull()
                .isEqualTo(transferProcessOutput);
    }

    @Test
    void getConsumptionProcess_shouldReturnProcess_whenTransferTransformationFails()
            throws Exception {
        // ARRANGE
        var id = "id";

        var process = consumptionDto(negotiationId, transferProcessId);
        insertConsumptionProcess(id, process);

        var negotiationOutput = negotiationOutputDto();

        when(negotiationStore.findById(any())).thenReturn(negotiation());
        when(transferProcessStore.findById(any())).thenReturn(transferProcess());
        when(transformerRegistry.transform(any(ContractNegotiation.class),
                eq(ContractNegotiationOutputDto.class)))
                .thenReturn(Result.success(negotiationOutput));
        when(transformerRegistry.transform(any(TransferProcess.class),
                eq(TransferProcessOutputDto.class))).thenReturn(Result.failure("error"));

        // ACT
        var output = consumptionService.getConsumptionProcess(id);

        // ASSERT
        assertThat(output).isNotNull();
        assertThat(output.getContractNegotiation())
                .isNotNull()
                .isEqualTo(negotiationOutput);
        assertThat(output.getTransferProcess()).isNull();
    }

    private ContractNegotiation negotiation() {
        return ContractNegotiation.Builder.newInstance()
                .id(negotiationId)
                .type(ContractNegotiation.Type.CONSUMER)
                .counterPartyId("counterPartyId")
                .counterPartyAddress("http://localhost")
                .protocol("protocol")
                .contractAgreement(ContractAgreement.Builder.newInstance()
                        .id(agreementId)
                        .assetId(assetId)
                        .policy(policy())
                        .providerId("provider")
                        .consumerId("consumer")
                        .build())
                .build();
    }

    private static Policy policy() {
        return Policy.Builder.newInstance()
                .permission(Permission.Builder.newInstance()
                        .action(Action.Builder.newInstance()
                                .type("use")
                                .build())
                        .target(assetId)
                        .build())
                .target(assetId)
                .build();
    }

    private static DataAddress dataAddress() {
        return DataAddress.Builder.newInstance()
                .type("test")
                .build();
    }

    private static TransferProcess transferProcess() {
        return TransferProcess.Builder.newInstance()
                .id(transferProcessId)
                .build();
    }

    private static ContractNegotiationOutputDto negotiationOutputDto() {
        return ContractNegotiationOutputDto.builder().build();
    }

    private static TransferProcessOutputDto transferProcessOutputDto() {
        return TransferProcessOutputDto.builder().build();
    }

    private static ConsumptionDto consumptionDto(String negotiationId, String transferProcessId) {
        var destination = dataAddress();
        var input = ConsumptionInputDto.builder()
                .connectorId(counterPartyId)
                .connectorAddress(counterPartyAddress)
                .assetId(assetId)
                .offerId(offerId)
                .policy(policy())
                .dataDestination(destination)
                .build();
        var consumptionDto = new ConsumptionDto(input);
        consumptionDto.setContractNegotiationId(negotiationId);
        consumptionDto.setTransferProcessId(transferProcessId);

        return consumptionDto;
    }

    private void insertConsumptionProcess(String id, ConsumptionDto process) throws Exception {
        var processes = Map.of(id, process);
        var field = ConsumptionService.class.getDeclaredField("consumptionProcesses");
        field.setAccessible(true);
        field.set(consumptionService, processes);
    }

    private static class InvalidInputArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(null, counterPartyAddress, assetId, offerId, policy(),
                            dataAddress()),
                    Arguments.of(counterPartyId, null, assetId, offerId, policy(),
                            dataAddress()),
                    Arguments.of(counterPartyId, counterPartyAddress, null, offerId, policy(),
                            dataAddress()),
                    Arguments.of(counterPartyId, counterPartyAddress, assetId, null, policy(),
                            dataAddress()),
                    Arguments.of(counterPartyId, counterPartyAddress, assetId, offerId, null,
                            dataAddress()),
                    Arguments.of(counterPartyId, counterPartyAddress, assetId, offerId, policy(),
                            null)
            );
        }
    }

}
