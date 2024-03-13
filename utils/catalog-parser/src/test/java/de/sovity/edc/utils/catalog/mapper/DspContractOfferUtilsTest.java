package de.sovity.edc.utils.catalog.mapper;

import jakarta.json.Json;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DspContractOfferUtilsTest {
    @Disabled("hotfix: the stable ID or the way the new ID is build prevents a contract negotiation.")
    @Test
    void testCanConvertTheRandomIdToStableId() {
        // arrange
        val contractOffer = Json.createObjectBuilder()
                .add("@id", "part1:part2:part3")
                .add("somefield", "somevalue")
                .build();

        // act
        val result = DspContractOfferUtils.buildStableId(contractOffer);

        // assert
        assertThat(result).isEqualTo("part1:part2:MjliNzcwMjdjMzA2YzE3ZGYyZWNhZjY2OGI3OTYxY2U5OTY1YmExNw==");
    }
}
