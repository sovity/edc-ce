/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.ext.brokerserver.services.refreshing.offers;

import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DiffUtils.DiffResultMatch;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class DiffUtilsTest {

    @Test
    void testCompareLists() {
        // arrange
        List<Integer> existing = List.of(1, 2);
        List<String> fetched = List.of("1", "3");

        // act
        var actual = DiffUtils.compareLists(existing, Function.identity(), fetched, Integer::parseInt);

        // assert
        assertThat(actual.added()).containsExactly("3");
        assertThat(actual.updated()).containsExactly(new DiffResultMatch<>(1, "1"));
        assertThat(actual.removed()).containsExactly(2);
    }
}
