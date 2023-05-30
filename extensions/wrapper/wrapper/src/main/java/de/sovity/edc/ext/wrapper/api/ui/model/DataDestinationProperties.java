package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Data Destination Properties of Data Transfer")
public class DataDestinationProperties {

    @Schema(description = "Base Url", requiredMode = Schema.RequiredMode.REQUIRED)
    private String baseUrl;

    @Schema(description = "EDC Receiver HTTP Dynamic Auth Code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String edcReceiverHttpDynamicAuthCode;

    @Schema(description = "EDC Receiver HTTP Dynamic Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String edcReceiverHttpDynamicEndpoint;

    @Schema(description = "EDC Receiver HTTP Dynamic Auth Key", requiredMode = Schema.RequiredMode.REQUIRED)
    private String edcReceiverHttpDynamicAuthKey;

    @Schema(description = "Method Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String method;

    @Schema(description = "EDC Receiver HTTP Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiverHttpEndpoint;

    @Schema(description = "Method Type", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

}
