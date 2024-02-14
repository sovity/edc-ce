package de.sovity.edc.ext.wrapper.api.ui.pages.catalog;

import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.utils.catalog.DspCatalogService;
import de.sovity.edc.utils.catalog.DspCatalogServiceException;
import de.sovity.edc.utils.catalog.mapper.DspDataOfferBuilder;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.val;
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CatalogApiServiceTest {
    @Test
    void testDontLetTheWrongExceptionsBubbleUp() throws Exception {
        // arrange
        val catalogApiService = mock(DspCatalogService.class);
        when(catalogApiService.fetchDataOffers(anyString())).thenThrow(DspCatalogServiceException.class);

        val service = new CatalogApiService(mock(AssetMapper.class), mock(PolicyMapper.class), catalogApiService);

        // act
        val exception = assertThrows(WebApplicationException.class, () -> service.fetchDataOffers("http:example.com"));

        // assert
        assertThat(exception.getResponse().getStatus()).isEqualTo(Response.Status.BAD_GATEWAY.getStatusCode());
    }
}
