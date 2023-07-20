package de.sovity.edc.client;

import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.text.ParseException;
import java.util.Map;

import static de.sovity.edc.ext.wrapper.utils.EdcDateUtils.dateFormatterToLong;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ApiTest
@ExtendWith(EdcExtension.class)
class GetAssetApiServiceTest {

    public static final String DATA_SINK = "http://my-data-sink/api/stuff";

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(TestUtils.createConfiguration(Map.of()));
    }

    @Test
    void testGetAssetApiService(AssetService assetStore) throws ParseException {

        var client = TestUtils.edcClient();

        //arrange data
        DataAddress dataAddress = getDataAddress();
        createAsset(assetStore, dataAddress, "urn:artifact:test-asset-1", "test-asset-1", "2023-07-18");
        createAsset(assetStore, dataAddress, "urn:artifact:test-asset-2", "test-asset-2", "2023-07-20");

        //act
        var asset = client.uiApi().assetPageEndpoint();

        //assert
        assertThat(asset.get(0).getAssetId()).isEqualTo("urn:artifact:test-asset-1");
        assertThat(asset.get(0).getProperties().get("asset:prop:name")).isEqualTo("test-asset-1");
        assertThat(asset.get(1).getAssetId()).isEqualTo("urn:artifact:test-asset-2");
        assertThat(asset.get(1).getProperties().get("asset:prop:name")).isEqualTo("test-asset-2");
    }

    private static DataAddress getDataAddress() {
        var dataAddress = DataAddress.Builder.newInstance()
                .type("HttpData")
                .property("baseUrl", DATA_SINK)
                .build();
        return dataAddress;
    }

    private static void createAsset(AssetService assetStore, DataAddress dataAddress, String assetId, String assetName, String createdDate) throws ParseException {
        var asset = Asset.Builder.newInstance()
                .id(assetId)
                .property("asset:prop:name", assetName)
                .createdAt(dateFormatterToLong(createdDate))
                .build();
        assetStore.create(asset, dataAddress);
    }
}
