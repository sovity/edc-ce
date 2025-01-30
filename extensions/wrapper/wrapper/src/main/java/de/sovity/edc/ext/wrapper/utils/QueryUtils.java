package de.sovity.edc.ext.wrapper.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class QueryUtils {
    public @NotNull <T> ArrayList<T> fetchInBatches(FetchBatch<T> fetcher) {
        val batchSize = 1000;
        var position = 0;
        val all = new ArrayList<T>();

        List<T> batch;

        do {
            batch = fetcher.fetchBatch(position, batchSize);

            all.addAll(batch);

            position += batchSize;
        } while (!batch.isEmpty());

        return all;
    }
}
