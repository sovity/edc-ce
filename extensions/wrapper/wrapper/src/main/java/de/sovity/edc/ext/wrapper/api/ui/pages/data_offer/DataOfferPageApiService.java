package de.sovity.edc.ext.wrapper.api.ui.pages.data_offer;

import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.ext.wrapper.api.ui.model.IdAvailabilityResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;

@RequiredArgsConstructor
public class DataOfferPageApiService {
    @NotNull
    public IdAvailabilityResponse checkIfPolicyIdAvailable(DSLContext dsl, String id) {
        val table = Tables.EDC_POLICYDEFINITIONS;
        val field = table.POLICY_ID;

        return new IdAvailabilityResponse(id, isIdAvailable(dsl, table, field, id));
    }

    @NotNull
    public IdAvailabilityResponse checkIfAssetIdAvailable(DSLContext dsl, String id) {
        val table = Tables.EDC_ASSET;
        val field = table.ASSET_ID;

        return new IdAvailabilityResponse(id, isIdAvailable(dsl, table, field, id));
    }

    @NotNull
    public IdAvailabilityResponse checkIfContractDefinitionIdAvailable(DSLContext dsl, String id) {
        val table = Tables.EDC_CONTRACT_DEFINITIONS;
        val field = table.CONTRACT_DEFINITION_ID;

        return new IdAvailabilityResponse(id, isIdAvailable(dsl, table, field, id));
    }

    private boolean isIdAvailable(DSLContext dsl, Table<?> table, TableField<?, String> idField, String id) {
        return !dsl.fetchExists(
        dsl.select(idField)
            .from(table)
            .where(idField.eq(id))
            );
    }
}
