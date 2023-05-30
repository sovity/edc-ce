package de.sovity.edc.ext.brokerserver.services.refreshing.offers;

import de.sovity.edc.ext.brokerserver.utils.MapUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiffUtils {
    /**
     * Tries to match two collections by a key, then collects planned change sets as {@link DiffResult}.
     *
     * @param existing      list of existing elements
     * @param existingKeyFn existing elements key extractor
     * @param fetched       list of fetched elements
     * @param fetchedKeyFn  fetched elements key extractor
     * @param <A>           first collection type
     * @param <B>           second collection type
     * @param <K>           key type
     */
    public static <A, B, K> DiffResult<A, B> compareLists(
            Collection<A> existing,
            Function<A, K> existingKeyFn,
            Collection<B> fetched,
            Function<B, K> fetchedKeyFn
    ) {
        var existingByKey = MapUtils.associateBy(existing, existingKeyFn);
        var fetchedByKey = MapUtils.associateBy(fetched, fetchedKeyFn);

        var keys = new HashSet<>(existingByKey.keySet());
        keys.addAll(fetchedByKey.keySet());

        var result = new DiffResult<A, B>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        keys.forEach(key -> {
            var existingItem = existingByKey.get(key);
            var fetchedItem = fetchedByKey.get(key);

            if (existingItem == null) {
                result.added.add(fetchedItem);
            } else if (fetchedItem == null) {
                result.removed.add(existingItem);
            } else {
                result.updated.add(new DiffResultMatch<>(existingItem, fetchedItem));
            }
        });

        return result;
    }

    /**
     * Result of comparing two collections by keys.
     *
     * @param added   elements that are present in fetched collection but not in existing
     * @param updated elements that are present in both collections
     * @param removed elements that are present in existing collection but not in fetched
     * @param <A>     existing item type
     * @param <B>     fetched item type
     */
    record DiffResult<A, B>(List<B> added, List<DiffResultMatch<A, B>> updated, List<A> removed) {
    }

    /**
     * Pair of elements that are present in both collections.
     *
     * @param existing existing item
     * @param fetched  fetched item
     * @param <A>      existing item type
     * @param <B>      fetched item type
     */
    record DiffResultMatch<A, B>(A existing, B fetched) {
    }
}
