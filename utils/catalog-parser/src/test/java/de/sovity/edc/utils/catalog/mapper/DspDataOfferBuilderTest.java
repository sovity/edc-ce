package de.sovity.edc.utils.catalog.mapper;

import jakarta.json.Json;
import lombok.val;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.spi.monitor.Monitor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

class DspDataOfferBuilderTest {
    @Test
    void testCanConvertTheRandomIdToStableId() {
        // arrange
        val contractOffer = Json.createObjectBuilder()
                .add("@id", "part1:part2:part3")
                .add("somefield", "somevalue")
                .build();

        // act
        val monitor = mock(Monitor.class);
        val result = new DspDataOfferBuilder(new TitaniumJsonLd(monitor), monitor).buildContractOffer(contractOffer);

        // assert
        val stableId = "part1:part2:KbdwJ8MGwX3y7K9mi3lhzplluhc=";
        assertThat(result.getContractOfferId()).isEqualTo(stableId);
        assertThat(result.getPolicyJsonLd().getString("@id")).isEqualTo(stableId);
    }
}
