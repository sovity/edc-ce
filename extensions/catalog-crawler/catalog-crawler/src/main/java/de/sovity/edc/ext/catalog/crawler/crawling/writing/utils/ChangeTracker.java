package de.sovity.edc.ext.catalog.crawler.crawling.writing.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeTracker {
    private boolean changed = false;

    public <T> void setIfChanged(
            T existing,
            T fetched,
            Consumer<T> setter
    ) {
        setIfChanged(existing, fetched, setter, Objects::equals);
    }

    public <T> void setIfChanged(
            T existing,
            T fetched,
            Consumer<T> setter,
            BiPredicate<T, T> equalityChecker
    ) {
        if (!equalityChecker.test(existing, fetched)) {
            setter.accept(fetched);
            changed = true;
        }
    }
}
