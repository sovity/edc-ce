package de.sovity.edc.ext.wrapper.api.usecase.model;

import lombok.Data;

import java.util.Map;

@Data
public class TransferProcessDto {
    private Map<Integer, Integer> incomingTransferProcessCounts;
    private Map<Integer, Integer> outgoingTransferProcessCounts;
}
