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
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.catalog.crawler.dao.connectors;


import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ConnectorRefTest {

    @Test
    void testEqualsTrue() {
        // arrange
        var a = new ConnectorRef("a", "1", "1", "1", "1");
        var b = new ConnectorRef("a", "2", "2", "2", "2");

        // act
        var result = a.equals(b);

        // assert
        assertThat(result).isTrue();
    }

    @Test
    void testEqualsFalse() {
        // arrange
        var a = new ConnectorRef("a", "1", "1", "1", "1");
        var b = new ConnectorRef("b", "1", "1", "1", "1");

        // act
        var result = a.equals(b);

        // assert
        assertThat(result).isFalse();
    }

    @Test
    void testSet() {
        // arrange
        var a = new ConnectorRef("a", "1", "1", "1", "1");
        var a2 = new ConnectorRef("a", "2", "2", "2", "2");
        var b = new ConnectorRef("b", "1", "1", "1", "1");

        // act
        var result = new HashSet<>(List.of(a, a2, b)).stream().map(ConnectorRef::getConnectorId).toList();

        // assert
        assertThat(result).containsExactlyInAnyOrder("a", "b");
    }
}
