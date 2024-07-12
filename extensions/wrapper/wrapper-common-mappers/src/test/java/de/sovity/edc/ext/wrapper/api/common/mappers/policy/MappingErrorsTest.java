/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.common.mappers.policy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MappingErrorsTest {


    @Test
    void testMappingErrors() {
        var parent = MappingErrors.root();
        parent.add("a");

        var childObj = parent.forChildObject("child");
        childObj.add("b");

        var childObjArray = childObj.forChildArrayElement(3);
        childObjArray.add("c");

        assertThat(parent.getErrors()).containsExactly(
                "$: a",
                "$.child: b",
                "$.child[3]: c"
        );
    }
}
