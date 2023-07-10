package de.sovity.edc.ext.brokerserver.dao.pages.dataoffer;

import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;

public class ViewCountLogger {
    public void increaseDataOfferViewCount(DSLContext dsl, String assetId, String endpoint) {
        var v = Tables.DATA_OFFER_VIEW_COUNT;
        dsl.insertInto(v, v.ASSET_ID, v.CONNECTOR_ENDPOINT, v.DATE).values(assetId, endpoint, OffsetDateTime.now()).execute();
    }
}
