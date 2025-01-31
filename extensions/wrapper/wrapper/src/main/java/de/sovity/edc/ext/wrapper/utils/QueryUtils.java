package de.sovity.edc.ext.wrapper.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class QueryUtils {
    public static final int BATCH_SIZE = 4000;

    public @NotNull <T> ArrayList<T> fetchAllInBatches(FetchBatch<T> fetcher) {
        var position = 0;
        val all = new ArrayList<T>();

        List<T> batch;

        do {
            batch = fetcher.fetchBatch(position, BATCH_SIZE);
            all.addAll(batch);
            position += BATCH_SIZE;
        } while (!batch.isEmpty());

        System.out.println("ALL   " + all.size());

        return all;
    }
}
