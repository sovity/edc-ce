package de.sovity.edc.ext.wrapper.utils;

import java.util.List;

public interface FetchBatch<T> {
    List<T> fetchBatch(int offset, int batchSize);
}
