package de.sovity.edc.ext.brokerserver.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Number of Data Offers per Connector endpoint.", requiredMode = Schema.RequiredMode.REQUIRED)
public class DataOfferCountResult {
    @Schema(description = "Map from endpoint URL to Data Offer count", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Integer> dataOfferCount;
}
