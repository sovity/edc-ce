package de.sovity.edc.ext.wrapper.api.usecase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * A DTO Class for DataRequests
 *
 * @author Haydar Qarawlus
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DataRequestDto {
    private String id;
    private String processId;
    private String connectorAddress;
    private String protocol;
    private String connectorId;
    private String assetId;
    private String contractId;
    private Map<String, String> dataDestination;
}
