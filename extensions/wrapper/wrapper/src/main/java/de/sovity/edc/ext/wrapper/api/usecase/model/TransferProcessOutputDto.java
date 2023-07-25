package de.sovity.edc.ext.wrapper.api.usecase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to store the TransferProcessOutput
 *
 * @author Haydar Qarawlus
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TransferProcessOutputDto {
    private String id;
    private String state;
    private DataRequestDto dataRequest;
    private Map<String, String> contentDataAddress;
    private Map<String, String> privateProperties = new HashMap<>();
    private String errorDetail;
}
